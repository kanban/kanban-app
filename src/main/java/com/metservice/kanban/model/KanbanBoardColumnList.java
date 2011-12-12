package com.metservice.kanban.model;

import static java.util.Arrays.asList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class KanbanBoardColumnList extends AbstractList<KanbanBoardColumn> {

    private final List<KanbanBoardColumn> columns;

    public KanbanBoardColumnList(KanbanBoardColumn... kanbanBoardColumn) {
        this(asList(kanbanBoardColumn));
    }

    public KanbanBoardColumnList(List<KanbanBoardColumn> columns) {
        this.columns = new ArrayList<KanbanBoardColumn>(columns);
    }

    @Override
    public KanbanBoardColumn get(int index) {
        return columns.get(index);
    }

    @Override
    public int size() {
        return columns.size();
    }

    public boolean containsPhase(String phase) {
        for (KanbanBoardColumn column : columns) {
            if (column.getPhase().equals(phase)) {
                return true;
            }
        }
        return false;
    }

    public int getIndexOfPhase(String phase) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getPhase().equals(phase)) {
                return i;
            }
        }
        throw new IllegalArgumentException("phase not in list: " + phase);
    }

    public List<WorkItem> filter(List<WorkItem> workItems) {
        List<WorkItem> filteredWorkItems = new ArrayList<WorkItem>();

        for (WorkItem workItem : workItems) {
            if (containsPhase(workItem.getCurrentPhase())) {
                filteredWorkItems.add(workItem);
            }
        }
        return new ArrayList<WorkItem>(filteredWorkItems);
    }

    @Override
    public String toString() {
        return columns.toString();
    }
}
