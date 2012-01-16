package com.metservice.kanban.web;

import static com.metservice.kanban.utils.DateUtils.parseConventionalNewZealandDate;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.KanbanPropertiesFile;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.charts.burnup.BurnUpChartGenerator;
import com.metservice.kanban.model.BoardIdentifier;
import com.metservice.kanban.model.DefaultKanbanProject;
import com.metservice.kanban.model.DefaultWorkItemTree;
import com.metservice.kanban.model.KanbanBacklog;
import com.metservice.kanban.model.KanbanBoard;
import com.metservice.kanban.model.KanbanJournalItem;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.KanbanProjectConfiguration;
import com.metservice.kanban.model.TreeNode;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemComment;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.model.WorkItemTypeCollection;

public class KanbanBoardControllerTest {

    Map<String, String> workStreams;
    KanbanBoardController kanbanController;
    KanbanProject project;
    KanbanService kanbanService;

    @Before
    public void setUp() {
        workStreams = new HashMap<String, String>();
        kanbanController = new KanbanBoardController();
        project = mock(KanbanProject.class);
        kanbanService = mock(KanbanService.class);
    }

    // TODO Rewrite KanbanController legacy test in this form

    @Test
    public void modelContainsKanban() throws IOException {

        when(kanbanService.getKanbanProject("project")).thenReturn(project);


        kanbanController.setKanbanService(kanbanService);
        assertThat(kanbanController.populateProject("project"), is(project));
    }

    //    @Test
    //    public void modelContainsRedirectViewThatReturnsToTheBoard() {
    //        KanbanBoardController kanbanController = new KanbanBoardController();
    //        kanbanController.setKanbanService(null);
    //
    //        RedirectView redirectView = kanbanController.populateRedirectView("project", "board");
    //
    //        assertThat(redirectView.getUrl(), is("/projects/project/board"));
    //        // Note: should also test that the RedirectView is context-relative, but this property is not exposed
    //    }

    @Test
    public void canDeleteWorkItems() throws IOException {


        kanbanController.setKanbanService(null);
        RedirectView actualView = kanbanController.deleteWorkItem(project, 3, "wall");

        verify(project).deleteWorkItem(3);
        verify(project).save();
        assertThat(actualView.getUrl(), is("../wall"));
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

        when(project.getWorkItemTree()).thenReturn(tree);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("date-backlog", "10/02/2011");
        request.addParameter("date-completed", "11/02/2011");

        kanbanController.setKanbanService(null);
        kanbanController.editItemAction(project, "wall", feature.getId(), feature.getParentId(), "new feature name",
            "5", "7", "8", "some notes", "FFFFFF", "on", "a, b,  c ", null, request);

        assertThat(feature.getName(), is("new feature name"));
        assertThat(feature.getAverageCaseEstimate(), is(5));
        assertThat(feature.getWorstCaseEstimate(), is(7));
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

        when(project.getWorkItemTree()).thenReturn(tree);
        MockHttpServletRequest request = new MockHttpServletRequest();

        kanbanController.setKanbanService(null);
        kanbanController.editItemAction(project, "wall", feature.getId(), feature.getParentId(), "new feature name",
            "", "", "", "some notes", "FFFFFF", "on", "", null, request);

        assertThat(feature.getAverageCaseEstimate(), is(0));
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

        when(project.getWorkItemTree()).thenReturn(tree);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("date-story-phase", "10/02/2011");

        kanbanController.setKanbanService(null);
        kanbanController.editItemAction(project, "wall", story.getId(), feature2.getId(), "new name", "4", "6", "1",
            "new notes", "FFFFFF", "false", "", null, request);

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

        when(project.getWorkItemTree()).thenReturn(tree);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("date-story-phase", "10/02/2011");

        kanbanController.setKanbanService(null);
        kanbanController.editItemAction(project, "wall", middleFeature.getId(), middleFeature.getParentId(),
            "new name", "3", "8", "11", "new notes", "FFFFFF", null, "", null, request);

        List<WorkItem> workItems = tree.getChildren(middleFeature.getParentId());

        assertThat(workItems.get(1), is(middleFeature));
    }

