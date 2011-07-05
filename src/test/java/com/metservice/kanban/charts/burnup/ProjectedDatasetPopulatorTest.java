package com.metservice.kanban.charts.burnup;

import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.BACKLOG;
import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.COMPLETE;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.jfree.data.category.DefaultCategoryDataset;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.utils.DateUtils;

public class ProjectedDatasetPopulatorTest {

    private static final WorkItemType WORK_ITEM_TYPE = new WorkItemType("backlog", "completed");
    private static final LocalDate MONDAY = DateUtils.parseIsoDate("2011-06-13");
    
    private static final LocalDate TUESDAY = MONDAY.plusDays(1);
    private static final LocalDate WEDNESDAY = MONDAY.plusDays(2);
    private static final LocalDate THURSDAY = MONDAY.plusDays(3);
    private static final LocalDate FRIDAY = MONDAY.plusDays(4);
    private static final LocalDate SATURDAY= MONDAY.plusDays(5);
    private static final LocalDate SUNDAY = MONDAY.plusDays(6);
    private static final LocalDate NEXT_MONDAY = MONDAY.plusDays(7);
    
    private DefaultCategoryDataset dataset;
    
    @Before
    public void before() {
        dataset = new DefaultCategoryDataset();
    }

    @Test
    public void projectsData() {
        WorkItem workItem1 = new WorkItem(1, WORK_ITEM_TYPE);
        workItem1.setSize(1);
        workItem1.advance(MONDAY);
        workItem1.advance(TUESDAY);

        WorkItem workItem2 = new WorkItem(2, WORK_ITEM_TYPE);
        workItem2.setSize(1);
        workItem2.advance(MONDAY);

        BurnUpDataModel model = new BurnUpDataModel(WORK_ITEM_TYPE, asList(workItem1, workItem2), TUESDAY);
        
        ProjectedDatasetPopulator populator = new ProjectedDatasetPopulator(model);
        populator.populateDataset(dataset);

        assertThat(dataset.getValue(COMPLETE, WEDNESDAY).doubleValue(), is(2.0));
        assertThat(dataset.getValue(BACKLOG, WEDNESDAY).doubleValue(), is(0.0));
        
        assertThat(dataset.getColumnIndex(THURSDAY), is(-1));
    }

    @Test
    public void projectsDataOfNonUnitSize() {
        WorkItem workItem1 = new WorkItem(1, WORK_ITEM_TYPE);
        workItem1.setSize(2);
        workItem1.advance(MONDAY);
        workItem1.advance(TUESDAY);

        WorkItem workItem2 = new WorkItem(2, WORK_ITEM_TYPE);
        workItem2.setSize(2);
        workItem2.advance(MONDAY);
        
        BurnUpDataModel model = new BurnUpDataModel(WORK_ITEM_TYPE, asList(workItem1, workItem2), TUESDAY);
        
        ProjectedDatasetPopulator populator = new ProjectedDatasetPopulator(model);
        populator.populateDataset(dataset);

        assertThat(dataset.getValue(COMPLETE, WEDNESDAY).doubleValue(), is(4.0));
        assertThat(dataset.getValue(BACKLOG, WEDNESDAY).doubleValue(), is(0.0));
        
        assertThat(dataset.getColumnIndex(THURSDAY), is(-1));
    }
    
    @Test
    public void projectsDataMoreThanOneDay() {
        WorkItem workItem1 = new WorkItem(1, WORK_ITEM_TYPE);
        workItem1.setSize(1);
        workItem1.advance(MONDAY);
        workItem1.advance(TUESDAY);

        WorkItem workItem2 = new WorkItem(2, WORK_ITEM_TYPE);
        workItem2.setSize(2);
        workItem2.advance(MONDAY);

        BurnUpDataModel model = new BurnUpDataModel(WORK_ITEM_TYPE, asList(workItem1, workItem2), TUESDAY);
        
        ProjectedDatasetPopulator populator = new ProjectedDatasetPopulator(model);
        populator.populateDataset(dataset);

        assertThat(dataset.getValue(COMPLETE, WEDNESDAY).intValue(), is(2));
        assertThat(dataset.getValue(BACKLOG, WEDNESDAY).intValue(), is(1));
        
        assertThat(dataset.getValue(COMPLETE, THURSDAY).intValue(), is(3));
        assertThat(dataset.getValue(BACKLOG, THURSDAY).intValue(), is(0));
        
        assertThat(dataset.getColumnIndex(FRIDAY), is(-1));
    }
    
    @Test
    public void skipsWeekendsInProjection() {
        WorkItem workItem1 = new WorkItem(1, WORK_ITEM_TYPE);
        workItem1.setSize(1);
        workItem1.advance(THURSDAY);
        workItem1.advance(FRIDAY);

        WorkItem workItem2 = new WorkItem(2, WORK_ITEM_TYPE);
        workItem2.setSize(1);
        workItem2.advance(THURSDAY);
        
        BurnUpDataModel model = new BurnUpDataModel(WORK_ITEM_TYPE, asList(workItem1, workItem2), FRIDAY);
        
        ProjectedDatasetPopulator populator = new ProjectedDatasetPopulator(model);
        populator.populateDataset(dataset);

        assertThat(dataset.getColumnIndex(SATURDAY), is(-1));
        assertThat(dataset.getColumnIndex(SUNDAY), is(-1));
        
        assertThat(dataset.getValue(COMPLETE, NEXT_MONDAY).doubleValue(), is(2.0));
        assertThat(dataset.getValue(BACKLOG, NEXT_MONDAY).doubleValue(), is(0.0));
    }
    
    @Test
    public void doesNotProjectIfRateIsZero() {
        WorkItem workItem = new WorkItem(1, WORK_ITEM_TYPE);
        workItem.advance(MONDAY);
        
        BurnUpDataModel model = new BurnUpDataModel(WORK_ITEM_TYPE, asList(workItem), TUESDAY);
        
        ProjectedDatasetPopulator populator = new ProjectedDatasetPopulator(model);
        populator.populateDataset(dataset);
        
        assertThat(dataset.getColumnIndex(WEDNESDAY), is(-1));
    }
}
