package com.metservice.kanban.model;

public class KanbanBoardColumn {
    private final WorkItemType type;
    private final String phase;

    public KanbanBoardColumn(WorkItemType type, String phase) {
        this.type = type;
        this.phase = phase;
    }

    public WorkItemType getWorkItemType() {
        return type;
    }
    
    public String getPhase() {
        return phase;
    }
    
    @Override
    public String toString() {
        return type + "/" + phase;
    }
}
