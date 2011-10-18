package com.metservice.pet;

import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;

public class Project {

    private final List<Feature> plannedFeatures = new ArrayList<Feature>();
    private int numberOfIncludedFeatures = 0;

    private final List<Feature> completedFeatures = new ArrayList<Feature>();

    private int budget = 0;
    private int estimatedCostPerPoint = 0;
    private int costSoFar = 0;

    private int nextFeatureId = 0;

    private String projectName;

    private KanbanProject kanbanProject;

    public Feature getFeature(int id) {
        return plannedFeatures.get(getIndexOfFeature(id));
    }

    public int addFeature(Feature feature) {
        int id = nextFeatureId;
        nextFeatureId++;

        feature.setId(id);
        plannedFeatures.add(numberOfIncludedFeatures, feature);
        numberOfIncludedFeatures++;

        return id;
    }

    public void setFeature(int id, Feature feature) {
        plannedFeatures.set(getIndexOfFeature(id), feature);
    }

    private int getIndexOfFeature(int id) {
        for (int index = 0; index < plannedFeatures.size(); index++) {
            if (plannedFeatures.get(index).getId() == id) {
                return index;
            }
        }
        throw new NoSuchElementException("feature id = " + id);
    }

    public List<BudgetEntry> getBudgetEntries() {
        List<BudgetEntry> entries = new ArrayList<BudgetEntry>();

        for (int i = 0; i < plannedFeatures.size(); i++) {
            Feature previous = null;
            Feature next = null;
            if (i > 0) {
                previous = plannedFeatures.get(i - 1);
            }
            if (i < plannedFeatures.size() - 1) {
                next = plannedFeatures.get(i + 1);
            }
            Feature current = plannedFeatures.get(i);
            BudgetEntry entry = new BudgetEntry();

            entry.setFeature(current, previous, next);
            entries.add(entry);
        }


        calculateCumulativeCostBestGuess(entries);
        calculateCumulativeCostWorstCase(entries);
        identifyBudgetOverruns(entries);

        return entries;
    }

    private void calculateCumulativeCostBestGuess(List<BudgetEntry> entries) {
        int cumulativePoints = 0;

        for (BudgetEntry entry : entries) {
            cumulativePoints += entry.getFeature().getWorkItem().getBestCaseEstimate();

            entry.setBestCaseCumulativeCost(costSoFar + cumulativePoints * estimatedCostPerPoint);
        }
    }

    private void calculateCumulativeCostWorstCase(List<BudgetEntry> entries) {
        int cumulativePointVariance = 0;

        for (BudgetEntry entry : entries) {
            cumulativePointVariance += entry.getFeature().getVariance();

            int buffer = (int) round(sqrt(cumulativePointVariance) * estimatedCostPerPoint);
            entry.setWorstCaseCumulativeCost(entry.getBestCaseCumulativeCost() + buffer);
        }
    }

    private void identifyBudgetOverruns(List<BudgetEntry> entries) {
        for (BudgetEntry entry : entries) {
            if (entry.getBestCaseCumulativeCost() > budget) {
                entry.setOverBudgetInBestCase(true);
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
        return round((float) getCostSoFar() / (float) getCompletedPoints());
    }

    private int getCompletedPoints() {
        int sum = 0;
        for (Feature feature : completedFeatures) {
            sum += feature.getWorkItem().getBestCaseEstimate();
        }
        return sum;
    }

    public void excludeFeature(int id) {
        int index = getIndexOfFeature(id);
        if (index >= numberOfIncludedFeatures) {
            throw new IllegalArgumentException("already excluded: feature id = " + id);
        }

        plannedFeatures.add(numberOfIncludedFeatures - 1, plannedFeatures.remove(index));
        numberOfIncludedFeatures--;
    }

    public void includeFeature(int id) {
        int index = getIndexOfFeature(id);
        if (index < numberOfIncludedFeatures) {
            throw new IllegalArgumentException("already included: feature id = " + id);
        }

        plannedFeatures.add(numberOfIncludedFeatures, plannedFeatures.remove(index));
        numberOfIncludedFeatures++;
    }

    public void moveFeatureUp(int id) {
        int index = getIndexOfFeature(id);
        if (index <= 0) {
            throw new IllegalArgumentException("already at top: featrue id = " + id);
        }

        Feature feature = plannedFeatures.remove(index);
        plannedFeatures.add(index - 1, feature);
    }

    public void moveFeatureDown(int id) {
        int index = getIndexOfFeature(id);
        if (index >= plannedFeatures.size() - 1) {
            throw new IllegalArgumentException("already at bottom: id = " + id);
        }

        Feature feature = plannedFeatures.remove(index);
        plannedFeatures.add(index + 1, feature);
    }

    public void completeFeature(int id) {
        int index = getIndexOfFeature(id);
        Feature feature = plannedFeatures.remove(index);

        if (index < numberOfIncludedFeatures) {
            numberOfIncludedFeatures--;
        }
        completedFeatures.add(feature);
    }

    public List<Feature> getCompletedFeatures() {
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
        }
        else {
            List<WorkItem> workItemList = kanbanProject.getWorkItemTree().getWorkItemList();

            for (WorkItem wi : workItemList) {
                if (wi.getType().getName().equalsIgnoreCase("feature")) {

                    Feature f = new Feature(wi);

                    if (wi.isCompleted()) {
                        completedFeatures.add(f);
                    }
                    else {
                        plannedFeatures.add(f);
                    }
                }
            }
        }

    }

    public KanbanProject getKanbanProject() {
        return kanbanProject;
    }
}
