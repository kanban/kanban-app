package com.metservice.kanban.charts.burnup;

import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.BACKLOG;
import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.COMPLETE;
import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.jfree.data.category.CategoryDataset;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class BurnUpDatasetGeneratorTest {

    private static final LocalDate DATE = parseIsoDate("2011-06-13");
    private static final WorkItemType WORK_ITEM_TYPE = new WorkItemType("backlog", "in progress", "completed");
    
    private BurnUpDatasetGenerator datasetFactory;

    @Before
    public void before() {
        datasetFactory = new BurnUpDatasetGenerator();
    }

    @Test
    public void datasetContainsHistoricValues() {
        WorkItem workItem = newBacklogItemWithSize(7);
        
        BurnUpDataModel model = new BurnUpDataModel(WORK_ITEM_TYPE, asList(workItem), DATE);
        CategoryDataset data = datasetFactory.createDataset(model);

        assertThat(data.getValue(BACKLOG, DATE).intValue(), is(7));
    }
    
    @Test
    public void datasetContainsProjectedValues() {
        WorkItem workItem1 = newBacklogItemWithSize(1);
        workItem1.advance(DATE);
        workItem1.advance(DATE);

        WorkItem workItem2 = newBacklogItemWithSize(1);
        
        BurnUpDataModel model = new BurnUpDataModel(WORK_ITEM_TYPE, asList(workItem1, workItem2), null, DATE.plusDays(1));
        CategoryDataset data = datasetFactory.createDataset(model);
        
        assertThat(data.getValue(COMPLETE, DATE.plusDays(2)).intValue(), is(2));
    }
    
    @Test
    public void chartDataDoesNotUseExcludedWorkItems() {
        WorkItem excludedWorkItem = newBacklogItemWithSize(6);
        excludedWorkItem.setExcluded(true);
        
        BurnUpDataModel model = new BurnUpDataModel(WORK_ITEM_TYPE, asList(excludedWorkItem), null, DATE);
        CategoryDataset data = datasetFactory.createDataset(model);

        assertThat(data.getValue(BACKLOG, DATE).doubleValue(), is(0.0));
    }
    
    private final WorkItem newBacklogItemWithSize(int size) {
        WorkItem workItem = new WorkItem(1, WORK_ITEM_TYPE);
        workItem.setAverageCaseEstimate(size);
        workItem.advance(DATE);
        return workItem;
    }
}
