package com.metservice.kanban.model;

import static com.metservice.kanban.model.TreeNode.create;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TreeNodeTest {

    @Test
    public void providesAccessToItsDataValue() {
        TreeNode<Integer> node = create(Integer.class, 17);

        assertThat(node.getValue(), is(17));
    }

    @Test
    public void providesIndexedAccessToChildren() {
        TreeNode<String> child0 = create(String.class, "child 0");
        TreeNode<String> child1 = create(String.class, "child 1");
        TreeNode<String> parent = create(String.class, "parent", child0, child1);

        assertThat(parent.getChild(0), is(child0));
        assertThat(parent.getChild(1), is(child1));
    }

    @Test
    public void cannotModifyChildrenThroughArrayArgumentToConstructor() {
        TreeNode<String> originalChild = create(String.class, "child 0");

        @SuppressWarnings("unchecked")
        TreeNode<String>[] children = new TreeNode[] {originalChild};

        TreeNode<String> parent = create(String.class, "parent", children);
        children[0] = create(String.class, "new child");

        assertThat(parent.getChild(0), is(originalChild));
    }

    @Test(expected = ClassCastException.class)
    public void throwsExceptionIfChildrenAreWrongType() {
        TreeNode<Integer> integerChild = create(Integer.class, 1);

        create(String.class, "text", integerChild);
    }
    
    @Test
    public void knowsTheNumberOfChildren() {
        TreeNode<String> child0 = create(String.class, "child 0");
        TreeNode<String> child1 = create(String.class, "child 1");
        
        assertThat(create(String.class, "parent with no children").getNumberOfChildren(), is(0));
        assertThat(create(String.class, "parent with one child", child0).getNumberOfChildren(), is(1));
        assertThat(create(String.class, "parent with two children", child0, child1).getNumberOfChildren(), is(2));
    }
    
    @Test
    public void useSameStringRepresentationAsItsValue() {
        assertThat(create(Integer.class, 17).toString(), is("17"));
        assertThat(create(Double.class, 11.0).toString(), is("11.0"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfValueIsNull() {
        create(Object.class, null);
    }
    
    @Test
    public void canBeConstructedWithACollection() {
        TreeNode<String> child0 = create(String.class, "child 0");
        TreeNode<String> child1 = create(String.class, "child 1");
        
        List<TreeNode<String>> children = new ArrayList<TreeNode<String>>();
        children.add(child0);
        children.add(child1);
        
        TreeNode<String> parent = create(String.class, "parent", children);
        
        assertThat(parent.getChild(0), is(child0));
        assertThat(parent.getChild(1), is(child1));
        assertThat(parent.getNumberOfChildren(), is(2));
    }
    
    @Test
    public void knowsWhetherItHasChildren() {
        TreeNode<String> child = create(String.class, "child");
        TreeNode<String> parent = create(String.class, "parent", child);
        
        assertThat(parent.hasChildren(), is(true));
        assertThat(child.hasChildren(), is(false));
    }
    
    @Test
    public void canProduceAListOfItsChildren() {
        TreeNode<String> child = create(String.class, "child");
        TreeNode<String> parent = create(String.class, "parent", child);
        
        assertThat(parent.getChildren(), hasItem(child));
    }
}
