package com.metservice.kanban.model;

import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import com.google.common.base.Preconditions;
import com.metservice.kanban.utils.WorkingDayUtils;

/**
 * A work item is a story. It can have a parent.
 *
 *
 */
public class WorkItem {
    public static final int ROOT_WORK_ITEM_ID = 0;

    private final int id;
    private final int parentId;
    private final WorkItemType type;
    private String name;
    private int averageCaseEstimate;
    private int worstCaseEstimate;
    private int importance;
    private String notes;
    private boolean excluded;
    private boolean blocked; //Whether a story is allowed to progress regardless of stage
    private HtmlColour colour;

    //Keep track of when this item started a phase
    private final Map<String, LocalDate> datesByPhase = new HashMap<String, LocalDate>();
    private String currentPhase;

    private boolean mustHave;

    private List<String> workStreams;

    private List<WorkItemComment> comments = new ArrayList<WorkItemComment>();

    public WorkItem(int id, WorkItemType type, String advanceToPhase) {
        this(id, ROOT_WORK_ITEM_ID, type, advanceToPhase);
    }

    public WorkItem(int id, int parentId, WorkItemType type, String advanceToPhase) {
        this(id, parentId, type);

        int targetPhaseIndex = type.getPhases().indexOf(advanceToPhase);
        if (targetPhaseIndex == -1) {
            throw new IllegalArgumentException("cannot advance; named phase does not exist: " + advanceToPhase);
        }

        for (int i = 0; i < targetPhaseIndex + 1; i++) {
            advance(parseIsoDate("1970-01-01"));
        }
    }

    public WorkItem(int id, WorkItemType workItemType) {
        this(id, ROOT_WORK_ITEM_ID, workItemType);
    }

    /**
     * Default constructor for WorkItem
     * @param id - id of the item we are creating
     * @param parentId - parent item's id
     * @param type - type of WorkItem
     */
    public WorkItem(int id, int parentId, WorkItemType type) {
        this.id = id;
        this.parentId = parentId;
        this.type = type;
        this.currentPhase = null;
        this.name = "";
        this.averageCaseEstimate = 0;
        this.importance = 0;
        this.notes = "";
        this.excluded = false;
        this.blocked = false;
        this.colour = new HtmlColour("FFFFFF");
        this.worstCaseEstimate = 0;
        this.mustHave = false;
    }

    public int getId() {
        return id;
    }

    public boolean isTopLevel() {
        return parentId == ROOT_WORK_ITEM_ID;
    }

    public int getParentId() {
        return parentId;
    }

    public WorkItemType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAverageCaseEstimate() {
        return averageCaseEstimate;
    }

