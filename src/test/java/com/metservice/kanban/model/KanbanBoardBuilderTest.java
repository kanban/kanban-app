package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import java.util.Iterator;
import org.junit.Test;

public class KanbanBoardBuilderTest {

    @Test
    public void concatenatesItemsOfTheSameType() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem workItem1 = new WorkItem(1, type, "phase");
        WorkItem workItem2 = new WorkItem(2, type, "phase");
        WorkItemTypeCollection workItemTypes = new WorkItemTypeCollection(TreeNode.create(WorkItemType.class, type));

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(workItem1, workItem2);

        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(new KanbanBoardColumn(type, "phase")), workItemTypes, tree);

        KanbanBoard board = builder.build(null);

        assertThat(board.getCell(0, 0).getWorkItem(), is(workItem1));
        assertThat(board.getCell(1, 0).getWorkItem(), is(workItem2));
    }

    @Test
    public void mergesItemsOfDifferentTypes() {
        WorkItemType parentType = new WorkItemType("parent type phase");
        WorkItemType alphaType = new WorkItemType("alpha type phase");
        WorkItemType betaType = new WorkItemType("beta type phase");

        WorkItemTypeCollection workItemTypes = new WorkItemTypeCollection(
            TreeNode.create(WorkItemType.class, parentType,
                TreeNode.create(WorkItemType.class, alphaType),
                TreeNode.create(WorkItemType.class, betaType)));

        WorkItem workItem1 = new WorkItem(1, parentType, "parent type phase");
        WorkItem workItem2 = new WorkItem(2, 1, alphaType, "alpha type phase");
        WorkItem workItem3 = new WorkItem(3, 1, betaType, "beta type phase");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(workItem1, workItem2, workItem3);

        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(parentType, "parent type phase"),
                new KanbanBoardColumn(alphaType, "alpha type phase"),
                new KanbanBoardColumn(betaType, "beta type phase")),
            workItemTypes,
            tree);

        KanbanBoard board = builder.build(null);

        assertThat(board.getCell(0, 0).getWorkItem(), is(workItem1));
        assertThat(board.getCell(0, 1).getWorkItem(), is(workItem2));
        assertThat(board.getCell(0, 2).getWorkItem(), is(workItem3));
    }

    @Test
    public void packsBottomLevelOfWorkItems() {
        WorkItemType featureType = new WorkItemType("feature phase");
        WorkItemType storyType = new WorkItemType("phase 1", "phase 2");

        WorkItemTypeCollection workItemTypes = new WorkItemTypeCollection(
            TreeNode.create(WorkItemType.class, featureType,
                TreeNode.create(WorkItemType.class, storyType)));

        WorkItem feature = new WorkItem(1, featureType, "feature phase");
        WorkItem story1 = new WorkItem(2, 1, storyType, "phase 1");
        WorkItem story2 = new WorkItem(3, 1, storyType, "phase 2");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature, story1, story2);

        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(featureType, "feature phase"),
                new KanbanBoardColumn(storyType, "phase 1"),
                new KanbanBoardColumn(storyType, "phase 2")),
            workItemTypes,
            tree);
        KanbanBoard board = builder.build(null);

        assertThat(board.getCell(0, 0).getWorkItem(), is(feature));
        assertThat(board.getCell(0, 1).getWorkItem(), is(story1));
        assertThat(board.getCell(0, 2).getWorkItem(), is(story2));
    }

    @Test
    public void excludesPackedWorkItemsOutsideTheBoard() {
        WorkItemType featureType = new WorkItemType("feature phase");
        WorkItemType storyType = new WorkItemType("off-board story phase");

        WorkItemTypeCollection workItemTypes = new WorkItemTypeCollection(
            TreeNode.create(WorkItemType.class, featureType,
                TreeNode.create(WorkItemType.class, storyType)));

        WorkItem feature = new WorkItem(1, featureType, "feature phase");
        WorkItem story = new WorkItem(2, 1, storyType, "off-board story phase");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature, story);

        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(featureType, "feature phase")),
            workItemTypes,
            tree);
        KanbanBoard board = builder.build(null);

        Iterator<KanbanBoardRow> rowIterator = board.iterator();
        KanbanBoardRow topRow = rowIterator.next();
        Iterator<KanbanCell> topRowCellIterator = topRow.iterator();
        KanbanCell firstCell = topRowCellIterator.next();

        assertThat(firstCell.getWorkItem(), is(feature));
        assertThat(rowIterator.hasNext(), is(false));
        assertThat(topRowCellIterator.hasNext(), is(false));
    }

    @Test
    public void excludesStackedWorkItemsOutsideTheBoard() {
        WorkItemType featureType = new WorkItemType("on-board", "off-board");

        WorkItem feature1 = new WorkItem(1, featureType, "on-board");
        WorkItem feature2 = new WorkItem(2, featureType, "off-board");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2);

        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(featureType, "on-board")),
            new WorkItemTypeCollection(TreeNode.create(WorkItemType.class, featureType)),
            tree);
        KanbanBoard board = builder.build(null);

        Iterator<KanbanBoardRow> rowIterator = board.iterator();
        KanbanBoardRow topRow = rowIterator.next();
        Iterator<KanbanCell> topRowCellIterator = topRow.iterator();
        KanbanCell firstCell = topRowCellIterator.next();

        assertThat(firstCell.getWorkItem(), is(feature1));
        assertThat(rowIterator.hasNext(), is(false));
        assertThat(topRowCellIterator.hasNext(), is(false));
    }

    @Test
    public void setsWorkItemsAboveAndBelow() {
        WorkItemType featureType = new WorkItemType("phase");

        WorkItem feature1 = new WorkItem(1, featureType, "phase");
        WorkItem feature2 = new WorkItem(2, featureType, "phase");
        WorkItem feature3 = new WorkItem(3, featureType, "phase");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2, feature3);

        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(featureType, "phase")),
            new WorkItemTypeCollection(TreeNode.create(WorkItemType.class, featureType)),
            tree);
        KanbanBoard board = builder.build(null);

        assertThat(board.getCell(0, 0).getWorkItemAbove(), nullValue());
        assertThat(board.getCell(0, 0).getWorkItemBelow(), is(feature2));

        assertThat(board.getCell(1, 0).getWorkItemAbove(), is(feature1));
        assertThat(board.getCell(1, 0).getWorkItemBelow(), is(feature3));

        assertThat(board.getCell(2, 0).getWorkItemAbove(), is(feature2));
        assertThat(board.getCell(2, 0).getWorkItemBelow(), nullValue());
    }

    @Test
    public void packedWorkItemsInDifferentPhasesAreNotAdjacent() {
        WorkItemType featureType = new WorkItemType("feature phase");
        WorkItemType storyType = new WorkItemType("phase 1", "phase 2");

        WorkItem feature = new WorkItem(1, featureType, "feature phase");
        WorkItem story1 = new WorkItem(2, 1, storyType, "phase 1");
        WorkItem story2 = new WorkItem(3, 1, storyType, "phase 1");
        WorkItem story3 = new WorkItem(4, 1, storyType, "phase 2");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature, story1, story2, story3);

        WorkItemTypeCollection workItemTypes = new WorkItemTypeCollection(
            TreeNode.create(WorkItemType.class, featureType,
                TreeNode.create(WorkItemType.class, storyType)));

        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(featureType, "feature phase"),
                new KanbanBoardColumn(storyType, "phase 1"),
                new KanbanBoardColumn(storyType, "phase 2")),
            workItemTypes,
            tree);
        KanbanBoard board = builder.build(null);

        assertThat(board.getCell(0, 1).getWorkItemAbove(), nullValue());
        assertThat(board.getCell(0, 1).getWorkItemBelow(), is(story2));

        assertThat(board.getCell(1, 1).getWorkItemAbove(), is(story1));
        assertThat(board.getCell(1, 1).getWorkItemBelow(), nullValue());

        assertThat(board.getCell(0, 2).getWorkItemAbove(), nullValue());
        assertThat(board.getCell(0, 2).getWorkItemBelow(), nullValue());
    }

    @Test
    public void stackedWorkItemsInDifferentPhasesAreAdjacent() {
        WorkItemType featureType = new WorkItemType("phase 1", "phase 2");
        WorkItemType storyType = new WorkItemType();
        WorkItemTypeCollection workItemTypes = new WorkItemTypeCollection(TreeNode.create(WorkItemType.class,
            featureType, TreeNode.create(WorkItemType.class, storyType)));

        WorkItem feature1 = new WorkItem(1, featureType, "phase 1");
        WorkItem feature2 = new WorkItem(2, featureType, "phase 1");
        WorkItem feature3 = new WorkItem(2, featureType, "phase 2");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2, feature3);

        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(featureType, "phase 1"),
                new KanbanBoardColumn(featureType, "phase 2")),
            workItemTypes,
            tree);
        KanbanBoard board = builder.build(null);

        assertThat(board.getCell(0, 0).getWorkItemAbove(), nullValue());
        assertThat(board.getCell(0, 0).getWorkItemBelow(), is(feature2));

        assertThat(board.getCell(1, 0).getWorkItemAbove(), is(feature1));
        assertThat(board.getCell(1, 0).getWorkItemBelow(), is(feature3));

        assertThat(board.getCell(2, 1).getWorkItemAbove(), is(feature2));
        assertThat(board.getCell(2, 1).getWorkItemBelow(), nullValue());
    }

    @Test
    public void onlyWorkItemsOnTheBoardCanBeBeforeOrAfter() {
        WorkItemType type = new WorkItemType("on-board", "off-board");
        
        WorkItem workItem1 = new WorkItem(1, type, "on-board");
        WorkItem workItem2 = new WorkItem(2, type, "off-board");
        WorkItem workItem3 = new WorkItem(3, type, "on-board");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(workItem1, workItem2, workItem3);
        
        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(type, "on-board")),
            new WorkItemTypeCollection(TreeNode.create(WorkItemType.class, type)),
            tree);
        KanbanBoard board = builder.build(null);

        assertThat(board.getCell(0, 0).getWorkItemBelow(), is(workItem3));
        assertThat(board.getCell(1, 0).getWorkItemAbove(), is(workItem1));
    }

    @Test
    public void kanbanBacklogSeemsRight() {
        WorkItemType featureType = new WorkItemType("backlog", "dev");
        WorkItemType storyType = new WorkItemType("story backlog");
        
        WorkItem feature1 = new WorkItem(1, featureType, "backlog");
        WorkItem feature2 = new WorkItem(2, featureType, "dev");
        WorkItem feature3 = new WorkItem(3, featureType, "backlog");
        WorkItem story = new WorkItem(4, 3, storyType, "story backlog");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2, feature3, story);
        
        KanbanBoardBuilder builder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(featureType, "backlog")),
            new WorkItemTypeCollection(TreeNode.create(WorkItemType.class, featureType)),
            tree);
        KanbanBacklog backlog = builder.buildKanbanBacklog(null);
        
        assertThat(backlog.size(), is(2));
        assertThat(backlog.get(0).getWorkItemBelow(), is(feature3));
        assertThat(backlog.get(1).getWorkItemAbove(), is(feature1));
    }

}
