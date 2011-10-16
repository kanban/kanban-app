package com.metservice.pet;

public class BudgetEntry {
	private Feature feature;
	private boolean mustHave;
	private int bestGuessCumulativeCost;
	private int worstCaseCumulativeCost;
	private boolean overBudgetInWorstCase;
	private boolean overBudgetInBestGuess;
	
	public BudgetEntry() {
	}
	
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	
	public Feature getFeature() {
		return feature;
	}
	
	public void setMustHave(boolean mustHave) {
		this.mustHave = mustHave;
	}
	
	public boolean isMustHave() {
		return mustHave;
	}
	
	public void setBestGuessCumulativeCost(int bestGuessCumulativeCost) {
		this.bestGuessCumulativeCost = bestGuessCumulativeCost;
	}
	
	public int getBestGuessCumulativeCost() {
		return bestGuessCumulativeCost;
	}
	
	public void setWorstCaseCumulativeCost(int worstCaseCumulativeCost) {
		this.worstCaseCumulativeCost = worstCaseCumulativeCost;
	}
	
	public int getWorstCaseCumulativeCost() {
		return worstCaseCumulativeCost;
	}
	
	public void setOverBudgetInBestGuess(boolean overBudgetInBestGuess) {
		this.overBudgetInBestGuess = overBudgetInBestGuess;
	}
	
	public boolean isOverBudgetInBestGuess() {
		return overBudgetInBestGuess;
	}
	
	public void setOverBudgetInWorstCase(boolean overBudgetInWorstCase) {
		this.overBudgetInWorstCase = overBudgetInWorstCase;
	}
	
	public boolean isOverBudgetInWorstCase() {
		return overBudgetInWorstCase;
	}
}
