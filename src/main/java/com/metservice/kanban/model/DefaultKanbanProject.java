package com.metservice.kanban.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import com.metservice.kanban.web.KanbanPersistence;

//TODO This class needs more unit tests.

/**
 * Chris n Janella
 */

public class DefaultKanbanProject implements KanbanProject {

    private final WorkItemTypeCollection workItemTypes;
    private final KanbanBoardConfiguration columnsByBoard;
    private final WorkItemTree tree;
    private final KanbanPersistence persistence;

    public DefaultKanbanProject(WorkItemTypeCollection workItemTypes, KanbanBoardConfiguration phaseSequences,
            WorkItemTree tree, KanbanPersistence persistence) {
        this.workItemTypes = workItemTypes;        
        this.columnsByBoard = phaseSequences;
        this.tree = tree;
        this.persistence = persistence;
    }

    /**
     * Clicking small arrows on wall to advance workitem
     * Moves workitem to nextphase and logs date of phase start
     */
    @Override
    public void advance(int id, LocalDate date) {
        tree.getWorkItem(id).advance(date);
    }

    
    /**
     * Adds work item to the project from the user or previously added ones in the csv file.
     * 
     */
    @Override
    public void addWorkItem(int parentId, WorkItemType type, String name, int size, int importance, String notes,
            LocalDate backlogDate) {
        int newId = tree.getNewId();
        WorkItem workItem = new WorkItem(newId, parentId, type);
        workItem.setName(name);
        workItem.setSize(size);
        workItem.setImportance(importance);
        workItem.setNotes(notes);
        tree.addWorkItem(workItem);

        advance(newId, backlogDate);
    }


    /**
     * Changes order in which the workitems are displayed on the wall
     */
    @Override
    public void move(int id, int targetId, boolean after) {
        tree.move(tree.getWorkItem(id), tree.getWorkItem(targetId), after);
    }

    @Override
    public KanbanBoardColumnList getColumns(BoardIdentifier boardType) {
        return columnsByBoard.get(boardType);
    }

    @Override
    public WorkItemTree getWorkItemTree() {
        return this.tree;
    }

    @Override
    public void deleteWorkItem(int id) {
        tree.delete(id);
    }

    @Override
    public void save() throws IOException {
        persistence.write(tree);
    }

    /**
     * Change parent of specified work item.
     */
    @Override
    public void reparentWorkItem(int id, int newParentId) {
        tree.reparent(id, newParentId);
    }

    /**
     * Gets current KanBan Board
     */
    @Override
    public KanbanBoard getBoard(BoardIdentifier boardType) {
        KanbanBoardBuilder kanbanBoardBuilder = new KanbanBoardBuilder(columnsByBoard.get(boardType), workItemTypes,
            tree);
        return kanbanBoardBuilder.build();
    }

    /**
     * Builds backlog screen
     */
    @Override
    public KanbanBacklog getBacklog() {
        // TODO Why a board builder to build the backlog screen?
        
        KanbanBoardBuilder kanbanBoardBuilder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(getRootWorkItemType(), getRootWorkItemType().getPhases().get(0))),
                workItemTypes, tree);
        KanbanBacklog backlog = kanbanBoardBuilder.buildKanbanBacklog();
        return backlog;
    }
/**
 * Gets top level type of workitems
 * @return
 */
    private WorkItemType getRootWorkItemType() {
        return workItemTypes.getRoot().getValue();
    }

    /**
     * Returns collection of all workitem types
     */
    @Override
    public WorkItemTypeCollection getWorkItemTypes() {
        return workItemTypes;
    }
    

    /**
     * called when order of backlog changed.
     */
    @Override
    public void reorder(Integer id, Integer[] newIdList) {
        List<WorkItem> list = new ArrayList<WorkItem>();
        for(Integer i: newIdList) {
            list.add(tree.getWorkItem(i));
        }
        
        tree.reorder(tree.getWorkItem(id), list);
    }
    
}
