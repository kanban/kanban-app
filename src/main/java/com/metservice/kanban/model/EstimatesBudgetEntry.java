package com.metservice.kanban.model;


public class EstimatesBudgetEntry {

    private WorkItem feature;
    private int averageCaseCumulativeCost;
    private int worstCaseCumulativeCost;
    private boolean overBudgetInWorstCase;
    private boolean overBudgetInAverageCase;
    private WorkItem nextFeature;
    private WorkItem prevFeature;

    public EstimatesBudgetEntry(WorkItem feature, WorkItem prevFeature, WorkItem nextFeature) {
        this.feature = feature;
        this.prevFeature = prevFeature;
        this.nextFeature = nextFeature;
    }

    public WorkItem getFeature() {
        return feature;
    }

    public void setAverageCaseCumulativeCost(int averageCaseCumulativeCost) {
        this.averageCaseCumulativeCost = averageCaseCumulativeCost;
    }

    public int getAverageCaseCumulativeCost() {
        return averageCaseCumulativeCost;
    }

    public void setWorstCaseCumulativeCost(int worstCaseCumulativeCost) {
        this.worstCaseCumulativeCost = worstCaseCumulativeCost;
    }

    public int getWorstCaseCumulativeCost() {
        return worstCaseCumulativeCost;
    }

    public void setOverBudgetInAverageCase(boolean overBudgetInAverageCase) {
        this.overBudgetInAverageCase = overBudgetInAverageCase;
    }

    public boolean isOverBudgetInAverageCase() {
        return overBudgetInAverageCase;
    }

    public void setOverBudgetInWorstCase(boolean overBudgetInWorstCase) {
        this.overBudgetInWorstCase = overBudgetInWorstCase;
    }

    public boolean isOverBudgetInWorstCase() {
        return overBudgetInWorstCase;
    }

    public WorkItem getNextFeature() {
        return nextFeature;
    }

    public WorkItem getPrevFeature() {
        return prevFeature;
    }
}
