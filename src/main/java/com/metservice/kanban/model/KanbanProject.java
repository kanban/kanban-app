package com.metservice.kanban.model;

import java.io.IOException;
import java.util.Set;
import org.joda.time.LocalDate;


/**
 * The interface for a KanbanProject representation.
 * @author Janella Espinas, Chris Cooper
 */
public interface KanbanProject {

    void deleteWorkItem(int i);

    void save() throws IOException;

    void advance(int id, LocalDate date);
    
    void stop(int id);

    void addWorkItem(int parentId, WorkItemType type, String name, int size, int importance, String notes,
            String string, LocalDate backlogDate);

    void move(int id, int targetId, boolean after);

    void reorder(Integer id, Integer[] newIdList);

    WorkItemTree getWorkItemTree();

    KanbanBoardColumnList getColumns(BoardIdentifier boardType);
    
    void reparentWorkItem(int id, int newParentId);

    KanbanBacklog getBacklog();

    KanbanBoard getBoard(BoardIdentifier boardType);

    WorkItemTypeCollection getWorkItemTypes();
    
    String getJournalText();
    
    void writeJournalText(String journalText);

    Set<String> getWorkStreams();
}
