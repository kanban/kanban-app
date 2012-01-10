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
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.EstimatesDao;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class EstimatesControllerTest {

    private EstimatesController controller;
    private EstimatesDao petDao;
    private KanbanService kanbanService;
    private EstimatesProject project;
    private KanbanProject kanbanProject;

    @Before
    public void setUp() {
        petDao = mock(EstimatesDao.class);
        kanbanService = mock(KanbanService.class);
        project = mock(EstimatesProject.class);
        kanbanProject = mock(KanbanProject.class);
        when(project.getKanbanProject()).thenReturn(kanbanProject);

        controller = new EstimatesController();
        controller.petDao = petDao;
        controller.kanbanService = kanbanService;
    }

    @Test
    public void testSetBugdet() throws IOException {

        RedirectView result = controller.setBudget("budget", 100, project);
        verify(project).setBudget(100);
        assertEquals("pet-project", result.getUrl());

        controller.setBudget("costSoFar", 50, project);
        verify(project).setCostSoFar(50);
        assertEquals("pet-project", result.getUrl());

        controller.setBudget("estimatedCostPerPoint", 10, project);
        verify(project).setEstimatedCostPerPoint(10);
        assertEquals("pet-project", result.getUrl());

        verify(petDao, times(3)).storeProjectEstimates(project);

        verifyNoMoreInteractions(project, kanbanService, petDao);
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
        assertEquals("pet-project", result.getUrl());
        assertEquals(10, item.getAverageCaseEstimate());
        assertEquals(20, item.getWorstCaseEstimate());

        verify(petDao).storeUpdatedFeatures(project);

        verifyNoMoreInteractions(petDao);
    }

    @Test
    public void testIncludeFeature() throws IOException {
        WorkItem item = new WorkItem(1, new WorkItemType("aaa"));

        when(kanbanProject.getWorkItemById(1)).thenReturn(item);

        RedirectView result = controller.excludeFeature(1, true, project);
        assertEquals("pet-project", result.getUrl());
        assertTrue(item.isMustHave());
        verify(petDao).storeUpdatedFeatures(project);

    }

    @Test
    public void testExcludeFeature() throws IOException {
        WorkItem item = new WorkItem(1, new WorkItemType("aaa"));

        when(kanbanProject.getWorkItemById(1)).thenReturn(item);

        RedirectView result = controller.excludeFeature(1, false, project);
        assertEquals("pet-project", result.getUrl());
        assertFalse(item.isMustHave());
        verify(petDao).storeUpdatedFeatures(project);

    }
}
