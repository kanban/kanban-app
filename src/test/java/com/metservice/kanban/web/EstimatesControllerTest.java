package com.metservice.kanban.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.Map;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.EstimatesDao;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class EstimatesControllerTest {

    private EstimatesController controller;
    private EstimatesDao estimatesDao;
    private KanbanService kanbanService;
    private EstimatesProject project;
    private KanbanProject kanbanProject;

    @Before
    public void setUp() {
        estimatesDao = mock(EstimatesDao.class);
        kanbanService = mock(KanbanService.class);
        project = mock(EstimatesProject.class);
        kanbanProject = mock(KanbanProject.class);
        when(project.getKanbanProject()).thenReturn(kanbanProject);

        controller = new EstimatesController();
        controller.estimatesDao = estimatesDao;
        controller.kanbanService = kanbanService;
    }

    @Test
    public void testSetBugdet() throws IOException {

        RedirectView result = controller.setBudget("budget", 100, project);
        verify(project).setBudget(100);
        assertEquals("estimates", result.getUrl());

        controller.setBudget("estimatedCostPerPoint", 10, project);
        verify(project).setEstimatedCostPerPoint(10);
        assertEquals("estimates", result.getUrl());

        verify(estimatesDao, times(2)).storeProjectEstimates(project);

        verifyNoMoreInteractions(project, kanbanService, estimatesDao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBugdetBanNameShouldThrowException() throws IOException {
        controller.setBudget("budget_wrong", 100, project);
    }

    @Test
    public void testSaveFeature() throws IOException {
        WorkItem item = new WorkItem(1, new WorkItemType("aaa"));
        when(kanbanProject.getWorkItemById(1)).thenReturn(item);

        RedirectView result = controller.saveFeature(1, 10, 20, project);
        assertEquals("estimates", result.getUrl());
        assertEquals(10, item.getAverageCaseEstimate());
        assertEquals(20, item.getWorstCaseEstimate());

        verify(estimatesDao).storeUpdatedFeatures(project);

        verifyNoMoreInteractions(estimatesDao);
    }

    @Test
    public void testIncludeFeature() throws IOException {
        WorkItem item = new WorkItem(1, new WorkItemType("aaa"));

        when(kanbanProject.getWorkItemById(1)).thenReturn(item);

        RedirectView result = controller.excludeFeature(1, true, project);
        assertEquals("estimates", result.getUrl());
        assertTrue(item.isMustHave());
        verify(estimatesDao).storeUpdatedFeatures(project);

    }

    @Test
    public void testExcludeFeature() throws IOException {
        WorkItem item = new WorkItem(1, new WorkItemType("aaa"));

        when(kanbanProject.getWorkItemById(1)).thenReturn(item);

        RedirectView result = controller.excludeFeature(1, false, project);
        assertEquals("estimates", result.getUrl());
        assertFalse(item.isMustHave());
        verify(estimatesDao).storeUpdatedFeatures(project);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveDailyCosts() throws IOException {
        EstimatesController.Data [] data = new EstimatesController.Data [2];
        data[0] = new EstimatesController.Data("2012-01-20", "10");
        data[1] = new EstimatesController.Data("2012-01-22", "5");
        
        JsonStatus result = controller.saveDailyCosts(project, data);
        assertEquals("ok", result.status);
        ArgumentCaptor<Map> costs = ArgumentCaptor.forClass(Map.class);
        verify(project).setDayCosts(costs.capture());
        assertEquals(2, costs.getValue().size());
        assertEquals(10, costs.getValue().get(LocalDate.parse("2012-01-20")));
        assertEquals(5, costs.getValue().get(LocalDate.parse("2012-01-22")));

        verify(estimatesDao).storeProjectEstimates(project);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveDailyCostsWrongItemsShouldBeSkip() throws IOException {
        EstimatesController.Data[] data = new EstimatesController.Data[3];
        data[0] = new EstimatesController.Data("2012-01-20", "10");
        data[1] = new EstimatesController.Data("2012-01-22", "a5");
        data[2] = new EstimatesController.Data("2012-01-", "5");

        JsonStatus result = controller.saveDailyCosts(project, data);
        assertEquals("ok", result.status);
        ArgumentCaptor<Map> costs = ArgumentCaptor.forClass(Map.class);
        verify(project).setDayCosts(costs.capture());
        assertEquals(1, costs.getValue().size());
        assertEquals(10, costs.getValue().get(LocalDate.parse("2012-01-20")));

        verify(estimatesDao).storeProjectEstimates(project);
    }
}
