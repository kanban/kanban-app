package com.metservice.kanban.model;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class KanbanBacklog extends AbstractList<KanbanCell> {

    private final List<KanbanCell> cells;
    

    public KanbanBacklog(KanbanCell... cells) {
        this(asList(cells));
    }

    public KanbanBacklog(List<KanbanCell> cells) {
        this.cells = unmodifiableList(new ArrayList<KanbanCell>(cells));
    }
    
    @Override
    public KanbanCell get(int index) {
        return cells.get(index);
    }

    @Override
    public int size() {
        return cells.size();
    }


}
