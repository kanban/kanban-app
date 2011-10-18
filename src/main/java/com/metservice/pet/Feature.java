package com.metservice.pet;

import com.metservice.kanban.model.WorkItem;

public class Feature {

    public static final int BLANK_ID = -1;

    private int id = BLANK_ID;
    private String description;

    private WorkItem workItem;

    public Feature() {}

    public Feature(int id, String description) {
        this.description = description;
        this.id = id;
    }

    public Feature(WorkItem wi) {
        this(wi.getId(), wi.getName());

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

    public int getVariance() {
        int deviation = workItem.getWorstCaseEstimate() - workItem.getBestCaseEstimate();
        return deviation * deviation;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }
}
