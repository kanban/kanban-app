package com.metservice.kanban.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.metservice.kanban.model.EstimatesBudgetEntry;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;


public class EstimatesProjectTest {

    EstimatesProject project;
    KanbanProject kanbanProject;
    List<WorkItem> workItems;
    WorkItemTree tree;
    WorkItem workItem0, workItem5, workItem10;

    @Before
    public void setUp() {

        kanbanProject = mock(KanbanProject.class);
        tree = mock(WorkItemTree.class);
        workItems = new ArrayList<WorkItem>();

        when(kanbanProject.getWorkItemTree()).thenReturn(tree);
        when(tree.getWorkItemList()).thenReturn(workItems);

        WorkItemType type = new WorkItemType("Backkog", "Complete");

        workItem0 = new WorkItem(0, type);
        workItem0.setAverageCaseEstimate(5);
        workItem0.setWorstCaseEstimate(10);

        workItem5 = new WorkItem(5, type);
        workItem5.setAverageCaseEstimate(6);
        workItem5.setWorstCaseEstimate(12);

        workItem10 = new WorkItem(10, type);
        workItem10.setAverageCaseEstimate(7);
        workItem10.setWorstCaseEstimate(14);

        workItem0.advance(LocalDate.fromCalendarFields(Calendar.getInstance()));
        workItem0.advance(LocalDate.fromCalendarFields(Calendar.getInstance()));

        workItems.add(workItem0);
        workItems.add(workItem5);
        workItems.add(workItem10);

        project = new EstimatesProject();
        project.setEstimatedCostPerPoint(10);
        project.setKanbanProject(kanbanProject);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void getFeatureTest() {
        assertEquals(workItem5, project.getFeature(5));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void getFeatureUnknownIdShouldThrowException() {
        project.getFeature(3);
    }

    @Test(expected = NoSuchElementException.class)
    public void getFinishedFeatureShouldThrowException() {
        project.getFeature(0);
    }

    @Test
    public void getBudgetEntries() {
        List<EstimatesBudgetEntry> budgetEntries = project.getBudgetEntries();
        assertEquals(2, budgetEntries.size());
        assertEquals(workItem5, budgetEntries.get(0).getFeature());
        assertEquals(workItem10, budgetEntries.get(1).getFeature());

        assertEquals(120, budgetEntries.get(0).getWorstCaseCumulativeCost());
        assertEquals(60, budgetEntries.get(0).getAverageCaseCumulativeCost());

        assertEquals(222, budgetEntries.get(1).getWorstCaseCumulativeCost());
        assertEquals(130, budgetEntries.get(1).getAverageCaseCumulativeCost());
    }

    @Test
    @Ignore("not working now")
    public void getCostPerPointSoFar() {
        //        project.setCostSoFar(100);
        assertEquals(20, project.getCostPerPointSoFar());
    }

    @Test
    public void setNullKanbanProject() {
        project.setKanbanProject(null);
        assertEquals(0, project.getCostPerPointSoFar());
        assertEquals(0, project.getBudgetEntries().size());
    }
}
