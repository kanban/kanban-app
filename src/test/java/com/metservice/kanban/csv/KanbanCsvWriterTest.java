package com.metservice.kanban.csv;

import static java.util.Collections.singleton;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.utils.DateUtils;

public class KanbanCsvWriterTest {

    @Test
    public void canReadAWorkItemItWrites() throws IOException {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItemType type = new WorkItemType("phase 1", "phase 2");

        WorkItem workItem = new WorkItem(111, 11, type);
        workItem.setName("workItem 1");
        workItem.setSize(6);
        workItem.setImportance(3);
        workItem.setNotes("notes");
        workItem.setDateAsString("phase 1", "2011-06-02");
        workItem.setExcluded(true);
        
        List<WorkItem> workItems = new ArrayList<WorkItem>(singleton(workItem));

        StringWriter writeTarget = new StringWriter();
        KanbanCsvWriter writer = new KanbanCsvWriter(writeTarget, type);
        writer.write(workItems);
        
        StringReader readSource = new StringReader(writeTarget.toString());
        KanbanCsvReader reader = new KanbanCsvReader(readSource, type);
        
        WorkItem retrievedWorkItem = reader.read().get(0);
        assertThat(retrievedWorkItem.getId(), is(111));
        assertThat(retrievedWorkItem.getParentId(), is(11));
        assertThat(retrievedWorkItem.getType(), is(type));
        assertThat(retrievedWorkItem.getName(), is("workItem 1"));
        assertThat(retrievedWorkItem.getSize(), is(6));
        assertThat(retrievedWorkItem.getImportance(), is(3));
        assertThat(retrievedWorkItem.getNotes(), is("notes"));
        assertThat(retrievedWorkItem.isExcluded(), is(true));
        assertThat(retrievedWorkItem.getDate("phase 1"), is(DateUtils.parseIsoDate("2011-06-02")));
        assertThat(retrievedWorkItem.hasDate("phase 2"), is(false));
    }
}
