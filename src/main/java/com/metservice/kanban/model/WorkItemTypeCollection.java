package com.metservice.kanban.model;

import java.util.AbstractCollection;
import java.util.Iterator;

//TODO This class needs unit tests.
public class WorkItemTypeCollection extends AbstractCollection<WorkItemType> {

    private TreeNode<WorkItemType> root;

    public WorkItemTypeCollection(TreeNode<WorkItemType> root) {
        this.root = root;
    }

    @Override
    public Iterator<WorkItemType> iterator() {
        return root.depthFirstIterator();
    }

    @Override
    public int size() {
        return root.getNumberOfNodes();
    }

    public WorkItemType getByName(String name) {
        for (WorkItemType workItem : this) {
            if (workItem.getName().equals(name)) {
                return workItem;
            }
        }
        throw new IllegalArgumentException("unrecognised work item type: " + name);
    }

    public TreeNode<WorkItemType> getRoot() {
        return root;
    }

    public TreeNode<WorkItemType> getTreeNode(WorkItemType type) {
        if (root.getValue().equals(type)) {
            return root;
        }
        for(TreeNode<WorkItemType> itemType: root.getChildren()) {
            if (itemType.getValue().equals(type)) {
                return itemType;
            }
        }
        return null;
    }
}
