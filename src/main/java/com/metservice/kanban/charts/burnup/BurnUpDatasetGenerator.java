package com.metservice.kanban.charts.burnup;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class BurnUpDatasetGenerator {

    public CategoryDataset createDataset(BurnUpDataModel model) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        HistoricDatasetPopulator historicPopulator = new HistoricDatasetPopulator(model);
        historicPopulator.populateDataset(dataset);

        ProjectedDatasetPopulator projectedPopulator = new ProjectedDatasetPopulator(model);
        projectedPopulator.populateDataset(dataset);

        return dataset;
    }
}
