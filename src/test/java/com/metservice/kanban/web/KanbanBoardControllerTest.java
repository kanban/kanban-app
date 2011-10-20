package com.metservice.kanban.web;

import static com.metservice.kanban.utils.DateUtils.parseConventionalNewZealandDate;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.charts.burnup.BurnUpChartGenerator;
import com.metservice.kanban.model.DefaultKanbanProject;
import com.metservice.kanban.model.DefaultWorkItemTree;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.TreeNode;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.model.WorkItemTypeCollection;

public class KanbanBoardControllerTest {

    
    // TODO Rewrite KanbanController legacy test in this form

    @Test
    public void modelContainsKanban() throws IOException {
        KanbanService kanbanService = mock(KanbanService.class);
        KanbanProject kanban = mock(KanbanProject.class);

        when(kanbanService.getKanbanProject("project")).thenReturn(kanban);

        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(kanbanService);
        assertThat(kanbanController.populateProject("project"), is(kanban));
    }

    @Test
    public void modelContainsRedirectViewThatReturnsToTheBoard() {
        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);

        RedirectView redirectView = kanbanController.populateRedirectView("project", "board");

        assertThat(redirectView.getUrl(), is("/projects/project/board"));
        // Note: should also test that the RedirectView is context-relative, but this property is not exposed
    }

    @Test
    public void canDeleteWorkItems() throws IOException {
        KanbanProject project = mock(KanbanProject.class);
        View expectedView = mock(View.class);

        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);
        View actualView = kanbanController.deleteWorkItem(project, 3, expectedView);

        verify(project).deleteWorkItem(3);
        verify(project).save();
        assertThat(actualView, is(expectedView));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void putsWorkItemAndItsChildrenIntoModel() throws IOException {
        WorkItemType type = new WorkItemType("feature");
        WorkItemTree tree = new DefaultWorkItemTree();
        WorkItem feature = new WorkItem(1, type);
        WorkItem story1 = new WorkItem(2, 1, type);
        WorkItem story2 = new WorkItem(3, 1, type);

        tree.addWorkItems(feature, story1, story2);
        
        // Phases by board, persistence and service aren't used
        KanbanProject project = new DefaultKanbanProject(null, null, tree, null, null);
        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);

        ModelAndView modelAndView = kanbanController.editItem(project, "project name", "backlog",
            feature.getId());

        assertThat((WorkItem) modelAndView.getModelMap().get("workItem"), is(feature));
        assertThat((List<WorkItem>) modelAndView.getModelMap().get("children"), hasItems(story1, story2));
    }

    @Test
    public void canSaveEditedWorkItems() throws IOException, ParseException {
        WorkItemType type = new WorkItemType("backlog", "completed");
        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        WorkItem feature = new WorkItem(1, type);
        tree.addWorkItem(feature);

        KanbanProject project = mock(KanbanProject.class);
        when(project.getWorkItemTree()).thenReturn(tree);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("date-backlog", "10/02/2011");
        request.addParameter("date-completed", "11/02/2011");
        
        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);
        kanbanController.editItemAction(project, "wall", feature.getId(), feature.getParentId(), "new feature name",
            "5", "8", "some notes", "FFFFFF", "on", "a, b,  c ", request);

        assertThat(feature.getName(), is("new feature name"));
        assertThat(feature.getSize(), is(5));
        assertThat(feature.getImportance(), is(8));
        assertThat(feature.getNotes(), is("some notes"));
        assertThat(feature.isExcluded(), is(true));
        assertThat(feature.getWorkStreamsAsString(), is("a,b,c"));
        assertThat(feature.getWorkStreams().size(), is(3));

        assertThat(feature.getDate("backlog"), is(parseConventionalNewZealandDate("10/02/2011")));
        assertThat(feature.getDate("completed"), is(parseConventionalNewZealandDate("11/02/2011")));
    }
    
    @Test
    public void canSaveEditedWorkItemsWithNoImportanceNoSizeNoWorkStreams() throws IOException, ParseException {
        WorkItemType type = new WorkItemType("backlog");
        DefaultWorkItemTree tree = new DefaultWorkItemTree();
        WorkItem feature = new WorkItem(1, type);
        tree.addWorkItem(feature);

        KanbanProject project = mock(KanbanProject.class);
        when(project.getWorkItemTree()).thenReturn(tree);
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);
        kanbanController.editItemAction(project, "wall", feature.getId(), feature.getParentId(), "new feature name",
            "", "", "some notes", "FFFFFF", "on", "", request);
        
        assertThat(feature.getSize(), is(0));
        assertThat(feature.getImportance(), is(0));
        assertThat(feature.getWorkStreams().size(), is(0));
    }

    @Test
    public void canReparentWorkItems() throws IOException, ParseException {
        WorkItemType featureType = new WorkItemType("feature-phase");
        WorkItemType storyType = new WorkItemType("story-phase");

        WorkItem feature1 = new WorkItem(1, featureType);
        WorkItem story = new WorkItem(2, 1, storyType);
        WorkItem feature2 = new WorkItem(3, featureType);

        WorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, story, feature2);

        assertThat(story.getParentId(), is(feature1.getId()));

        KanbanProject project = mock(KanbanProject.class);
        when(project.getWorkItemTree()).thenReturn(tree);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("date-story-phase", "10/02/2011");
        
        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);
        kanbanController.editItemAction(project, "wall", story.getId(), feature2.getId(), "new name", "4", "1",
            "new notes", "FFFFFF", "false", "", request);

        WorkItem reparentedStory = tree.getWorkItem(story.getId());

        assertThat(reparentedStory.getParentId(), is(feature2.getId()));
    }

    @Test
    public void ifTheParentHasNotChangedSiblingsAreNotReordered() throws IOException, ParseException {
        WorkItemType type = new WorkItemType("phase");
        WorkItem middleFeature = new WorkItem(2, type);
        WorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItem(new WorkItem(1, type));
        tree.addWorkItem(middleFeature);
        tree.addWorkItem(new WorkItem(2, type));
        
        KanbanProject project = mock(KanbanProject.class);
        when(project.getWorkItemTree()).thenReturn(tree);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("date-story-phase", "10/02/2011");
        
        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);
        kanbanController.editItemAction(project, "wall", middleFeature.getId(), middleFeature.getParentId(),
            "new name", "3", "11", "new notes", "FFFFFF", null, "", request);

        List<WorkItem> workItems = tree.getChildren(middleFeature.getParentId());

        assertThat(workItems.get(1), is(middleFeature));
    }

    @Test
    public void presentsChartPage() {
        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);
        KanbanProject project = mock(KanbanProject.class);
        ModelAndView modelAndView = kanbanController.chart(project, "cool-chart", "feature", "projectName", "", "");

        assertThat(modelAndView.getViewName(), is("/chart.jsp"));
        assertThat((String) modelAndView.getModelMap().get("workItemTypeName"), is("feature"));
        assertThat((String) modelAndView.getModelMap().get("imageName"), is("cool-chart.png"));
        assertThat((String) modelAndView.getModelMap().get("projectName"), is("projectName"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void generatesBurnUpChart() throws IOException {
        WorkItemType featureType = new WorkItemType("feature-phase");
        WorkItemType storyType = new WorkItemType("story-phase");
        
        WorkItem feature1 = new WorkItem(1, featureType);
        WorkItem story = new WorkItem(2, 1, storyType);
        WorkItem feature2 = new WorkItem(3, featureType);
        
        WorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItems(feature1, story, feature2);
        
        WorkItemTypeCollection workItems = new WorkItemTypeCollection(TreeNode.create(WorkItemType.class,
            featureType));
        DefaultKanbanProject project = new DefaultKanbanProject(workItems, null, tree, null, null);
        BurnUpChartGenerator chartGenerator = mock(BurnUpChartGenerator.class);
        OutputStream outputStream = mock(OutputStream.class);

        KanbanBoardController kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(null);
        
        kanbanController.burnUpChartPng(project, chartGenerator, new Date().toString(), new Date().toString(), outputStream);
        ArgumentCaptor<List> workItemsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<OutputStream> outputStreamCaptor = ArgumentCaptor.forClass(OutputStream.class);
        verify(chartGenerator).generateBurnUpChart(
            eq(featureType), workItemsCaptor.capture(), (LocalDate) eq(null), (LocalDate) eq(null), outputStreamCaptor.capture());
        assertThat((Iterable<WorkItem>) workItemsCaptor.getValue(), hasItems(feature1, feature2));
        assertThat((Iterable<WorkItem>) workItemsCaptor.getValue(), not(hasItem(story)));
        assertThat(outputStreamCaptor.getValue(), is(outputStream));
    }
}
