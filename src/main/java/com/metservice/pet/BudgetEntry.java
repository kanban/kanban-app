package com.metservice.pet;

public class BudgetEntry {

    private Feature feature;
    private int bestCaseCumulativeCost;
    private int worstCaseCumulativeCost;
    private boolean overBudgetInWorstCase;
    private boolean overBudgetInBestCase;
    private Feature nextFeature;
    private Feature prevFeature;

    public BudgetEntry() {}

    public void setFeature(Feature feature, Feature prevFeature, Feature nextFeature) {
        this.feature = feature;
        this.prevFeature = prevFeature;
        this.nextFeature = nextFeature;
    }

    public Feature getFeature() {
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

    public Feature getNextFeature() {
        return nextFeature;
    }

    public Feature getPrevFeature() {
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
