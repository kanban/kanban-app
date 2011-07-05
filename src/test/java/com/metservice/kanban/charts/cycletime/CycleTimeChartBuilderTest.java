package com.metservice.kanban.charts.cycletime;

import static com.metservice.kanban.utils.DateUtils.parseConventionalNewZealandDate;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jfree.data.category.CategoryDataset;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class CycleTimeChartBuilderTest {

    @Test
    public void testFilterNoMatches() {
        WorkItemType type = new WorkItemType("phase1", "phase2");
        
        Collection<WorkItem> workItemList = new ArrayList<WorkItem>();
        workItemList.add(new WorkItem(1, type));
        workItemList.add(new WorkItem(2, type));
        workItemList.add(new WorkItem(3, type));
        

        CycleTimeChartBuilder builder = new CycleTimeChartBuilder();
        Collection<WorkItem> result = builder.getCompletedWorkItemsInOrderOfCompletion(workItemList);
        assertThat(result.size(), is(0));
    }

    @Test
    public void testFilteringAndOrdering() throws ParseException {

        List<WorkItem> workItems = buildList();

        CycleTimeChartBuilder builder = new CycleTimeChartBuilder();
        Collection<WorkItem> result = builder.getCompletedWorkItemsInOrderOfCompletion(workItems);
        assertThat(result.size(), is(2));
        Iterator<WorkItem> iterator = result.iterator();
        assertThat(iterator.next().getId(), is(2));
        assertThat(iterator.next().getId(), is(1));
    }

    @Test
    public void createsCorrectDataset() throws IOException, ParseException {
        CycleTimeChartBuilder builder = new CycleTimeChartBuilder();
        Collection<WorkItem> workItems = builder.getCompletedWorkItemsInOrderOfCompletion(buildList());
        CategoryDataset dataset = builder.createDataset(workItems);

        assertThat(getNumberOfDaysInPhase(dataset, "1", "phase2"), is(2));
        assertThat(getNumberOfDaysInPhase(dataset, "2", "phase2"), is(3));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void excludedItemsShouldnBeUsed() throws IOException, ParseException {
        CycleTimeChartBuilder builder = new CycleTimeChartBuilder();
        Collection<WorkItem> list = buildList();
        for(WorkItem item: list) {
            if (item.getId() == 2) {
                item.setExcluded(true);
            }
        }
        
        Collection<WorkItem> workItems = builder.getCompletedWorkItemsInOrderOfCompletion(list);
        
        
        CategoryDataset dataset = builder.createDataset(workItems);

        getNumberOfDaysInPhase(dataset, "2", "phase2");
    }
    

    private int getNumberOfDaysInPhase(CategoryDataset dataset, String id, String phase) {
        return dataset.getValue(phase, id).intValue();
    }

    private List<WorkItem> buildList() throws ParseException {
        List<WorkItem> workItems = new ArrayList<WorkItem>();
        WorkItemType type = new WorkItemType("phase1", "phase2", "phase3");

        WorkItem workItem1 = new WorkItem(1, type);
        WorkItem workItem2 = new WorkItem(2, type);
        WorkItem workItem3 = new WorkItem(3, type);
        
        workItem1.setDate("phase1", parseConventionalNewZealandDate("13/05/2011"));
        workItem1.setDate("phase2", parseConventionalNewZealandDate("18/05/2011"));
        workItem1.setDate("phase3", parseConventionalNewZealandDate("20/05/2011"));

        workItem2.setDate("phase1", parseConventionalNewZealandDate("12/05/2011"));
        workItem2.setDate("phase2", parseConventionalNewZealandDate("15/05/2011"));
        workItem2.setDate("phase3", parseConventionalNewZealandDate("19/05/2011"));

        workItem3.setDate("phase1", parseConventionalNewZealandDate("11/05/2011"));

        workItems.add(workItem1);
        workItems.add(workItem2);
        workItems.add(workItem3);
        return workItems;
    }
}
