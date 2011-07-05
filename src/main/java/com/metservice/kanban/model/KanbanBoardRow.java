package com.metservice.kanban.model;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

//TODO This class needs unit tests.

public class KanbanBoardRow implements Iterable<KanbanCell>, Cloneable {

    private final KanbanBoardColumnList columns;
    private final KanbanCell[] cells;

    public KanbanBoardRow(KanbanBoardColumnList columns) {
        this.columns = columns;
        this.cells = createEmptyCellArray(columns);
    }

    private KanbanCell[] createEmptyCellArray(KanbanBoardColumnList columns) {
        KanbanCell[] array = new KanbanCell[columns.size()];

        int i = 0;
        for (KanbanBoardColumn column : columns) {
            array[i] = new KanbanCell(column.getWorkItemType());
            i++;
        }
        return array;
    }

    public boolean canAdd(WorkItem workItem) {
        String phase = workItem.getCurrentPhase();
        int index = columns.getIndexOfPhase(phase);

        return cells[index].isEmpty();
    }

    public void insert(WorkItem workItem, WorkItem workItemAbove, WorkItem workItemBelow) {
        String phase = workItem.getCurrentPhase();
        
        int index = columns.getIndexOfPhase(phase);

        KanbanCell cell = cells[index];
        cell.setWorkItem(workItem);
        cell.setWorkItemAbove(workItemAbove);
        cell.setWorkItemBelow(workItemBelow);
    }

    public void merge(KanbanBoardRow otherRow) {
        verifyPhasesMatch(otherRow);

        for (int i = 0; i < otherRow.cells.length; i++) {
            KanbanCell cell = otherRow.cells[i];
            if (!cell.isEmpty()) {
                cells[i] = cell.clone();
            }
        }
    }

    public boolean hasItemOfType(WorkItemType type) {
        for (KanbanCell cell : cells) {
            if (!cell.isEmpty() && cell.getWorkItemType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public KanbanBoardRow clone() {
        KanbanBoardRow clone = new KanbanBoardRow(columns);
        for (int i = 0; i < cells.length; i++) {
            clone.cells[i] = cells[i].clone();
        }
        return clone;
    }

    private void verifyPhasesMatch(KanbanBoardRow otherRow) {
        if (otherRow.columns != columns) {
            throw new IllegalArgumentException("rows must contain the same phases");
        }
    }

    @Override
    public Iterator<KanbanCell> iterator() {
        return unmodifiableCollection(asList(cells)).iterator();
    }

    public KanbanCell getCell(int index) {
        return cells[index];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('|');
        for (KanbanCell cell : this) {
            builder.append(cell.toFixedWidthString());
            builder.append('|');
        }
        return builder.toString();
    }

    public KanbanBoardColumnList getColumns() {
        return columns;
    }
    
    public Collection<KanbanCell> listOfCellsOfType(WorkItemType type) {
        ArrayList<KanbanCell> list = new ArrayList<KanbanCell>();
        for(KanbanCell cell: cells)
        {
            if (!cell.isEmpty() && cell.getWorkItemType().equals(type)) {
                list.add(cell);
            }
        }
        return Collections.unmodifiableCollection(list);
    }
}
