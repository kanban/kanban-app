package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class KanbanBacklogTest {
    
    @Test
    public void kanbanBacklogBehavesLikeAList() {
        WorkItemType type = new WorkItemType("backlog");
        
        List<KanbanCell> list = new ArrayList<KanbanCell>();
        list.add(new KanbanCell(type));
        list.add(new KanbanCell(type));
        list.add(new KanbanCell(type));
        
        KanbanBacklog backlog = new KanbanBacklog(list);
        assertThat(backlog.size(), is(3));
        assertThat(backlog.get(1), is(list.get(1)));
    }

    @Test
    public void kanbanBacklogDoesnAllowChangeInTheData() {
        WorkItemType type = new WorkItemType("backlog");
        
        List<KanbanCell> list = new ArrayList<KanbanCell>();
        list.add(new KanbanCell(type));
        list.add(new KanbanCell(type));
        list.add(new KanbanCell(type));
        
        KanbanBacklog backlog = new KanbanBacklog(list);
        
        list.add(new KanbanCell(type));
        
        assertThat(backlog.size(), is(3));
    }
}
