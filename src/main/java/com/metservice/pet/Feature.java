package com.metservice.pet;

public class Feature {
	public static final int BLANK_ID = -1;

	private int id = BLANK_ID;
	private String description;
	private int bestGuessEstimate;
	private int worstCaseEstimate;
	
	public Feature() {		
	}

	public Feature(String description, int bestGuessEstimate, int worstCaseEstimate) {
		this.description = description;
		this.bestGuessEstimate = bestGuessEstimate;
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
		return bestGuessEstimate;
	}

	public void setBestGuessEstimate(int bestGuessEstimate) {
		this.bestGuessEstimate = bestGuessEstimate;
	}

	public int getWorstCaseEstimate() {
		return worstCaseEstimate;
	}

	public void setWorstCaseEstimate(int worstCaseEstimate) {
		this.worstCaseEstimate = worstCaseEstimate;
	}

	public int getVariance() {
		int deviation = worstCaseEstimate - bestGuessEstimate;
		return deviation * deviation;
	}
}
