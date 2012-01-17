package com.metservice.kanban.charts.burnup;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.joda.time.LocalDate;
import com.metservice.kanban.charts.KanbanDrawingSupplier;
import com.metservice.kanban.charts.cumulativeflow.CumulativeFlowChartBuilder;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class DefaultBurnUpChartGenerator implements BurnUpChartGenerator {

    private final ChartWriter chartWriter;

    public DefaultBurnUpChartGenerator(ChartWriter chartWriter) {
        this.chartWriter = chartWriter;
    }

    @Override
    public void generateBurnUpChart(KanbanProject project, WorkItemType type, List<WorkItem> workItems,
                                    LocalDate startDate, LocalDate currentDate, OutputStream outputStream)
        throws IOException {
        BurnUpDataModel model = new BurnUpDataModel(type, workItems, startDate, currentDate);

        CategoryDataset dataset = new BurnUpDatasetGenerator().createDataset(model);  
        JFreeChart chart = createChart(dataset, project, startDate, currentDate);
        chartWriter.writeChart(outputStream, chart, 800, 600);        
    }

    private JFreeChart createChart(CategoryDataset dataset, KanbanProject project, LocalDate startDate,
                                   LocalDate endDate) {
        JFreeChart chart = ChartFactory.createStackedAreaChart(
            "Burn-Up Chart", // chart title
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
        plot.setDrawingSupplier(new KanbanDrawingSupplier(3));        

        CumulativeFlowChartBuilder.insertJournalEntries(dataset, project, plot, startDate, endDate);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.12);
        
        return chart;
    }
}
