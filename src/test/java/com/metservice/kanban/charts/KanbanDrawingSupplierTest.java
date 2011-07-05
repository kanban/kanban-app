package com.metservice.kanban.charts;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;


public class KanbanDrawingSupplierTest {
    
    @Test
    public void canCheckNumberOfSeriers() {
        KanbanDrawingSupplier supplier = new KanbanDrawingSupplier(5);
        assertThat(supplier.getNumberOfSeries(), is(5));
    }

}
