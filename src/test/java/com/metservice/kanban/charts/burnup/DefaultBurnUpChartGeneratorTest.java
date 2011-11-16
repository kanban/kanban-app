package com.metservice.kanban.charts.burnup;

import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static java.awt.Color.WHITE;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.CategoryDataset;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import com.metservice.kanban.charts.KanbanDrawingSupplier;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class DefaultBurnUpChartGeneratorTest {
    
    private static final LocalDate TODAY = parseIsoDate("2011-06-13");

    private ChartWriter writer;
    private DefaultBurnUpChartGenerator generator;
    private ArgumentCaptor<JFreeChart> chartCaptor;
    private WorkItemType type;

    @Before
    public void before() {
        writer = mock(ChartWriter.class);
        generator = new DefaultBurnUpChartGenerator(writer);
        chartCaptor = ArgumentCaptor.forClass(JFreeChart.class);
        type = new WorkItemType("backlog", "completed");
    }

    @Test
    public void chartsAre800By600() throws IOException {
        generator.generateBurnUpChart(type, new ArrayList<WorkItem>(), null, TODAY, mock(OutputStream.class));

        verify(writer).writeChart(any(OutputStream.class), any(JFreeChart.class), eq(800), eq(600));
    }

    @Test
    public void chartsAreWrittenToTargetOutputStream() throws IOException {
        OutputStream targetOutputStream = mock(OutputStream.class);

        generator.generateBurnUpChart(type, new ArrayList<WorkItem>(), null, TODAY, targetOutputStream);

        verify(writer).writeChart(eq(targetOutputStream), any(JFreeChart.class), anyInt(), anyInt());
    }

    @Test
    public void chartHasCorrectDecoration() throws IOException {
        JFreeChart chart = captureGeneratedJFreeChart(type, new ArrayList<WorkItem>(), TODAY);

        assertThat(chart.getBackgroundPaint(), is((Paint) WHITE));
        assertThat(chart.getTitle().getText(), is("Burn-Up Chart"));
        assertThat(chart.getCategoryPlot().getForegroundAlpha(), is(1f));
        assertThat(chart.getCategoryPlot().getBackgroundPaint(), is((Paint)Color.WHITE));
        assertThat(chart.getCategoryPlot().isDomainGridlinesVisible(), is(true));
        assertThat(chart.getCategoryPlot().getDomainGridlinePaint(), is((Paint)Color.GRAY));
        assertThat(chart.getCategoryPlot().getRangeGridlinePaint(), is((Paint)Color.GRAY));

        assertThat(((KanbanDrawingSupplier) chart.getCategoryPlot().getDrawingSupplier()).getNumberOfSeries(), is(3));
        
        assertThat(chart.getCategoryPlot().getDomainAxis().getLowerMargin(), is(0.0));
        assertThat(chart.getCategoryPlot().getDomainAxis().getUpperMargin(), is(0.0));
        assertThat(chart.getCategoryPlot().getDomainAxis().getCategoryLabelPositions(), is(CategoryLabelPositions.UP_45));
        

        assertThat(chart.getCategoryPlot().getRangeAxis().getStandardTickUnits(), is(NumberAxis.createIntegerTickUnits()));
    }
    
    @Test
    public void obtainsDataFromFactory() throws IOException {
        WorkItem workItem1 = new WorkItem(1, type);
        workItem1.advance(TODAY);
        workItem1.advance(TODAY.plusDays(1));
        workItem1.setAverageCaseEstimate(1);
        
        WorkItem workItem2 = new WorkItem(1, type);
        workItem2.advance(TODAY);
        workItem1.setAverageCaseEstimate(1);

        CategoryDataset dataset = captureGeneratedJFreeChart(type, asList(workItem1, workItem2),
            TODAY.plusDays(1)).getCategoryPlot().getDataset();
        
        assertThat((String) dataset.getRowKey(0), is("Complete"));
    }

    private JFreeChart captureGeneratedJFreeChart(WorkItemType type, List<WorkItem> input, LocalDate endDate) throws IOException {
        generator.generateBurnUpChart(type, input, null, endDate, mock(OutputStream.class));

        verify(writer).writeChart(any(OutputStream.class), chartCaptor.capture(), anyInt(), anyInt());
        return chartCaptor.getValue();
    }
}
