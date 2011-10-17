package com.metservice.pet;

import com.metservice.kanban.model.WorkItem;

public class Feature {

    public static final int BLANK_ID = -1;

    private int id = BLANK_ID;
    private String description;
    private int bestCaseEstimate;
    private int worstCaseEstimate;
    private WorkItem workItem;

    private boolean mustHave;

    public Feature() {}

    public Feature(String description, int bestCaseEstimate, int worstCaseEstimate) {
        this.description = description;
        this.bestCaseEstimate = bestCaseEstimate;
        this.worstCaseEstimate = worstCaseEstimate;
    }

    public Feature(WorkItem wi) {
        this(wi.getName(), wi.getBestCaseEstimate(), wi.getWorstCaseEstimate());

        this.id = wi.getId();
        this.mustHave = wi.isMustHave();

        this.workItem = wi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBestCaseEstimate() {
        return bestCaseEstimate;
    }

    public void setBestCaseEstimate(int bestCaseEstimate) {
        this.bestCaseEstimate = bestCaseEstimate;
    }

    public int getWorstCaseEstimate() {
        return worstCaseEstimate;
    }

    public void setWorstCaseEstimate(int worstCaseEstimate) {
        this.worstCaseEstimate = worstCaseEstimate;
    }

    public int getVariance() {
        int deviation = worstCaseEstimate - bestCaseEstimate;
        return deviation * deviation;
    }

    public void setMustHave(boolean mustHave) {
        this.mustHave = mustHave;
    }

    public boolean isMustHave() {
        return mustHave;
    }
}
