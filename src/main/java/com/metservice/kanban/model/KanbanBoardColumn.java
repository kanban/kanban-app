package com.metservice.kanban.model;

/**
 * 
 * 
 * WIP Limit by Nicholas Malcolm and Chris Cooper
 *
 */
public class KanbanBoardColumn {
    private final WorkItemType type;
    private final String phase;
    private int wipLimit;

    public KanbanBoardColumn(WorkItemType type, String phase) {
        this.type = type;
        this.phase = phase;
        this.wipLimit = -1;
    }
    
    public KanbanBoardColumn(WorkItemType type, String phase, int wipLimit) {
        this.type = type;
        this.phase = phase;
        this.wipLimit = wipLimit;
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
    
    public int getWIPLimit(){
    	return wipLimit;
    }

	public void setWIPLimit(int columnLimit) {
		this.wipLimit = columnLimit;
	}
}
