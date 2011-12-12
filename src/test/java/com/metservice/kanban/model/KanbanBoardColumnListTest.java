package com.metservice.kanban.model;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import java.util.Collections;
import java.util.List;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class KanbanBoardColumnListTest {

    private WorkItemType featureType;

    @Before
    public void before() {
        featureType = new WorkItemType("phase 1", "phase 2");
        featureType.setName("feature");
    }

    @Test
    public void canBeConstructedWithVarArgsColumns() {
        KanbanBoardColumn column1 = new KanbanBoardColumn(featureType, "phase 1");
        KanbanBoardColumn column2 = new KanbanBoardColumn(featureType, "phase 2");
        KanbanBoardColumnList columnList = new KanbanBoardColumnList(column1, column2);

        assertThat(columnList, hasItems(column1, column2));
    }

    @Test
    public void canBeConstructedWithAListOfColumns() {
        KanbanBoardColumn column1 = new KanbanBoardColumn(featureType, "phase 1");
        KanbanBoardColumn column2 = new KanbanBoardColumn(featureType, "phase 2");
        KanbanBoardColumnList columnList = new KanbanBoardColumnList(asList(column1, column2));

        assertThat(columnList, hasItems(column1, column2));
    }

    @Test
    public void canDetermineTheIndexOfAColumnFromItsPhase() {
        KanbanBoardColumn column1 = new KanbanBoardColumn(featureType, "phase 1");
        KanbanBoardColumn column2 = new KanbanBoardColumn(featureType, "phase 2");
        KanbanBoardColumnList columnList = new KanbanBoardColumnList(asList(column1, column2));

        assertThat(columnList.getIndexOfPhase("phase 1"), is(0));
        assertThat(columnList.getIndexOfPhase("phase 2"), is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsAnExceptionWhenAskedForTheIndexOfAMissingPhase() {
        KanbanBoardColumnList columnList = new KanbanBoardColumnList(Collections.<KanbanBoardColumn> emptyList());

        columnList.getIndexOfPhase("missing phase");
    }

    @Test
    public void knowsWhetherItContainsAColumnWithASpecificPhase() {
        KanbanBoardColumn column = new KanbanBoardColumn(featureType, "phase 1");
        KanbanBoardColumnList columnList = new KanbanBoardColumnList(column);

        assertThat(columnList.containsPhase("phase 1"), is(true));
        assertThat(columnList.containsPhase("missing phase"), is(false));
    }

    @Test
    public void canFilterAWorkItemListToExcludeWorkItemsFromMissingPhases() {
        WorkItem workItem1 = new WorkItem(1, featureType);
        workItem1.advance(new LocalDate());

        WorkItem workItem2 = new WorkItem(2, featureType);
        workItem2.advance(new LocalDate());
        workItem2.advance(new LocalDate());

        List<WorkItem> workItems = asList(workItem1, workItem2);

        KanbanBoardColumn column = new KanbanBoardColumn(featureType, "phase 1");
        KanbanBoardColumnList columnList = new KanbanBoardColumnList(column);

        List<WorkItem> filteredWorkItems = columnList.filter(workItems);

        assertThat(filteredWorkItems, hasItem(workItem1));
        assertThat(filteredWorkItems, not(hasItem(workItem2)));
    }
}
