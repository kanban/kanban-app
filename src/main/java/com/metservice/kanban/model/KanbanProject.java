package com.metservice.kanban.model;

import java.io.IOException;
import org.joda.time.LocalDate;


/**
 * Object representing a kanban project
 * Chris n Janella.
 *
 */
public interface KanbanProject {

    void deleteWorkItem(int i);

    void save() throws IOException;

    void advance(int id, LocalDate date);

    void addWorkItem(int parentId, WorkItemType type, String name, int size, int importance, String notes,
            LocalDate backlogDate);

    void move(int id, int targetId, boolean after);

    void reorder(Integer id, Integer[] newIdList);

    WorkItemTree getWorkItemTree();

    KanbanBoardColumnList getColumns(BoardIdentifier boardType);
    
    void reparentWorkItem(int id, int newParentId);

    KanbanBacklog getBacklog();

    KanbanBoard getBoard(BoardIdentifier boardType);

    WorkItemTypeCollection getWorkItemTypes();
}
