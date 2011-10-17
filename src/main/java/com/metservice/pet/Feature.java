package com.metservice.pet;

public class Feature {
	public static final int BLANK_ID = -1;

	private int id = BLANK_ID;
	private String description;
	private int bestCaseEstimate;
	private int worstCaseEstimate;
	
	public Feature() {		
	}

	public Feature(String description, int bestGuessEstimate, int worstCaseEstimate) {
		this.description = description;
		this.bestCaseEstimate = bestGuessEstimate;
		this.worstCaseEstimate = worstCaseEstimate;
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

	public int getBestGuessEstimate() {
		return bestCaseEstimate;
	}

	public void setBestGuessEstimate(int bestGuessEstimate) {
		this.bestCaseEstimate = bestGuessEstimate;
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
}
