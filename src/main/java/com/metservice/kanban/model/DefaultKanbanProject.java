package com.metservice.kanban.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import com.metservice.kanban.web.KanbanPersistence;

//TODO This class needs more unit tests.

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

    @Override
    public void advance(int id, LocalDate date) {
        tree.getWorkItem(id).advance(date);
    }

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

    @Override
    public void reparentWorkItem(int id, int newParentId) {
        tree.reparent(id, newParentId);
    }

    @Override
    public KanbanBoard getBoard(BoardIdentifier boardType) {
        KanbanBoardBuilder kanbanBoardBuilder = new KanbanBoardBuilder(columnsByBoard.get(boardType), workItemTypes,
            tree);
        return kanbanBoardBuilder.build();
    }

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

    private WorkItemType getRootWorkItemType() {
        return workItemTypes.getRoot().getValue();
    }

    @Override
    public WorkItemTypeCollection getWorkItemTypes() {
        return workItemTypes;
    }

    @Override
    public void reorder(Integer id, Integer[] newIdList) {
        List<WorkItem> list = new ArrayList<WorkItem>();
        for(Integer i: newIdList) {
            list.add(tree.getWorkItem(i));
        }
        
        tree.reorder(tree.getWorkItem(id), list);
    }
    
}
