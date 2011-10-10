package com.metservice.kanban.charts.cumulativeflow;

import static com.metservice.kanban.utils.DateUtils.currentLocalDate;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.joda.time.LocalDate;

import com.metservice.kanban.charts.ChartUtils;
import com.metservice.kanban.charts.KanbanDrawingSupplier;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.utils.Day;

public class CumulativeFlowChartBuilder {
	private LocalDate startDate;
	private LocalDate endDate;
	
	public CumulativeFlowChartBuilder (LocalDate startDate, LocalDate endDate){
		this.startDate = startDate;
		this.endDate = endDate;
	}
    
    public CategoryDataset createDataset(List<String> phases, List<WorkItem> workItemList) throws IOException {
        // add start date and end date instead of local date here
        if (endDate == null){
        	endDate = currentLocalDate();
        }
    	if (startDate == null){
        	startDate = ChartUtils.getFirstDate(workItemList);

            if (startDate == null) {
                startDate = endDate;
            }
        }
        CumulativeFlowChartMatrix matrix = new CumulativeFlowChartMatrix(phases, startDate, endDate);
        for(WorkItem workItem: workItemList) {
            matrix.registerWorkItem(workItem);
        }
        
        List<LocalDate> dates = matrix.getOrderedListOfDates();
        double[][] data = matrix.getData();
        Day[] days = getListOfDays(dates);
        return DatasetUtilities.createCategoryDataset(getPhasesInInverseOrder(phases), days, data);
    }
    
    
    private String[] getPhasesInInverseOrder(List<String> phases) {
        String[] invertedPhases = new String[phases.size()];
        int k = phases.size()-1;
        for(String phase: phases) {
            invertedPhases[k--] = phase;
        }
        return invertedPhases;
    }


    public JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createStackedAreaChart(
            "Cumulative Flow Chart", // chart title
            "", // domain axis label
            "", // range axis label
            dataset, // data
            PlotOrientation.VERTICAL, // orientation
            true, // include legend
            true,
            false
            );

        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        
        plot.setForegroundAlpha(1f);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDrawingSupplier(new KanbanDrawingSupplier(dataset.getRowCount()));

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }

    private Day[] getListOfDays(List<LocalDate> dates) {
        Day[] days = new Day[dates.size()];
        int i=0;
        for(LocalDate date: dates) {
            days[i++] = new Day(date);
        }
        return days;
    }
    
}


