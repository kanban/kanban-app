package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class KanbanCellTest {

    @Test
    public void cellCanHaveWorkItemAbove() {
        WorkItem featureAbove = new WorkItem(1, new WorkItemType());

        KanbanCell cell = new KanbanCell(featureAbove.getType());
        cell.setWorkItemAbove(featureAbove);

        assertThat(cell.getWorkItemAbove(), is(featureAbove));
    }

    @Test
    public void cellCanHaveWorkItemBelow() {
        WorkItem featureBelow = new WorkItem(1, new WorkItemType());

        KanbanCell cell = new KanbanCell(featureBelow.getType());
        cell.setWorkItemBelow(featureBelow);

        assertThat(cell.getWorkItemBelow(), is(featureBelow));
    }

    @Test
    public void cellCanBeCloned() throws CloneNotSupportedException {
        WorkItem feature = new WorkItem(1, new WorkItemType());
        WorkItem featureAbove = new WorkItem(2, new WorkItemType());
        WorkItem featureBelow = new WorkItem(3, new WorkItemType());

        KanbanCell originalCell = new KanbanCell(feature.getType());
        originalCell.setWorkItem(feature);
        originalCell.setWorkItemAbove(featureAbove);
        originalCell.setWorkItemBelow(featureBelow);

        KanbanCell clonedCell = originalCell.clone();
        assertThat(clonedCell.getWorkItem(), is(feature));
        assertThat(clonedCell.getWorkItemAbove(), is(featureAbove));
        assertThat(clonedCell.getWorkItemBelow(), is(featureBelow));
    }
}
