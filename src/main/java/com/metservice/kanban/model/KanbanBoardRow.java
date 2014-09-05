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

    private KanbanCell[] createEmptyCellArray(KanbanBoardColumnList columnList) {
        KanbanCell[] array = new KanbanCell[columnList.size()];

        int i = 0;
        for (KanbanBoardColumn column : columnList) {
            array[i] = new KanbanCell(column.getWorkItemType());
            i++;
        }
        return array;
    }

    public boolean canAdd(WorkItem workItem) {
        String phase = workItem.getCurrentPhase();
        int index = columns.getIndexOfPhase(phase);

        return cells[index].isEmptyCell();
    }

    public void insert(WorkItem workItem, WorkItem workItemAbove, WorkItem workItemBelow, WorkItem workItemTop) {
        String phase = workItem.getCurrentPhase();
        
        int index = columns.getIndexOfPhase(phase);

        KanbanCell cell = cells[index];
        cell.setWorkItem(workItem);
        cell.setWorkItemAbove(workItemAbove);
        cell.setWorkItemBelow(workItemBelow);
        cell.setWorkItemTop(workItemTop);
    }

    public void merge(KanbanBoardRow otherRow) {
        verifyPhasesMatch(otherRow);

        for (int i = 0; i < otherRow.cells.length; i++) {
            KanbanCell cell = otherRow.cells[i];
            if (!cell.isEmptyCell()) {
                cells[i] = cell.clone();
            }
        }
    }

    public boolean hasItemOfType(WorkItemType type) {
        for (KanbanCell cell : cells) {
            if (!cell.isEmptyCell() && cell.getWorkItemType().equals(type)) {
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
        for (KanbanCell cell : cells) {
            if (!cell.isEmptyCell() && cell.getWorkItemType().equals(type)) {
                list.add(cell);
            }
        }
        return Collections.unmodifiableCollection(list);
    }

    // required by JSP as c:forEach cannot iterate using Iterable
    public Iterator<KanbanCell> getIterator() {
        return iterator();
    }
}