package com.metservice.kanban.model;

import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import com.metservice.kanban.utils.WorkingDayUtils;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.LocalDate;

/**
 * A work item is a story. It can have a parent.
 *
 *
 */
public class WorkItem {

    private final int id;
    private final int parentId;
    private final WorkItemType type;
    private String name;
    private int size;
    private int importance;
    private String notes;
    private boolean excluded;
    private boolean stopped; //Whether a story is allowed to progress regardless of stage

    //Keep track of when this item started a phase
    private final Map<String, LocalDate> datesByPhase = new HashMap<String, LocalDate>();
    private String currentPhase;
    public static final int ROOT_WORK_ITEM_ID = 0;

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
        this.size = 0;
        this.importance = 0;
        this.notes = "";
        this.excluded = false;
        this.stopped = false;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
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
        String currentPhase = null;
        for (String phase : type.getPhases()) {
            if (hasDate(phase)) {
                currentPhase = phase;
            }
        }
        return currentPhase;
    }

    public boolean isCompleted() {
    	return !type.hasPhaseAfter(currentPhase);
    }
    
    public boolean isStopped() {
    	return stopped;
    }
    
    public void stop() {
    	if (stopped==true) { stopped = false; }
    	else stopped = true;
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
        workItem.size = size;
        workItem.importance = importance;
        workItem.notes = notes;
        workItem.datesByPhase.putAll(datesByPhase);
        workItem.currentPhase = currentPhase;
        workItem.excluded = excluded;
        workItem.stopped = stopped;

        return workItem;
    }

    @Override
    public String toString() {
        return "work item " + getId() + " (" + getName() + ")";
    }

    @Override
    public boolean equals(Object object) {
        return object != null && ((WorkItem) object).id == id;
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
}
