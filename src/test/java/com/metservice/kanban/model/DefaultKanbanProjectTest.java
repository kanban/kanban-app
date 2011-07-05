package com.metservice.kanban.model;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;

public class DefaultKanbanProjectTest {

    @Test
    public void canDeleteWorkItems() {
        WorkItemTree tree = mock(WorkItemTree.class);

        DefaultKanbanProject project = new DefaultKanbanProject(null, null, tree, null);
        project.deleteWorkItem(7);

        verify(tree).delete(7);
    }

    @Test
    public void canReparentWorkItems() {
        WorkItemTree tree = mock(WorkItemTree.class);

        DefaultKanbanProject project = new DefaultKanbanProject(null, null, tree, null);
        project.reparentWorkItem(11, 2);

        verify(tree).reparent(11, 2);
    }
    
    @Test 
    public void reorderIsCalledProperly() {
        WorkItemTree tree = mock(WorkItemTree.class);
        WorkItem workItem1 = new WorkItem(1, new WorkItemType());
        WorkItem workItem2 = new WorkItem(2, new WorkItemType());
        WorkItem workItem3 = new WorkItem(3, new WorkItemType());
        
        when(tree.getWorkItem(1)).thenReturn(workItem1);
        when(tree.getWorkItem(2)).thenReturn(workItem2);
        when(tree.getWorkItem(3)).thenReturn(workItem3);
        
        DefaultKanbanProject project = new DefaultKanbanProject(null, null, tree, null);
        
        project.reorder(1, new Integer[] {2,1,3});
        verify(tree).reorder(workItem1, asList(workItem2, workItem1, workItem3));
        
        project.reorder(1, new Integer[] {2,3,1});
        verify(tree).reorder(workItem1, asList(workItem2, workItem3, workItem1));

        project.reorder(1, new Integer[] {1,2,3});
        verify(tree).reorder(workItem1, asList(workItem1, workItem2, workItem3));
        
    }
}
