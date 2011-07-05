package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class KanbanBoardRowTest {

    @Test
    public void mayContainMultipleWorkItems() {
        WorkItemType type = new WorkItemType("phase 1", "phase 2");
        WorkItem feature1 = new WorkItem(1, type, "phase 1");
        WorkItem feature2 = new WorkItem(2, type, "phase 2");

        KanbanBoardColumnList columns = new KanbanBoardColumnList(new KanbanBoardColumn(type, "phase 1"),
            new KanbanBoardColumn(type, "phase 2"));
        KanbanBoardRow row = new KanbanBoardRow(columns);
        row.insert(feature1, null, null);
        row.insert(feature2, null, null);

        assertThat(row.getCell(0).getWorkItem(), is(feature1));
        assertThat(row.getCell(1).getWorkItem(), is(feature2));
    }

    @Test
    public void canAddReturnsTrueForAnEmptyCell() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem feature = new WorkItem(1, type, "phase");

        KanbanBoardColumnList columns = new KanbanBoardColumnList(new KanbanBoardColumn(type, "phase"));
        KanbanBoardRow row = new KanbanBoardRow(columns);

        assertThat(row.canAdd(feature), is(true));
    }

    @Test
    public void canAddReturnsFalseForAnOccupiedCell() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, type, "phase");
        WorkItem feature2 = new WorkItem(2, type, "phase");

        KanbanBoardColumnList columns = new KanbanBoardColumnList(new KanbanBoardColumn(type, "phase"));
        KanbanBoardRow row = new KanbanBoardRow(columns);
        row.insert(feature1, null, null);

        assertThat(row.canAdd(feature2), is(false));
    }

    @Test
    public void copiesEverythingWhenCloned() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, type, "phase");
        WorkItem feature2 = new WorkItem(2, type, "phase");
        WorkItem feature3 = new WorkItem(3, type, "phase");

        KanbanBoardColumnList columns = new KanbanBoardColumnList(new KanbanBoardColumn(type, "phase"));
        KanbanBoardRow originalRow = new KanbanBoardRow(columns);
        originalRow.insert(feature1, feature2, feature3);

        KanbanBoardRow clonedRow = originalRow.clone();

        KanbanCell cell = clonedRow.getCell(0);
        assertThat(cell.getWorkItem(), is(feature1));
        assertThat(cell.getWorkItemAbove(), is(feature2));
        assertThat(cell.getWorkItemBelow(), is(feature3));
        assertThat(clonedRow.getColumns(), is(columns));
    }
}
