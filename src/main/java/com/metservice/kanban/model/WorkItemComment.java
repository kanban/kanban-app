package com.metservice.kanban.model;

import java.io.Serializable;

import org.joda.time.LocalDateTime;
import org.springframework.util.Assert;

/**
 * A single comment that has been attached to a {@link WorkItem}.
 */
public class WorkItemComment implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The work items identifier the comment is for. */
    private int parentId;

    /** The Date/Time the comment was added. */
    private LocalDateTime whenAdded;

    /** The name of the person who added the comment. */
    private String addedBy;

    /** The actual text of the comment. */
    private String commentText;

    /**
     * Creates a new Comment object for the specified Work Item.
     *
     * @param addedBy
     *          The person who added the comment; mandatory.
     * @param commentText
     *          The contents of the comment mandatory.
     *
     * @throws IllegalArgumentException
     *          If any of the mandatory parameters are {@code null}.
     */
    public WorkItemComment(String addedBy, String commentText) {
        Assert.notNull(addedBy);
        Assert.notNull(commentText);

        this.whenAdded = new LocalDateTime();
        this.addedBy = addedBy;
        this.commentText = commentText;
    }

    /* package */ void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getWhenAdded() {
        return whenAdded;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public String getCommentText() {
        return commentText;
    }

    @Override
    public String toString() {
        return String.format("WorkItemComment [parentId=%s, whenAdded=%s, addedBy=%s, commentText=%s]",
                parentId, whenAdded, addedBy, commentText);
    }
}