    public void setAverageCaseEstimate(int averageCaseEstimate) {
        this.averageCaseEstimate = averageCaseEstimate;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getNotes() {
        return notes;
    }

    public String getNotesAndBlock() {
        if (isBlocked()) {
            return StringUtils.defaultIfEmpty(getNotes(), "") + "\n" + getLastBlockedComment();
        } else {
            return getNotes();
        }
    }

    public String getLastBlockedComment() {
        WorkItemComment lastComment = null;
        for (WorkItemComment c : getComments()) {
            if (isBlockedComment(c) && (lastComment == null || lastComment.getWhenAdded().isBefore(c.getWhenAdded()))) {
                lastComment = c;
            }
        }
        if (lastComment == null) {
            return "";
        }
        return lastComment.getCommentText() + " [" + lastComment.getAddedBy() + "]";
    }

    private boolean isBlockedComment(WorkItemComment c) {

        return c.getCommentText().startsWith("Blocked:");
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }

    public void setBlocked(boolean blocked) {
    	this.blocked = blocked;
    }

    public String getCurrentPhase() {
        if (currentPhase == null) {
            throw new IllegalStateException("work item is not yet in any phase");
        }
        return currentPhase;
    }

    public boolean hasDate(String phase) {
        return datesByPhase.containsKey(phase);
    }

    public LocalDate getDate(String phase) {
        return datesByPhase.get(phase);
    }

    public Map<String, LocalDate> getDatesByPhase() {
        return datesByPhase;
    }

    public void setDate(String phase, LocalDate date) {
        if (date == null) {
            datesByPhase.remove(phase);
        } else {
            datesByPhase.put(phase, date);
        }
        currentPhase = determineCurrentPhase();
    }

    public void setDateAsString(String phase, String dateAsString) {
        setDate(phase, parseIsoDate(dateAsString));
    }

    public String getPhaseOnDate(LocalDate date) {
        String phaseOnDate = null;
        for (String phase : type.getPhases()) {
            if (hasDate(phase) && !this.getDate(phase).isAfter(date)) {
                phaseOnDate = phase;
            }
        }
        return phaseOnDate;
    }

    public Map<String, Integer> getPhaseDurations() {
        Map<String, Integer> phaseDurations = new HashMap<String, Integer>();

        LocalDate previousDate = null;
        String previousPhase = null;
        LocalDate today = new LocalDate();

        // fill missing dates before current phase
        String newCurrentPhase = determineCurrentPhase();
        for (ListIterator<String> i = getType().getPhases().listIterator(getType().getPhases().size()); i.hasPrevious();) {

            String phase = i.previous();

            if (getDate(phase) != null) {
                previousDate = getDate(phase);
            }
            if (this.getType().isPhaseBefore(phase, newCurrentPhase) && getDate(phase) == null) {
                setDate(phase, previousDate);
            }
        }

        for (String phase : this.getType().getPhases()) {
            LocalDate date = this.getDate(phase);
            if (date == null) {
                date = today;
            }
            if (previousDate != null && previousPhase != null) {
                int diffInDays = WorkingDayUtils.getWorkingDaysBetween(previousDate, date);
                phaseDurations.put(previousPhase, diffInDays);
            }
            previousDate = date;
            previousPhase = phase;
        }
        //Last item will always have a duration between it's start date and now.
        if (previousDate != null && previousPhase != null) {
            int diffInDays = WorkingDayUtils.getWorkingDaysBetween(previousDate, today);
            phaseDurations.put(previousPhase, diffInDays);
        }

        return phaseDurations;
    }



    private String determineCurrentPhase() {
        String newCurrentPhase = null;
        for (String phase : type.getPhases()) {
            if (hasDate(phase)) {
                newCurrentPhase = phase;
            }
        }
        return newCurrentPhase;
    }

    public boolean isCompleted() {
    	return !type.hasPhaseAfter(currentPhase);
    }

    public boolean isBlocked() {
    	return blocked;
    }

    public void stop() {
        blocked = !blocked;
    }

    /**
     * Advance the phase of this item to the next phase in the phase list
     * @param date - the date the next phase has started (e.g. right now)
     */
    public void advance(LocalDate date) {
        if (!type.hasPhaseAfter(currentPhase)) {
            throw new IllegalStateException(this + " cannot advance: it is already in its final phase");
        }
        //Set the start date of the next phase to date
        setDate(type.getPhaseAfter(currentPhase), date);
    }

    /**
     * Returns a copy of this WorkItem, with a new parent.
     *
     * @param newParentId
     * @return
     */
    public WorkItem withNewParent(int newParentId) {
        WorkItem workItem = new WorkItem(id, newParentId, type);
        workItem.name = name;
        workItem.averageCaseEstimate = averageCaseEstimate;
        workItem.importance = importance;
        workItem.notes = notes;
        workItem.datesByPhase.putAll(datesByPhase);
        workItem.currentPhase = currentPhase;
        workItem.excluded = excluded;
        workItem.colour = colour;
        workItem.blocked = blocked;
        workItem.comments.addAll(comments);

        return workItem;
    }

    /**
     * Adds a new comment to the work item.
     *
     * @param comment
     *          The comment to add; mandatory.
     *
     * @throws NullPointerException
     *          If any of the mandatory parameters are {@code null}.
     */
    public void addComment(WorkItemComment comment) {
        Preconditions.checkNotNull(comment);
        comment.setParentId(id);
        this.comments.add(comment);
    }

    public List<WorkItemComment> getComments() {
        return Collections.unmodifiableList(this.comments);
    }

    public List<WorkItemComment> getCommentsInReverseOrder() {
        List<WorkItemComment> newList = new ArrayList<WorkItemComment>(comments);
        Collections.reverse(newList);
        return Collections.unmodifiableList(newList);
    }

    /**
     * Replaces the work items current list of comments with the specified list.
     * <p>
     * Not intended for general use.
     * </p>
     *
     * @param newComments
     *          the new comments.
     */
    public void resetCommentsAndReplaceWith(List<WorkItemComment> newComments) {
        this.comments.clear();
        if (newComments != null) {
            this.comments.addAll(newComments);
        }
    }

    @Override
    public String toString() {
        return "work item " + getId() + " (" + getName() + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof WorkItem) {
            return ((WorkItem) object).id == id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getTruncatedName() {
        if (name == null) {
            return null;
        }
        return name.substring(0, Math.min(name.length(), 40));
    }

    public void setColour(String colour) {
        if (colour != null && colour.length() > 0) {
            this.colour = new HtmlColour(colour);
        }
    }

    public HtmlColour getColour() {
        return colour;
    }

    public int getWorstCaseEstimate() {
        return worstCaseEstimate;
    }

    public void setWorstCaseEstimate(int worstCaseEstimate) {
        this.worstCaseEstimate = worstCaseEstimate;
    }

    public boolean isMustHave() {
        return mustHave;
    }

    public void setMustHave(boolean mustHave) {
        this.mustHave = mustHave;
    }

    public int getVariance() {
        int deviation = getWorstCaseEstimate() - getAverageCaseEstimate();
        return deviation * deviation;
    }

    public List<String> getWorkStreams() {
        return workStreams;
    }

    public void setWorkStreams(List<String> workStreams) {
        this.workStreams = workStreams;
    }

    public String getWorkStreamsAsString() {

        return StringUtils.join(workStreams, ',');
    }

    public void setWorkStreamsAsString(String workStream) {
        if (workStream == null) {
            this.workStreams = new ArrayList<String>();
        } else {
            this.workStreams = new ArrayList<String>();
            for (String ws : StringUtils.split(workStream, ',')) {
                this.workStreams.add(StringUtils.trim(ws));
            }
        }
    }

    public boolean isInWorkStream(String workStream) {
        // TODO this should rather ask the parent for work stream
        if (!isTopLevel()) {
            return true;
        }
        if (workStream == null || "".equals(workStream)) {
            return true;
        }
        return workStreams.contains(workStream);
    }
}
