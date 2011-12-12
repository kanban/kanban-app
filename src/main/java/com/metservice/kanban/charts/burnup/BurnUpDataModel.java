package com.metservice.kanban.charts.burnup;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.joda.time.LocalDate;
import com.metservice.kanban.charts.ChartUtils;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.utils.DateUtils;

public class BurnUpDataModel {

    private static final IsAnything<String> IS_ANY_PHASE_NAME = new IsAnything<String>();

    private final WorkItemType workItemType;
    private final List<WorkItem> workItems;
    private final LocalDate endDate;
    private final LocalDate startDate;

    public BurnUpDataModel(WorkItemType workItemType, List<WorkItem> workItems, LocalDate endDate) {
        this.workItemType = workItemType;
        this.workItems = removeExcludedWorkItems(workItems);
		if (endDate == null){
			endDate = DateUtils.currentLocalDate();
		}
    	this.startDate = ChartUtils.getFirstDate(workItems);
		this.endDate = endDate;
    }

    
    public BurnUpDataModel(WorkItemType workItemType, List<WorkItem> workItems, LocalDate startDate, LocalDate endDate) {
        this.workItemType = workItemType;
        this.workItems = removeExcludedWorkItems(workItems);
        if (startDate == null){
        	startDate = ChartUtils.getFirstDate(workItems);
        	
			if (endDate == null){
				endDate = DateUtils.currentLocalDate();
			}

            if (startDate == null) {
                startDate = endDate;
            }
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private static List<WorkItem> removeExcludedWorkItems(List<WorkItem> workItemList) {
        List<WorkItem> list = new ArrayList<WorkItem>();
        for (WorkItem item : workItemList) {
            if (!item.isExcluded()) {
                list.add(item);
            }
        }
        return list;
    }
    
    public LocalDate getCurrentDate() {
        return endDate;
    }

    public List<LocalDate> getWorkingDays() {
        return ChartUtils.getWorkingDaysForWorkItems(workItems, startDate, endDate);
    }

    public int getBacklogSizeOnDate(LocalDate date) {
        return sumWorkItemSizesWherePhase(is(workItemType.getBacklogPhase()), workItems, date);
    }

    public int getCompletedSizeOnDate(LocalDate date) {
        return sumWorkItemSizesWherePhase(is(workItemType.getCompletedPhase()), workItems, date);
    }
    
    public int getTotalSizeOnDate(LocalDate date) {
        return sumWorkItemSizesWherePhase(IS_ANY_PHASE_NAME, workItems, date);
    }

    public int getInProgressSizeOnDate(LocalDate date) {
        Matcher<String> inProgress = not(anyOf(
            nullValue(String.class),
            is(workItemType.getBacklogPhase()),
            is(workItemType.getCompletedPhase())));
        return sumWorkItemSizesWherePhase(inProgress, workItems, date);
    }

    private static int sumWorkItemSizesWherePhase(Matcher<String> phaseMatcher,
            Iterable<WorkItem> workItems, LocalDate onDate) {
        int sum = 0;
        for (WorkItem workItem : workItems) {
            if (phaseMatcher.matches(workItem.getPhaseOnDate(onDate))) {
                sum += workItem.getAverageCaseEstimate();
            }
        }
        return sum;
    }
}
