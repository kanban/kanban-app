package com.metservice.kanban.charts.burnup;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.core.IsAnything;
import org.joda.time.LocalDate;
import com.google.gson.internal.Pair;
import com.metservice.kanban.charts.ChartUtils;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.utils.DateUtils;

public class EstimatesBurnUpDataModel {

    private static final IsAnything<String> IS_ANY_PHASE_NAME = new IsAnything<String>();

    private final WorkItemType workItemType;
    private final List<WorkItem> workItems;
    private final LocalDate endDate;
    private final LocalDate startDate;
    private final EstimatesProject estimatesProject;

    public EstimatesBurnUpDataModel(WorkItemType workItemType, List<WorkItem> workItems, LocalDate endDate,
                                    EstimatesProject estimatesProject) {
        this.workItemType = workItemType;
        this.workItems = removeExcludedWorkItems(workItems);
        if (endDate == null) {
            endDate = DateUtils.currentLocalDate();
        }
        this.startDate = ChartUtils.getFirstDate(workItems);
        this.endDate = endDate;
        this.estimatesProject = estimatesProject;
    }

    public EstimatesBurnUpDataModel(WorkItemType workItemType, List<WorkItem> workItems, LocalDate startDate,
                                    LocalDate endDate, EstimatesProject estimatesProject) {
        this.workItemType = workItemType;
        this.workItems = removeExcludedWorkItems(workItems);

        if (startDate == null) {
            startDate = ChartUtils.getFirstDate(workItems);

            if (endDate == null) {
                endDate = DateUtils.currentLocalDate();
            }

            if (startDate == null) {
                startDate = endDate;
            }
        }
        this.startDate = startDate;
        this.endDate = endDate;
        this.estimatesProject = estimatesProject;
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

    public List<Pair<Integer, LocalDate>> getBudgetEntries() {
        List<Pair<Integer, LocalDate>> result = new ArrayList<Pair<Integer, LocalDate>>();

        int currentBudget = 0;

        for (LocalDate day : estimatesProject.getDayCosts().keySet()) {
            Integer integer = estimatesProject.getDayCosts().get(day);
            currentBudget += integer;
            result.add(new Pair<Integer, LocalDate>(currentBudget, day));
        }

        return result;
    }

    public int getAllFeaturePoints() {
        int points = 0;
        for (WorkItem wi : workItems) {
            points += wi.getAverageCaseEstimate();
        }
        return points;
    }

    public int getRemainingFeaturePointForBudget(Pair<Integer, LocalDate> budgetEntry) {
        int points = getAllFeaturePoints();

        for (WorkItem wi : workItems) {
            if (wi.isCompleted() && !wi.getDate(wi.getCurrentPhase()).isAfter(budgetEntry.second)) {
                points -= wi.getAverageCaseEstimate();
            }
        }
        return points;
    }

    public int getProjectedEndOfMoneyPoints() {
        return 2;
    }

    public Integer getProjectedBudgetConsumed() {
        return 90;
    }

    public Integer getBudget() {
        return estimatesProject.getBudget();
    }

}
