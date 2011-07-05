package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class KanbanBoardColumnTest {

    @Test
    public void remembersItsConstructorArguments() {
        WorkItemType type = new WorkItemType();

        KanbanBoardColumn column = new KanbanBoardColumn(type, "a phase");

        assertThat(column.getWorkItemType(), is(type));
        assertThat(column.getPhase(), is("a phase"));
    }
}
