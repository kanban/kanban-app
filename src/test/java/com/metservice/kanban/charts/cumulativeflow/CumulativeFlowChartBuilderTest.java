package com.metservice.kanban.charts.cumulativeflow;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.jfree.data.category.CategoryDataset;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class CumulativeFlowChartBuilderTest {

    @Test
    public void testDataSet() throws ParseException, IOException {
        WorkItemType type = new WorkItemType("phase1", "phase2", "phase3", "phase4");
        List<WorkItem> workItems = CumulativeFlowChartMatrixTest.buildListFirstCase(type);
        CumulativeFlowChartBuilder builder = new CumulativeFlowChartBuilder();
        builder.createDataset(type.getPhases(), workItems);
        
    }
    
    @Test
    public void testDataSetIsInCorrectOrder() throws ParseException, IOException {
        WorkItemType type = new WorkItemType("phase1", "phase2", "phase3", "phase4");
        List<WorkItem> workItems = CumulativeFlowChartMatrixTest.buildListFirstCase(type);
        CumulativeFlowChartBuilder builder = new CumulativeFlowChartBuilder();
        CategoryDataset dataset = builder.createDataset(type.getPhases(), workItems);
        assertThat(dataset.getRowKey(0).toString(), is("phase4"));
        assertThat(dataset.getRowKey(1).toString(), is("phase3"));
        assertThat(dataset.getRowKey(2).toString(), is("phase2"));
        assertThat(dataset.getRowKey(3).toString(), is("phase1"));
        
    }
    
}
