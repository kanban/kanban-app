package com.metservice.kanban.charts.cycletime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.joda.time.LocalDate;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class CycleTimeColumnTest {

    private static LocalDateFormatter formatter = new LocalDateFormatter();
    private static class LocalDateFormatter {
        public LocalDate parse(String pattern) throws ParseException {
            return LocalDate.fromDateFields(new SimpleDateFormat("yyyy/MM/dd").parse(pattern));
        }
    }

    @Test
    public void storesFragments() {
        CycleTimeColumn column = new CycleTimeColumn("column");
        column.addFragment("fragment1", 1);
        column.addFragment("fragment2", 2);
        
        assertThat(column.getName(), is("column"));
        assertThat(column.numberOfFragments(), is(2));
        assertThat(column.getFragmentName(0), is("fragment1"));
        assertThat(column.getFragmentWeight(0), is(1));
        assertThat(column.getFragmentName(1), is("fragment2"));
        assertThat(column.getFragmentWeight(1), is(2));
    }

    @Test
    public void representsWorkItemAsAColumnInTheChart() throws ParseException {
        WorkItemType type = new WorkItemType("backlog", "phase 2", "phase 3");
        WorkItem feature = new WorkItem(1, type);
        feature.setDateAsString("backlog", "2011-02-07");
        feature.setDateAsString("phase 2", "2011-02-10");
        feature.setDateAsString("phase 3", "2011-02-12");
        
        CycleTimeColumn column = CycleTimeColumn.buildCycleTimeColumnFromWorkItem(feature);
        
        assertThat(column.getName(), is("1"));
        assertThat(column.numberOfFragments(), is(1));
        assertThat(column.getFragmentName(0), is("phase 2"));
        assertThat(column.getFragmentWeight(0), is(2));
    }

    @Test
    public void skipItemIfWorkItemIsNotComplete() throws ParseException {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItemType type = new WorkItemType("phase 1", "phase 2", "phase 3", "phase 4");
        
        WorkItem workItem = new WorkItem(1, 0, type);
        workItem.setDate("phase 1", formatter.parse("2011/02/7"));
        workItem.setDate("phase 2", formatter.parse("2011/02/10"));
        workItem.setDate("phase 4", formatter.parse("2011/02/12"));

        CycleTimeColumn column = CycleTimeColumn.buildCycleTimeColumnFromWorkItem(workItem);

        assertThat(column.getName(), is("1"));
        assertThat(column.numberOfFragments(), is(1));
        assertThat(column.getFragmentName(0), is("phase 2"));
        assertThat(column.getFragmentWeight(0), is(2));
    }

    @Test
    public void returnsNullIfWorkItemIsNotComplete() throws ParseException {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItemType type = new WorkItemType("phase1", "phase2", "phase3");

        WorkItem workItem = new WorkItem(1, 0, type);
        workItem.setDate("phase1", formatter.parse("2011/02/7"));
        workItem.setDate("phase2", formatter.parse("2011/02/10"));
        assertNull(CycleTimeColumn.dateWhenPhaseWasCompleted(workItem, "phase3"));
    }

}
