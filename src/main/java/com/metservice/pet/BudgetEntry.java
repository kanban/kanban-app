package com.metservice.pet;

import com.metservice.kanban.model.WorkItem;

public class BudgetEntry {

    private WorkItem feature;
    private int bestCaseCumulativeCost;
    private int worstCaseCumulativeCost;
    private boolean overBudgetInWorstCase;
    private boolean overBudgetInBestCase;
    private WorkItem nextFeature;
    private WorkItem prevFeature;

    public BudgetEntry() {}

    public void setFeature(WorkItem feature, WorkItem prevFeature, WorkItem nextFeature) {
        this.feature = feature;
        this.prevFeature = prevFeature;
        this.nextFeature = nextFeature;
    }

    public WorkItem getFeature() {
        return feature;
    }

    public void setBestCaseCumulativeCost(int bestCaseCumulativeCost) {
        this.bestCaseCumulativeCost = bestCaseCumulativeCost;
    }

    public int getBestCaseCumulativeCost() {
        return bestCaseCumulativeCost;
    }

    public void setWorstCaseCumulativeCost(int worstCaseCumulativeCost) {
        this.worstCaseCumulativeCost = worstCaseCumulativeCost;
    }

    public int getWorstCaseCumulativeCost() {
        return worstCaseCumulativeCost;
    }

    public void setOverBudgetInBestCase(boolean overBudgetInBestCase) {
        this.overBudgetInBestCase = overBudgetInBestCase;
    }

    public boolean isOverBudgetInBestCase() {
        return overBudgetInBestCase;
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

    public boolean getCanChangeImportance() {
        // if it's the last feature and is must, can be changed to nice
        if (nextFeature == null && feature.isMustHave()) {
            return true;
        }

        if (prevFeature == null && !feature.isMustHave()) {
            return true;
        }

        if (nextFeature != null && feature.isMustHave() != nextFeature.isMustHave()) {
            return true;
        }

        if (prevFeature != null && feature.isMustHave() != prevFeature.isMustHave()) {
            return true;
        }
        return false;
    }
}
