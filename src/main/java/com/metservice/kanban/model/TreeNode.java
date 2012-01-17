package com.metservice.kanban.model;

import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.springframework.util.Assert;

public class TreeNode<T> {

    private final Class<T> valueClass;
    private final T value;
    private final TreeNode<T>[] children;

    public static <T> TreeNode<T> create(Class<T> valueClass, T value, TreeNode<?>... children) {
        return new TreeNode<T>(valueClass, value, children);
    }

    public static <T> TreeNode<T> create(Class<T> valueClass, T value, Collection<TreeNode<T>> children) {
        return new TreeNode<T>(valueClass, value, children.toArray(new TreeNode<?>[children.size()]));
    }

    private TreeNode(Class<T> valueClass, T value, TreeNode<?>[] children) {
        Assert.notNull(value, "value is null");

        this.valueClass = valueClass;
        this.value = value;
        this.children = cloneArray(valueClass, children);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private TreeNode<T>[] cloneArray(Class<T> valueClass, TreeNode<?>[] newChildren) {
        TreeNode[] clonedChildren = new TreeNode[newChildren.length];

        for (int i = 0; i < newChildren.length; i++) {

            TreeNode<T> child = (TreeNode<T>) newChildren[i];
            clonedChildren[i] = child;

            if (!valueClass.equals(child.valueClass)) {
                throw new ClassCastException("cannot add a " + child.valueClass + " to a tree of " + valueClass);
            }
        }
        return clonedChildren;
    }

    public T getValue() {
        return value;
    }

    public TreeNode<T> getChild(int index) {
        return children[index];
    }

    public int getNumberOfChildren() {
        return children.length;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public boolean hasChildren() {
        return children.length > 0;
    }

    public List<TreeNode<T>> getChildren() {
        return new ArrayList<TreeNode<T>>(asList(children));
    }

    public Iterator<T> depthFirstIterator() {
        return new TreeIterator<T>(this);
    }

    public int getNumberOfNodes() {
        return 0;
    }
}