    @Test
    public void presentsChartPage() {
        kanbanController.setKanbanService(null);
        ModelAndView modelAndView = kanbanController.chart(project, "cool-chart", "feature", "projectName", null, "",
            "");

        assertThat(modelAndView.getViewName(), is("/chart.jsp"));
        assertThat((String) modelAndView.getModelMap().get("workItemTypeName"), is("feature"));
        assertThat((String) modelAndView.getModelMap().get("imageName"), is("cool-chart.png"));
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

        kanbanController.setKanbanService(null);

        kanbanController.burnUpChartPng(project, chartGenerator, new Date().toString(), new Date().toString(), null,
            outputStream);
        ArgumentCaptor<List> workItemsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<OutputStream> outputStreamCaptor = ArgumentCaptor.forClass(OutputStream.class);
        verify(chartGenerator).generateBurnUpChart(
            eq(featureType), workItemsCaptor.capture(), (LocalDate) eq(null), (LocalDate) eq(null), outputStreamCaptor.capture());
        assertThat((Iterable<WorkItem>) workItemsCaptor.getValue(), hasItems(feature1, feature2));
        assertThat((Iterable<WorkItem>) workItemsCaptor.getValue(), not(hasItem(story)));
        assertThat(outputStreamCaptor.getValue(), is(outputStream));
    }

    @Test
    public void addComment() throws IOException {
        WorkItemType featureType = new WorkItemType("feature-phase");
        WorkItem feature = new WorkItem(99, featureType);

        when(project.getWorkItemById(99))
                .thenReturn(feature);

        ResponseEntity<String> responseEntity = kanbanController.addComment(project, 99, "user name",
            "this is a cool new feature!");

        assertThat(responseEntity, is(notNullValue()));
        verify(project).save();
    }

    @Test
    public void verifyProjectNames() {
        assertNull(KanbanBoardController.isProjectNameValid("New Project Name"));

        assertEquals("Project name contains incorrect characters at least one of (/\\|<>*?&:\")",
            KanbanBoardController.isProjectNameValid("New Project Name with bad character/"));

        assertEquals("Project name contains incorrect characters at least one of (/\\|<>*?&:\")",
            KanbanBoardController.isProjectNameValid("\\"));
        assertEquals("Project name contains incorrect characters at least one of (/\\|<>*?&:\")",
            KanbanBoardController.isProjectNameValid("|"));
        assertEquals("Project name contains incorrect characters at least one of (/\\|<>*?&:\")",
            KanbanBoardController.isProjectNameValid("<"));
        assertEquals("Project name contains incorrect characters at least one of (/\\|<>*?&:\")",
            KanbanBoardController.isProjectNameValid(">"));
        assertEquals("Project name contains incorrect characters at least one of (/\\|<>*?&:\")",
            KanbanBoardController.isProjectNameValid("*"));
        assertEquals("Project name contains incorrect characters at least one of (/\\|<>*?&:\")",
            KanbanBoardController.isProjectNameValid("\""));

        assertEquals("Project name is too long, maximum allowed length is 32 charactes, but is 76",
            KanbanBoardController
                .isProjectNameValid("New Project Name which is very long and we dont like such long project names"));

        assertEquals("Project name should not be empty",
            KanbanBoardController
                .isProjectNameValid("  "));
    }

    @Test
    public void testWallBoard() throws IOException {

        KanbanBoard board = mock(KanbanBoard.class);

        when(project.getBoard(eq(BoardIdentifier.WALL), anyString())).thenReturn(board);

        ModelAndView wallBoardResult = kanbanController.wallBoard(project, "project", null, null, workStreams, null);

        assertEquals("/project.jsp", wallBoardResult.getViewName());

        assertNull(wallBoardResult.getModel().get("highlight"));
        assertEquals(board, wallBoardResult.getModel().get("board"));
        assertEquals("wall", wallBoardResult.getModel().get("boardType"));
        assertEquals("project", wallBoardResult.getModel().get("projectName"));
    }

