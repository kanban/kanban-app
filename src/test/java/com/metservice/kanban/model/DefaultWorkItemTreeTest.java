package com.metservice.kanban.model;

import static com.metservice.kanban.model.WorkItem.ROOT_WORK_ITEM_ID;
import static com.metservice.kanban.tests.util.TestUtils.emptyWorkItemList;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import java.util.Collection;
import java.util.List;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;

public class DefaultWorkItemTreeTest {

    @Test
    public void workItemsCanBeDeleted() {
        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItem(new WorkItem(1, new WorkItemType()));
        tree.delete(1);

        assertThat(tree.getWorkItem(1), nullValue());
        assertThat(tree.getChildren(ROOT_WORK_ITEM_ID), emptyWorkItemList());
    }

    @Test(expected = IllegalStateException.class)
    public void itemsWithChildrenCannotBeDeleted() {
        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItem(new WorkItem(1, new WorkItemType()));
        tree.addWorkItem(new WorkItem(2, 1, new WorkItemType()));
        tree.delete(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonexistentWorkItemCannotBeDeleted() {
        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.delete(tree.getNewId());
    }

    @Test
    public void parentsSiblingsAreParentsAlternatives() {
        WorkItem story = new WorkItem(1, 2, new WorkItemType());
        WorkItem currentParent = new WorkItem(2, new WorkItemType());
        WorkItem alternativeParent1 = new WorkItem(3, new WorkItemType());
        WorkItem alternativeParent2 = new WorkItem(4, new WorkItemType());

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItem(story);
        tree.addWorkItem(currentParent);
        tree.addWorkItem(alternativeParent1);
        tree.addWorkItem(alternativeParent2);

        Iterable<WorkItem> alternativeParents = tree.getParentAlternatives(story);

        assertThat(alternativeParents, hasItems(currentParent, alternativeParent1, alternativeParent2));
    }

    @Test
    public void hasRootWorkItem() {
        DefaultWorkItemTree tree = new DefaultWorkItemTree();

        WorkItem root = tree.getRoot();

        assertThat(root.getName(), is("Top level"));
        assertThat(root.getId(), is(ROOT_WORK_ITEM_ID));
        assertThat(root.getParentId(), is(-1));
    }

    @Test
    public void topLevelWorkItemsHaveOnlyOneParentAlternative() {
        WorkItem story = new WorkItem(1, new WorkItemType());

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItem(story);

        Collection<WorkItem> parentAlternatives = tree.getParentAlternatives(story);

        assertThat(parentAlternatives, hasItem(tree.getRoot()));
    }

    @Test
    public void workItemsCanBeMovedToANewParent() {
        WorkItem story = new WorkItem(1, 2, new WorkItemType());
        WorkItem currentParent = new WorkItem(2, new WorkItemType());
        WorkItem newParent = new WorkItem(3, new WorkItemType());

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItem(story);
        tree.addWorkItem(currentParent);
        tree.addWorkItem(newParent);
        tree.reparent(story.getId(), newParent.getId());

        assertThat(tree.getChildren(currentParent.getId()), emptyWorkItemList());
        assertThat(tree.getChildren(newParent.getId()), hasItem(story));
        assertThat(tree.getWorkItem(story.getId()).getParentId(), is(newParent.getId()));
    }

    @Test
    public void multiplesWorkItemsCanBeAddedToATree() {
        WorkItem feature1 = new WorkItem(1, new WorkItemType());
        WorkItem feature2 = new WorkItem(2, new WorkItemType());

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2);

        assertThat(tree.getWorkItemList(), hasItems(feature1, feature2));
        assertThat(tree.getWorkItemList(), IsCollectionWithSize.hasSize(2));
    }

    @Test
    public void reorderCanBePerformed() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, type, "phase");
        WorkItem feature2 = new WorkItem(2, type, "phase");
        WorkItem feature3 = new WorkItem(3, type, "phase");
        WorkItem feature4 = new WorkItem(4, type, "phase");
        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2, feature3, feature4);
        tree.reorder(feature1, asList(feature2, feature3, feature1, feature4));
        assertThat(tree.getWorkItemList().get(2), is(feature1));
    }

    @Test
    public void reorderCanBePerformedInverseOrder() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, type, "phase");
        WorkItem feature2 = new WorkItem(2, type, "phase");
        WorkItem feature3 = new WorkItem(3, type, "phase");
        WorkItem feature4 = new WorkItem(4, type, "phase");
        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2, feature3, feature4);
        tree.reorder(feature4, asList(feature1, feature4, feature2, feature3));
        assertThat(tree.getWorkItemList().get(1), is(feature4));
    }
    
    @Test(expected = IllegalStateException.class)
    public void reorderCannotBePerformed() {
        WorkItemType type = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, type, "phase");
        WorkItem feature2 = new WorkItem(2, type, "phase");
        WorkItem feature3 = new WorkItem(3, type, "phase");
        WorkItem feature4 = new WorkItem(4, type, "phase");
        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2, feature3, feature4);
        tree.reorder(feature1, asList(feature1, feature3, feature2));
    }

    @Test
    public void getWorkItemsOfTypeWorksFineForNullOrEmptyWorkStreams() {
        WorkItemType featureType = new WorkItemType("phase");
        WorkItemType storyType = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, featureType, "phase");
        feature1.setWorkStreamsAsString("a,b");
        WorkItem feature2 = new WorkItem(2, featureType, "phase");
        feature2.setWorkStreamsAsString("b,c");
        WorkItem story1 = new WorkItem(3, storyType, "phase");
        story1.setWorkStreamsAsString("b,c");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2, story1);

        List<WorkItem> workItemsOfType = tree.getWorkItemsOfType(featureType, null);
        assertThat(workItemsOfType.size(), is(2));
        assertThat(workItemsOfType.get(0), is(feature1));
        assertThat(workItemsOfType.get(1), is(feature2));

        workItemsOfType = tree.getWorkItemsOfType(storyType, "");
        assertThat(workItemsOfType.size(), is(1));
        assertThat(workItemsOfType.get(0), is(story1));
    }

    @Test
    public void getWorkItemsOfTypeWorksFineForWorkStreams() {
        WorkItemType featureType = new WorkItemType("phase");
        WorkItemType storyType = new WorkItemType("phase");
        WorkItem feature1 = new WorkItem(1, featureType, "phase");
        feature1.setWorkStreamsAsString("a,b");
        WorkItem feature2 = new WorkItem(2, featureType, "phase");
        feature2.setWorkStreamsAsString("b,c");
        WorkItem story1 = new WorkItem(3, storyType, "phase");
        story1.setWorkStreamsAsString("b,c");

        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, feature2, story1);

        List<WorkItem> workItemsOfType = tree.getWorkItemsOfType(featureType, "b");
        assertThat(workItemsOfType.size(), is(2));
        assertThat(workItemsOfType.get(0), is(feature1));
        assertThat(workItemsOfType.get(1), is(feature2));

        workItemsOfType = tree.getWorkItemsOfType(featureType, "a");
        assertThat(workItemsOfType.size(), is(1));
        assertThat(workItemsOfType.get(0), is(feature1));
    }
}
