package com.metservice.kanban.charts.burnup;

import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.BACKLOG;
import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.COMPLETE;
import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.IN_PROGRESS;
import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.jfree.data.category.DefaultCategoryDataset;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class HistoricDatasetPopulatorTest {
    
    private DefaultCategoryDataset dataset;

    @Before
    public void before() {
        this.dataset = new DefaultCategoryDataset();
    }

    @Test
    public void dataHasCorrectThreeRows() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem workItem = new WorkItem(1, type);
        workItem.advance(parseIsoDate("2011-06-10"));

        BurnUpDataModel model = new BurnUpDataModel(type, asList(workItem), parseIsoDate("2011-06-13"));

        HistoricDatasetPopulator populator = new HistoricDatasetPopulator(model);
        populator.populateDataset(dataset);

        assertThat(dataset.getRowCount(), is(3));
        assertThat((String) dataset.getRowKey(0), is(COMPLETE));
        assertThat((String) dataset.getRowKey(1), is(IN_PROGRESS));
        assertThat((String) dataset.getRowKey(2), is(BACKLOG));
    }

    @Test
    public void dataHasOneColumnPerDay() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem workItem = new WorkItem(1, type);
        workItem.advance(parseIsoDate("2011-06-10"));

        BurnUpDataModel model = new BurnUpDataModel(type, asList(workItem), parseIsoDate("2011-06-13"));

        HistoricDatasetPopulator populator = new HistoricDatasetPopulator(model);
        populator.populateDataset(dataset);

        //expected to skip the weekend (11 and 12) 
        assertThat(dataset.getColumnCount(), is(2));
        assertThat((LocalDate) dataset.getColumnKey(0), is(parseIsoDate("2011-06-10")));
        assertThat((LocalDate) dataset.getColumnKey(1), is(parseIsoDate("2011-06-13")));
    }

    @Test
    public void dataHasCorrectValues() {
        WorkItemType type = new WorkItemType("backlog", "in progress", "complete");
        LocalDate yesterday = parseIsoDate("2011-06-01");
        LocalDate today = parseIsoDate("2011-06-02");

        WorkItem backlog1 = new WorkItem(1, type);
        backlog1.setAverageCaseEstimate(1);
        backlog1.advance(yesterday);

        WorkItem backlog2 = new WorkItem(2, type);
        backlog2.setAverageCaseEstimate(2);
        backlog2.advance(yesterday);

        WorkItem inProgress4 = new WorkItem(3, type);
        inProgress4.setAverageCaseEstimate(4);
        inProgress4.advance(yesterday);
        inProgress4.advance(today);

        BurnUpDataModel model = new BurnUpDataModel(type, asList(backlog1, backlog2, inProgress4), today);

        HistoricDatasetPopulator populator = new HistoricDatasetPopulator(model);
        populator.populateDataset(dataset);

        assertThat((Double) dataset.getValue(COMPLETE, yesterday), is(0.0));
        assertThat((Double) dataset.getValue(IN_PROGRESS, yesterday), is(0.0));
        assertThat((Double) dataset.getValue(BACKLOG, yesterday), is(7.0));

        assertThat((Double) dataset.getValue(COMPLETE, today), is(0.0));
        assertThat((Double) dataset.getValue(IN_PROGRESS, today), is(4.0));
        assertThat((Double) dataset.getValue(BACKLOG, today), is(3.0));
    }
}
