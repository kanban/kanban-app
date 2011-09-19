package com.metservice.kanban.charts.cycletime;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import com.metservice.kanban.charts.KanbanDrawingSupplier;
import com.metservice.kanban.model.WorkItem;

public class CycleTimeChartBuilder {

    public CategoryDataset createDataset(Collection<WorkItem> listOfItems) throws IOException {
        List<CycleTimeColumn> listOfColumns = convertFromWorkItemToCycleTimeColumns(listOfItems);
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        for (CycleTimeColumn column : listOfColumns) {
            for (int i = 0; i < column.numberOfFragments(); i++) {
                defaultcategorydataset.addValue(column.getFragmentWeight(i),
                    column.getFragmentName(i),
                    column.getName());
            }
        }
        return defaultcategorydataset;
    }

    public Collection<WorkItem> getCompletedWorkItemsInOrderOfCompletion(Collection<WorkItem> workItemList) {
        List<WorkItem> selectedWorkItems = new ArrayList<WorkItem>();
        for (WorkItem workItem : workItemList) {
            if (!workItem.isExcluded() && workItem.isCompleted()) {
                selectedWorkItems.add(workItem);
            }
        }
        sortByDateOfCompletion(selectedWorkItems);
        return selectedWorkItems;
    }

    private List<CycleTimeColumn> convertFromWorkItemToCycleTimeColumns(Collection<WorkItem> listOfItems) {
        List<CycleTimeColumn> listOfColumns = new ArrayList<CycleTimeColumn>();
        for (WorkItem workItem : listOfItems) {
            if (!workItem.isExcluded()) {
                listOfColumns.add(CycleTimeColumn.buildCycleTimeColumnFromWorkItem(workItem));
            }
        }
        return listOfColumns;
    }

    private void sortByDateOfCompletion(List<WorkItem> selectedWorkItems) {
        Collections.sort(selectedWorkItems, new Comparator<WorkItem>() {

            @Override
            public int compare(WorkItem o1, WorkItem o2) {
                return o1.getDate(o1.getCurrentPhase()).compareTo(o2.getDate(o2.getCurrentPhase()));
            }
        });
    }

    public JFreeChart createChart(CategoryDataset categorydataset) {

        JFreeChart jfreechart = ChartFactory.createStackedBarChart("Cycle Time Chart", "IDs", "Working Days", categorydataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) jfreechart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setDrawingSupplier(new KanbanDrawingSupplier(categorydataset.getRowCount()));

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        StackedBarRenderer stackedbarrenderer = (StackedBarRenderer) plot.getRenderer();
        stackedbarrenderer.setDrawBarOutline(false);
        stackedbarrenderer.setBarPainter(new GradientBarPainter(0, 0, 0));
        stackedbarrenderer.setShadowVisible(false);
        stackedbarrenderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());
        return jfreechart;
    }

}
