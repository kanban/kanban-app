package com.metservice.pet;

public class DemoProjectFactory {

	public static Project createDemoProject() {
		Project project = new Project();
		
		project.setBudget(25000);
		project.setEstimatedCostPerPoint(900);
		project.setCostSoFar(10668);
		
		project.completeFeature(
			project.addFeature(new Feature("User can pick a capital city and get the current time", 5, 8)));
		project.completeFeature(
			project.addFeature(new Feature("User can log in with LAN username and password", 8, 13)));
		
		project.addFeature(new Feature("Displays current temperature", 3, 8));
		project.addFeature(new Feature("Displays current wind speed and direction", 5, 88));
		project.excludeFeature(project.addFeature(new Feature("Shows eWasp icon", 3, 13)));
		project.excludeFeature(project.addFeature(new Feature("Predicts the rugby", 3, 8)));
		project.excludeFeature(project.addFeature(new Feature("Makes coffee", 21, 55)));
		project.excludeFeature(project.addFeature(new Feature("Gives friendly error message if no data available", 1, 1)));		
		
		return project;		
	}
}
