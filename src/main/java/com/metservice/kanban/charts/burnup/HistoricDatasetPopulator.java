package com.metservice.kanban.charts.burnup;

import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.BACKLOG;
import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.COMPLETE;
import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.IN_PROGRESS;
import org.jfree.data.category.DefaultCategoryDataset;
import org.joda.time.LocalDate;

public final class HistoricDatasetPopulator {

    private final BurnUpDataModel model;

    public HistoricDatasetPopulator(BurnUpDataModel model) {
        this.model = model;
    }

    public void populateDataset(DefaultCategoryDataset dataset) {
        for (LocalDate date : model.getWorkingDays()) {
            dataset.addValue(model.getCompletedSizeOnDate(date), COMPLETE, date);
            dataset.addValue(model.getInProgressSizeOnDate(date), IN_PROGRESS, date);
            dataset.addValue(model.getBacklogSizeOnDate(date), BACKLOG, date);
        }
    }
}