    @Test
    public void testBacklogBoard() throws IOException {
        KanbanBacklog backlog = mock(KanbanBacklog.class);
        WorkItemType type = new WorkItemType("phase");
        WorkItemTypeCollection workItems = new WorkItemTypeCollection(TreeNode.create(WorkItemType.class, type));

        when(project.getWorkItemTypes()).thenReturn(workItems);
        when(project.getBacklog(anyString())).thenReturn(backlog);

        ModelAndView backlogBoardResult = kanbanController.backlogBoard(project, "project", null, null, workStreams);

        assertEquals("/backlog.jsp", backlogBoardResult.getViewName());
        assertEquals(backlog, backlogBoardResult.getModel().get("kanbanBacklog"));
        assertNotNull(backlogBoardResult.getModel().get("type"));
        assertEquals("phase", backlogBoardResult.getModel().get("phase"));
        assertEquals("backlog", backlogBoardResult.getModel().get("boardType"));
        assertEquals("project", backlogBoardResult.getModel().get("projectName"));
    }

    @Test
    public void testJournalBoard() throws IOException {
        List<KanbanJournalItem> journal = new ArrayList<KanbanJournalItem>();
        journal.add(new KanbanJournalItem(1, "2012-01-10", "test", "user"));
        journal.add(new KanbanJournalItem(2, "2012-01-11", "test 2", "user 2"));
        when(project.getJournal()).thenReturn(journal);
        ModelAndView journalBoardResult = kanbanController.journalBoard(project, "project", null, null, workStreams);

        assertEquals("/journal.jsp", journalBoardResult.getViewName());
        assertEquals(journal, journalBoardResult.getModel().get("kanbanJournal"));
        assertEquals("journal", journalBoardResult.getModel().get("boardType"));
        assertEquals("project", journalBoardResult.getModel().get("projectName"));
    }

    @Test
    public void testCompletedBoard() throws IOException {
        KanbanBoard completed = mock(KanbanBoard.class);
        WorkItemType type = new WorkItemType("phase");
        WorkItemTypeCollection workItems = new WorkItemTypeCollection(TreeNode.create(WorkItemType.class, type));

        when(project.getWorkItemTypes()).thenReturn(workItems);
        when(project.getCompleted(anyString())).thenReturn(completed);

        ModelAndView completedBoardResult = kanbanController
            .completedBoard(project, "project", null, null, workStreams);

        assertEquals("/completed.jsp", completedBoardResult.getViewName());
        assertEquals(completed, completedBoardResult.getModel().get("board"));
        assertNotNull(completedBoardResult.getModel().get("type"));
        assertEquals("phase", completedBoardResult.getModel().get("phase"));
        assertEquals("completed", completedBoardResult.getModel().get("boardType"));
        assertEquals("project", completedBoardResult.getModel().get("projectName"));
    }

    @Test
    public void testCreateBlockedComment() {
        WorkItemComment blockedComment = KanbanBoardController.createBlockedComment(true, "comment", "user");

        assertEquals("Blocked: comment", blockedComment.getCommentText());

        WorkItemComment unblockedComment = KanbanBoardController.createBlockedComment(false, "comment", "user");
        assertEquals("Unblocked: comment", unblockedComment.getCommentText());
    }

    @Test
    public void setWorkStreamTestForRegularBoard() {

        RedirectView setWorkStreamResult = kanbanController.setWorkStream(project, "project", "wall", "ws1", null,
            null, workStreams);

        assertEquals("/projects/project/wall", setWorkStreamResult.getUrl());
        assertEquals("ws1", workStreams.get("project"));
    }

    @Test
    public void setWorkStreamTestForCharts() {
        RedirectView setWorkStreamResult = kanbanController.setWorkStream(project, "project", "chart", "ws1",
            "cycle-chart",
            "Story", workStreams);

        assertEquals("/projects/project/chart?chartName=cycle-chart&workItemTypeName=Story",
            setWorkStreamResult.getUrl());
        assertEquals("ws1", workStreams.get("project"));
    }

    @Test
    public void editProjectActionForRenamingProjectValidProjectName() throws IOException {
        kanbanController.setKanbanService(kanbanService);
        when(kanbanService.getKanbanProject("project")).thenReturn(project);

        RedirectView editProjectActionResult = kanbanController.editProjectAction(project, "project", "new project",
            "content");

        verify(kanbanService).renameProject("project", "new project");
        verify(kanbanService).editProject("new project", "content");
        verifyNoMoreInteractions(kanbanService);

        assertEquals("/projects/new project/wall", editProjectActionResult.getUrl());
    }

