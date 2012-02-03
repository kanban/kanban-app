package com.metservice.kanban.charts.burnup;

import static org.junit.Assert.assertEquals;
import org.jfree.data.xy.XYDataset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EstimatesBurnDownDatasetGeneratorTest {

    private EstimatesBurnDownDatasetGenerator generator;
    @Mock
    EstimatesBurnDownDataModel model;

    @Before
    public void setUp() {
        generator = new EstimatesBurnDownDatasetGenerator();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatesDatasetWithThreeSeries() {

        XYDataset dataset = generator.createDataset(model);

        assertEquals(3, dataset.getSeriesCount());

        assertEquals("Planned budget", dataset.getSeriesKey(0));
        assertEquals("Estimated budget", dataset.getSeriesKey(1));
        assertEquals("Current budget", dataset.getSeriesKey(2));
    }
}
