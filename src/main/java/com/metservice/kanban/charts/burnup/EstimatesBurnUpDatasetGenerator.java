package com.metservice.kanban.charts.burnup;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.joda.time.LocalDate;
import com.google.gson.internal.Pair;

public class EstimatesBurnUpDatasetGenerator {

    public CategoryDataset createDataset(EstimatesBurnUpDataModel model) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Integer currentBudget = 0;
        int remainingFeaturePoints = model.getAllFeaturePoints();

        dataset.addValue(remainingFeaturePoints, "feature points", currentBudget);

        for (Pair<Integer, LocalDate> budgetEntry : model.getBudgetEntries()) {
            remainingFeaturePoints = model.getRemainingFeaturePointForBudget(budgetEntry);
            dataset.addValue(remainingFeaturePoints, "feature points", budgetEntry.first);
        }

        if (model.getProjectedBudgetConsumed() < model.getBudget()) {
            dataset.addValue(0, "projected feature points", model.getProjectedBudgetConsumed());
            dataset.addValue(0, "projected feature points", model.getBudget());
        }
        else {
            dataset.addValue(model.getProjectedEndOfMoneyPoints(), "remaining f", model.getBudget());
        }



        /*
        HistoricDatasetPopulator historicPopulator = new HistoricDatasetPopulator(model);
        historicPopulator.populateDataset(dataset);

        ProjectedDatasetPopulator projectedPopulator = new ProjectedDatasetPopulator(model);
        projectedPopulator.populateDataset(dataset);
         */

        return dataset;
    }

}
