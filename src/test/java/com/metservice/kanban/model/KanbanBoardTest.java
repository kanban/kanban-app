package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class KanbanBoardTest {

    @Test
    public void workItemsCanBeInsertedIntoTheBoard() {
        WorkItemType type = new WorkItemType("phase 1", "phase 2");
        WorkItem feature1 = new WorkItem(1, type, "phase 1");
        WorkItem feature2 = new WorkItem(2, type, "phase 2");

        KanbanBoard board = new KanbanBoard(new KanbanBoardColumnList(new KanbanBoardColumn(type, "phase 1"),
            new KanbanBoardColumn(type, "phase 2")));
        board.insert(feature1, null, null);
        board.insert(feature2, null, null);

        assertThat(board.getCell(0, 0).getWorkItem(), is(feature1));
        assertThat(board.getCell(0, 1).getWorkItem(), is(feature2));
    }

    @Test
    public void insertedWorkItemsExtendTheBoardWhenNecessary() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, type, "phase");
        WorkItem feature2 = new WorkItem(2, type, "phase");

        KanbanBoard board = new KanbanBoard(new KanbanBoardColumnList(new KanbanBoardColumn(type, "phase")));
        board.insert(feature1, null, null);
        board.insert(feature2, null, null);

        assertThat(board.getCell(0, 0).getWorkItem(), is(feature1));
        assertThat(board.getCell(1, 0).getWorkItem(), is(feature2));
    }

    @Test
    public void whenClientCodeInsertsWorkItemsItCanAlsoSpecifyTheWorkItemsAboveAndBelow() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, type, "phase");
        WorkItem feature2 = new WorkItem(2, type, "phase");
        WorkItem feature3 = new WorkItem(3, type, "phase");

        KanbanBoard board = new KanbanBoard(new KanbanBoardColumnList(new KanbanBoardColumn(type, "phase")));
        board.insert(feature1, feature2, feature3);

        KanbanCell cell = board.getCell(0, 0);
        assertThat(cell.getWorkItemAbove(), is(feature2));
        assertThat(cell.getWorkItemBelow(), is(feature3));
    }
}
