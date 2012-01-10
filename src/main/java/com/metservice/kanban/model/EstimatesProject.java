package com.metservice.kanban.model;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class EstimatesProject {

    private final List<WorkItem> plannedFeatures = new ArrayList<WorkItem>();

    private final List<WorkItem> completedFeatures = new ArrayList<WorkItem>();

    private int budget = 0;
    private int estimatedCostPerPoint = 0;
    private int costSoFar = 0;

    private String projectName;

    private KanbanProject kanbanProject;

    public WorkItem getFeature(int id) {
        return plannedFeatures.get(getIndexOfFeature(id));
    }

    private int getIndexOfFeature(int id) {
        for (int index = 0; index < plannedFeatures.size(); index++) {
            if (plannedFeatures.get(index).getId() == id) {
                return index;
            }
        }
        throw new NoSuchElementException("feature id = " + id);
    }

    public List<EstimatesBudgetEntry> getBudgetEntries() {
        List<EstimatesBudgetEntry> entries = new ArrayList<EstimatesBudgetEntry>();

        for (int i = 0; i < plannedFeatures.size(); i++) {
            WorkItem previous = null;
            WorkItem next = null;
            if (i > 0) {
                previous = plannedFeatures.get(i - 1);
            }
            if (i < plannedFeatures.size() - 1) {
                next = plannedFeatures.get(i + 1);
            }
            WorkItem current = plannedFeatures.get(i);
            EstimatesBudgetEntry entry = new EstimatesBudgetEntry(current, previous, next);

            entries.add(entry);
        }


        calculateCumulativeCostAverageGuess(entries);
        calculateCumulativeCostWorstCase(entries);
        identifyBudgetOverruns(entries);

        return entries;
    }

    private void calculateCumulativeCostAverageGuess(List<EstimatesBudgetEntry> entries) {
        int cumulativePoints = 0;

        for (EstimatesBudgetEntry entry : entries) {
            cumulativePoints += entry.getFeature().getAverageCaseEstimate();

            entry.setAverageCaseCumulativeCost(costSoFar + cumulativePoints * estimatedCostPerPoint);
        }
    }

    private void calculateCumulativeCostWorstCase(List<EstimatesBudgetEntry> entries) {
        int cumulativePointVariance = 0;

        for (EstimatesBudgetEntry entry : entries) {
            cumulativePointVariance += entry.getFeature().getVariance();

            int buffer = (int) round(sqrt(cumulativePointVariance) * estimatedCostPerPoint);
            entry.setWorstCaseCumulativeCost(entry.getAverageCaseCumulativeCost() + buffer);
        }
    }

    private void identifyBudgetOverruns(List<EstimatesBudgetEntry> entries) {
        for (EstimatesBudgetEntry entry : entries) {
            if (entry.getAverageCaseCumulativeCost() > budget) {
                entry.setOverBudgetInAverageCase(true);
            }
            if (entry.getWorstCaseCumulativeCost() > budget) {
                entry.setOverBudgetInWorstCase(true);
            }
        }
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getBudget() {
        return budget;
    }

    public void setCostSoFar(int costSoFar) {
        this.costSoFar = costSoFar;
    }

    public int getCostSoFar() {
        return costSoFar;
    }

    public void setEstimatedCostPerPoint(int estimatedCostPerPoint) {
        this.estimatedCostPerPoint = estimatedCostPerPoint;
    }

    public int getEstimatedCostPerPoint() {
        return estimatedCostPerPoint;
    }

    public int getCostPerPointSoFar() {
        if (getCompletedPoints() == 0) {
            return 0;
        }
        return round((float) getCostSoFar() / (float) getCompletedPoints());
    }

    private int getCompletedPoints() {
        int sum = 0;
        for (WorkItem feature : completedFeatures) {
            sum += feature.getAverageCaseEstimate();
        }
        return sum;
    }

    public List<WorkItem> getCompletedFeatures() {
        return completedFeatures;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String name) {
        this.projectName = name;
    }

    public void setKanbanProject(KanbanProject kanbanProject) {
        this.kanbanProject = kanbanProject;

        if (kanbanProject == null) {
            completedFeatures.clear();
            plannedFeatures.clear();
        } else {
            List<WorkItem> workItemList = kanbanProject.getWorkItemTree().getWorkItemList();

            for (WorkItem wi : workItemList) {
                if (wi.isTopLevel()) {
                    if (wi.isCompleted()) {
                        completedFeatures.add(wi);
                    } else {
                        plannedFeatures.add(wi);
                    }
                }
            }
        }
    }

    public KanbanProject getKanbanProject() {
        return kanbanProject;
    }
}
