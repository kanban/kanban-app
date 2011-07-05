package com.metservice.kanban.model;

import java.util.HashMap;
import java.util.Map;

//TODO This class needs unit tests.

public class KanbanBoardConfiguration {
    private final Map<BoardIdentifier, KanbanBoardColumnList> columnsByBoard = new HashMap<BoardIdentifier, KanbanBoardColumnList>();
    
    public void add(BoardIdentifier boardType, KanbanBoardColumnList columns) {
        columnsByBoard.put(boardType, columns);
    }

    public KanbanBoardColumnList get(BoardIdentifier boardType) {
        return columnsByBoard.get(boardType);
    }
}
