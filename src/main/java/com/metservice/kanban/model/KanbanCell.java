package com.metservice.kanban.model;

import static java.lang.String.format;
import org.apache.commons.lang.UnhandledException;

//TODO This class needs unit tests.

public class KanbanCell implements Cloneable {
    
    private final WorkItemType workItemType;
    private WorkItem workItem;
    private WorkItem workItemAbove;
    private WorkItem workItemBelow;

    public KanbanCell(WorkItemType workItemType) {
        this.workItemType = workItemType;
    }

    public boolean isEmptyCell() {
        return workItem == null;
    }
    
    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItemAbove(WorkItem workItemAbove) {
        this.workItemAbove = workItemAbove;
    }

    public WorkItem getWorkItemAbove() {
        return workItemAbove;
    }

    public void setWorkItemBelow(WorkItem workItemBelow) {
        this.workItemBelow = workItemBelow;
    }

    public WorkItem getWorkItemBelow() {
        return workItemBelow;
    }
    
    public WorkItemType getWorkItemType() {
        return workItemType;
    }

    public void setWorkItem(WorkItem workItem) {
        if (!isEmptyCell()) {
            throw new IllegalArgumentException("work item already exists in this position");
        }
        this.workItem = workItem;
    }
    
  
    
    @Override
    protected KanbanCell clone() {
        try {
            return (KanbanCell) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnhandledException(e);
        }
    }
    
    @Override
    public String toString() {
        return isEmptyCell() ? "empty cell" : workItem.toString();
    }

    public String toFixedWidthString() {
        return isEmptyCell() ? "   " : format("%3d", workItem.getId());
    }
}
