package com.metservice.kanban.model;

import org.junit.Test;

/**
 * Unit tests for the {@link WorkItemComment} class.
 */
public class WorkItemCommentTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsExceptionOnNullUser() {
        new WorkItemComment(null, "Cool new feature!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsExceptionOnNullCommentText() {
        new WorkItemComment("user", null);
    }

}
