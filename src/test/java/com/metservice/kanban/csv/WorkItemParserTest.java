package com.metservice.kanban.csv;

import static com.metservice.kanban.csv.CsvConstants.*;
import static com.metservice.kanban.utils.DateUtils.formatIsoDate;
import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.joda.time.LocalDate;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class WorkItemParserTest {

    @Test
    public void parsesWorkItems() {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItemType type = new WorkItemType("phase 1", "phase 2", "phase 3");

        LocalDate date1 = parseIsoDate("2011-06-10");
        LocalDate date2 = parseIsoDate("2011-06-13");

        CsvColumnNames columnNames = new CsvColumnNames("phase 1", ID_COLUMN_NAME, NAME_COLUMN_NAME, "phase 2",
            IMPORTANCE_COLUMN_NAME, NOTES_COLUMN_NAME, PARENT_ID_COLUMN_NAME, SIZE_COLUMN_NAME, "phase 3", EXCLUDED_COLUMN_NAME, COLOR_COLUMN_NAME);
        String[] cells = new String[] {
            formatIsoDate(date1), "77", "work item name", formatIsoDate(date2), "-3", "some notes", "7", "5", "", "true", "FFFFFF"};

        DefaultWorkItemParser parser = new DefaultWorkItemParser(type);
        WorkItem workItem = parser.parseWorkItem(columnNames, cells);

        assertThat(workItem.getDate("phase 1"), is(date1));
        assertThat(workItem.getDate("phase 2"), is(date2));
        assertThat(workItem.hasDate("phase 3"), is(false));

        assertThat(workItem.getId(), is(77));
        assertThat(workItem.getParentId(), is(7));
        assertThat(workItem.getName(), is("work item name"));
        assertThat(workItem.getNotes(), is("some notes"));
        assertThat(workItem.getSize(), is(5));
        assertThat(workItem.getImportance(), is(-3));
        assertThat(workItem.isExcluded(), is(true));
    }
}
