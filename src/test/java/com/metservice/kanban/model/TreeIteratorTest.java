package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class TreeIteratorTest {

    @Test
    public void visitsParentThenChildren() {
        TreeNode<Integer> child1 = TreeNode.create(Integer.class, 42);
        TreeNode<Integer> child2 = TreeNode.create(Integer.class, 33);
        TreeNode<Integer> parent = TreeNode.create(Integer.class, 17, child1, child2);

        TreeIterator<Integer> iterator = new TreeIterator<Integer>(parent);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(17));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(42));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(33));
        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void visitsGrandchildren() {
        TreeNode<Integer> grandchild1 = TreeNode.create(Integer.class, 23);
        TreeNode<Integer> grandchild2 = TreeNode.create(Integer.class, 27);
        TreeNode<Integer> child = TreeNode.create(Integer.class, 84, grandchild1, grandchild2);
        TreeNode<Integer> root = TreeNode.create(Integer.class, 9, child);

        TreeIterator<Integer> iterator = new TreeIterator<Integer>(root);
        iterator.next();
        iterator.next();

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(23));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(27));
        assertThat(iterator.hasNext(), is(false));
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void removeShouldNotBeImplemented() {
        TreeNode<Integer> root = TreeNode.create(Integer.class, 9);
        TreeIterator<Integer> iterator = new TreeIterator<Integer>(root);
        iterator.remove();
        
        
    }
}
