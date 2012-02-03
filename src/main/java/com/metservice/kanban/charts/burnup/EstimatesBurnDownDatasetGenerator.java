package com.metservice.kanban.charts.burnup;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.joda.time.LocalDate;
import com.google.gson.internal.Pair;

public class EstimatesBurnDownDatasetGenerator {

    public XYDataset createDataset(EstimatesBurnDownDataModel model) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        double currentBudget = 0;
        int remainingFeaturePoints = model.getAllFeaturePoints();

        XYSeries plannedBudgedSeries = new XYSeries("Planned budget");
        XYSeries projectedBudgedSeries = new XYSeries("Estimated budget");
        XYSeries currentBudgedSeries = new XYSeries("Current budget");

        currentBudgedSeries.add(currentBudget, (double) remainingFeaturePoints);

        for (Pair<Integer, LocalDate> budgetEntry : model.getBudgetEntries()) {
            remainingFeaturePoints = model.getRemainingFeaturePointForBudget(budgetEntry);
            currentBudgedSeries.add((double) budgetEntry.first, remainingFeaturePoints);
        }

        plannedBudgedSeries.add(0, model.getAllFeaturePoints());
        plannedBudgedSeries.add((double) model.getBudget(), 0);

        projectedBudgedSeries.add(0, model.getAllFeaturePoints());
        projectedBudgedSeries.add((double) model.getProjectedBudgetConsumed(), 0);

        dataset.addSeries(plannedBudgedSeries);
        dataset.addSeries(projectedBudgedSeries);
        dataset.addSeries(currentBudgedSeries);

        return dataset;
    }

}
