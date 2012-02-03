package com.metservice.kanban.charts.burnup;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.joda.time.LocalDate;
import org.junit.*;
import com.google.gson.internal.Pair;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class EstimatesBurnDownDataModelTest {

    private EstimatesBurnDownDataModel model;
    private EstimatesProject estimatesProject;
    private List<WorkItem> workItems;
    private Map<LocalDate, Integer> dayCosts;

    @Before
    public void setUp() {
        workItems = new ArrayList<WorkItem>();

        WorkItemType type = new WorkItemType("Backlog", "Dev", "Complete");
        WorkItem item1 = new WorkItem(0, type);
        item1.setAverageCaseEstimate(5);
        workItems.add(item1);

        WorkItem item2 = new WorkItem(0, type);
        item2.setAverageCaseEstimate(3);
        workItems.add(item2);

        dayCosts = new TreeMap<LocalDate, Integer>();
        dayCosts.put(LocalDate.parse("2012-01-02"), 7);
        dayCosts.put(LocalDate.parse("2012-01-04"), 8);
        dayCosts.put(LocalDate.parse("2012-01-06"), 8);

        estimatesProject = mock(EstimatesProject.class);
        when(estimatesProject.getDayCosts()).thenReturn(dayCosts);

    }

    @Test
    public void returnsValidBudget() {
        model = new EstimatesBurnDownDataModel(workItems, estimatesProject);
        when(estimatesProject.getBudget()).thenReturn(10);
        assertEquals(10, model.getBudget());
    }

    @Test
    public void computesValidAllFeaturesPoints() {
        model = new EstimatesBurnDownDataModel(workItems, estimatesProject);
        assertEquals(8, model.computeAllFeaturePoints());
    }

    @Test
    public void computesValidBudgetEntries() {
        model = new EstimatesBurnDownDataModel(workItems, estimatesProject);
        List<Pair<Integer, LocalDate>> budgetEntries = model.computeBudgetEntries();

        assertEquals(3, budgetEntries.size());
        assertEquals(new Pair<Integer, LocalDate>(7, LocalDate.parse("2012-01-02")), budgetEntries.get(0));
        assertEquals(new Pair<Integer, LocalDate>(15, LocalDate.parse("2012-01-04")), budgetEntries.get(1));
        assertEquals(new Pair<Integer, LocalDate>(23, LocalDate.parse("2012-01-06")), budgetEntries.get(2));
    }

    @Test
    public void getRemainingFeaturePointForBudgetWhenNoFeaturesCompleted() {
        model = new EstimatesBurnDownDataModel(workItems, estimatesProject);
        assertEquals(8,
            model.getRemainingFeaturePointForBudget(new Pair<Integer, LocalDate>(7, LocalDate.parse("2012-01-02"))));
        assertEquals(8,
            model.getRemainingFeaturePointForBudget(new Pair<Integer, LocalDate>(15, LocalDate.parse("2012-01-04"))));
        assertEquals(8,
            model.getRemainingFeaturePointForBudget(new Pair<Integer, LocalDate>(24, LocalDate.parse("2012-01-06"))));
    }

    @Test
    public void getRemainingFeaturePointForBudgetWhenFeaturesCompleted() {
        workItems.get(0).advance(LocalDate.parse("2012-01-01"));
        workItems.get(0).advance(LocalDate.parse("2012-01-02"));
        workItems.get(0).advance(LocalDate.parse("2012-01-03"));
        workItems.get(1).advance(LocalDate.parse("2012-01-02"));
        workItems.get(1).advance(LocalDate.parse("2012-01-04"));
        workItems.get(1).advance(LocalDate.parse("2012-01-06"));

        model = new EstimatesBurnDownDataModel(workItems, estimatesProject);

        assertEquals(8,
            model.getRemainingFeaturePointForBudget(new Pair<Integer, LocalDate>(7, LocalDate.parse("2012-01-02"))));
        assertEquals(3,
            model.getRemainingFeaturePointForBudget(new Pair<Integer, LocalDate>(15, LocalDate.parse("2012-01-04"))));
        assertEquals(0,
            model.getRemainingFeaturePointForBudget(new Pair<Integer, LocalDate>(24, LocalDate.parse("2012-01-06"))));
    }

    @Test
    public void getLastBudgetEntry() {
        model = new EstimatesBurnDownDataModel(workItems, estimatesProject);
        Pair<Integer, LocalDate> lastBudgedEntry = model.getLastBudgedEntry();
        assertEquals(new Pair<Integer, LocalDate>(23, LocalDate.parse("2012-01-06")), lastBudgedEntry);
    }

    @Test
    public void computeProjectedBudgedConsumed() {
        workItems.get(0).advance(LocalDate.parse("2012-01-01"));
        workItems.get(0).advance(LocalDate.parse("2012-01-02"));
        workItems.get(0).advance(LocalDate.parse("2012-01-03"));

        model = new EstimatesBurnDownDataModel(workItems, estimatesProject);

        int computeProjectedBudgedConsumed = model.computeProjectedBudgedConsumed();

        assertEquals(36, computeProjectedBudgedConsumed);
    }

    @Test
    public void computeProjectedBudgedConsumedWhenNoWorkItemsCompleted() {
        model = new EstimatesBurnDownDataModel(workItems, estimatesProject);
        when(estimatesProject.getBudget()).thenReturn(100);

        int computeProjectedBudgedConsumed = model.computeProjectedBudgedConsumed();

        assertEquals(500, computeProjectedBudgedConsumed);
    }
}