    @Test
    public void editProjectActionForRenamingProjectInvalidProjectNameShouldRedirectError() throws IOException {
        kanbanController.setKanbanService(kanbanService);
        when(kanbanService.getKanbanProject("project")).thenReturn(project);

        RedirectView editProjectActionResult = kanbanController.editProjectAction(project, "project",
            "invalid/project",
            "content");

        verifyNoMoreInteractions(kanbanService);

        assertEquals(
            "edit-project?createNewProject=false&error=Project+name+contains+incorrect+characters+at+least+one+of+%28%2F%5C%7C%3C%3E*%3F%26%3A%22%29",
            editProjectActionResult.getUrl());
    }

    @Test
    public void editProjectNameNotChanged() throws IOException {
        kanbanController.setKanbanService(kanbanService);
        when(kanbanService.getKanbanProject("project")).thenReturn(project);

        RedirectView editProjectActionResult = kanbanController.editProjectAction(project, "project", "project",
            "content");

        verify(kanbanService).editProject("project", "content");
        verifyNoMoreInteractions(kanbanService);

        assertEquals("/projects/project/wall", editProjectActionResult.getUrl());
    }

    @Test
    public void redirectToWall() {
        RedirectView result = kanbanController.redirectToWall("a project");
        assertEquals("/projects/a project/wall", result.getUrl());
    }

    @Test
    public void addJournalEntry() {
        KanbanJournalItem result = kanbanController.addJournalEntry(project, "project name", "user", "2012-01-16",
            "a text");
        assertEquals("user", result.getUserName());
        assertEquals(LocalDateTime.parse("2012-01-16"), result.getDate());
        assertEquals("a text", result.getText());
        verify(project, times(1)).addJournalItem(result);
    }

    @Test
    public void removeJournalEntry() {
        kanbanController.removeJournalEntry(project, 10);
        verify(project, times(1)).deleteJournalItem(10);
    }

    @Test
    public void editColumnSetWipValidInteger() throws IOException {
        WorkItemType type = new WorkItemType("Backlog", "Dev", "Done");
        type.setName("feature");
        WorkItemTypeCollection itemTypes = mock(WorkItemTypeCollection.class);
        KanbanProjectConfiguration config = mock(KanbanProjectConfiguration.class);
        KanbanPropertiesFile propertiesFile = mock(KanbanPropertiesFile.class);

        when(project.getWorkItemTypes()).thenReturn(itemTypes);
        when(itemTypes.getByName("feature")).thenReturn(type);

        when(kanbanService.getProjectConfiguration("a project")).thenReturn(config);
        when(config.getKanbanPropertiesFile()).thenReturn(propertiesFile);
        kanbanController.setKanbanService(kanbanService);

        RedirectView result = kanbanController.editColumn(project, "a project", "feature", "Dev", 10);
        assertEquals("wall", result.getUrl());

        verify(propertiesFile).setColumnWipLimit(type, "Dev", 10);
    }

    @Test
    public void editColumnSetWipNull() throws IOException {
        WorkItemType type = new WorkItemType("Backlog", "Dev", "Done");
        type.setName("feature");
        WorkItemTypeCollection itemTypes = mock(WorkItemTypeCollection.class);
        KanbanProjectConfiguration config = mock(KanbanProjectConfiguration.class);
        KanbanPropertiesFile propertiesFile = mock(KanbanPropertiesFile.class);

        when(project.getWorkItemTypes()).thenReturn(itemTypes);
        when(itemTypes.getByName("feature")).thenReturn(type);

        when(kanbanService.getProjectConfiguration("a project")).thenReturn(config);
        when(config.getKanbanPropertiesFile()).thenReturn(propertiesFile);
        kanbanController.setKanbanService(kanbanService);

        RedirectView result = kanbanController.editColumn(project, "a project", "feature", "Dev", null);
        assertEquals("wall", result.getUrl());

        verify(propertiesFile).setColumnWipLimit(type, "Dev", null);
    }
}
