package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

public class WorkItemListTest {

    @Test
    public void testBasicNeeds() {
        // TODO break test up and rename
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItemType type = new WorkItemType();
        Collection<WorkItem> workItems = new ArrayList<WorkItem>();
        WorkItem workItem1 = new WorkItem(1, type);
        WorkItem workItem2 = new WorkItem(2, type);
        WorkItem workItem3 = new WorkItem(3, type);
        workItems.add(workItem1);
        workItems.add(workItem2);
        workItems.add(workItem3);
        List<WorkItem> workItemList = new ArrayList<WorkItem>(workItems);

        assertThat(workItemList.size(), is(3));

        assertThat(workItem1, is(workItemList.get(0)));
        assertThat(workItem2, is(workItemList.get(1)));
        assertThat(workItem3, is(workItemList.get(2)));

        assertThat(workItem1, is(workItemList.get(0)));
        assertThat(workItem2, is(workItemList.get(1)));
        assertThat(workItem3, is(workItemList.get(2)));

        Iterator<WorkItem> iterator = workItemList.iterator();
        assertThat(workItem1, is(iterator.next()));
        assertThat(workItem2, is(iterator.next()));
        assertThat(workItem3, is(iterator.next()));
    }
}
