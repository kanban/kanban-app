package com.metservice.pet;

public class BudgetEntry {
	private Feature feature;
	private int bestCaseCumulativeCost;
	private int worstCaseCumulativeCost;
	private boolean overBudgetInWorstCase;
	private boolean overBudgetInBestCase;
	
	public BudgetEntry() {
	}
	
	public void setFeature(Feature feature) {
		this.feature = feature;
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
}
