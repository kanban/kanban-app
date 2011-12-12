package com.metservice.kanban.charts.burnup;

import static com.metservice.kanban.charts.ChartUtils.nextWorkingDayAfter;
import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.BACKLOG;
import static com.metservice.kanban.charts.burnup.BurnUpChartSeriesNames.COMPLETE;
import org.jfree.data.category.DefaultCategoryDataset;
import org.joda.time.LocalDate;

public final class ProjectedDatasetPopulator {

    private final BurnUpDataModel model;

    public ProjectedDatasetPopulator(BurnUpDataModel model) {
        this.model = model;
    }

    public void populateDataset(DefaultCategoryDataset dataset) {
        double totalSize = model.getTotalSizeOnDate(model.getCurrentDate());
        double completedSize = model.getCompletedSizeOnDate(model.getCurrentDate());

        int elapsedDays = model.getWorkingDays().size() - 1;
        double pointsCompletedPerDay = completedSize / elapsedDays;
        
        if (pointsCompletedPerDay == 0) {
            return;
        }

        LocalDate date = model.getCurrentDate();
        while (completedSize <= totalSize - pointsCompletedPerDay) {
            completedSize += pointsCompletedPerDay;
            date = nextWorkingDayAfter(date);

            dataset.addValue(completedSize, COMPLETE, date);
            dataset.addValue(totalSize - completedSize, BACKLOG, date);
        }
    }
}
