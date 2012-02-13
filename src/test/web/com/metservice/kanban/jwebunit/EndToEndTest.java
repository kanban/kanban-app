package com.metservice.kanban.jwebunit;

import static com.metservice.kanban.KanbanService.KANBAN_HOME_PROPERTY_NAME;
import static com.metservice.kanban.jwebunit.BoardPage.openProject;
import static org.junit.Assert.assertEquals;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.tests.util.TestUtils;
import com.metservice.kanban.web.KanbanBoardController;

public class EndToEndTest {

    private static Server server;

    private static TemporaryFolder kanbanHome = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws Exception {
        kanbanHome.create();
        System.setProperty(KANBAN_HOME_PROPERTY_NAME, kanbanHome.getRoot().getAbsolutePath());
        server = new Server(8008);
        server.setHandler(new WebAppContext("src/main/webapp", "/kanban"));
        server.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stop();
        kanbanHome.delete();
    }

    @Test
    public void userCanDeleteAWorkItem() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        //        page.clickBacklogButton();
        page.clickEditFeatureButton("feature name").clickDeleteButtonAndConfirm();

        page.assertFeatureNotPresent("feature name");
    }

    @Test
    public void petPageShowsPlannedFeatures() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickPETButton().checkFeatureDescription("feature name");
    }

    @Test
    public void petPageShowsPlannedStoriesForProjectsWithOnlyStories() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project 2", "/Test Project 2/");
        page.clickAddStoryButton().enterName("feature name").clickSaveButton();
        page.clickPETButton().checkFeatureDescription("feature name");
    }

    @Test
    public void projectListAppearsSorted() throws IOException {
        File root = kanbanHome.getRoot();
        BoardPage.cleanProject(kanbanHome);
        TestUtils.createTestProject(root, "123", "/Test Project 2/");
        TestUtils.createTestProject(root, "ABC", "/Test Project 2/");
        TestUtils.createTestProject(root, "XYZ", "/Test Project 2/");
        TestUtils.createTestProject(root, "acb", "/Test Project 2/");
        TestUtils.createTestProject(root, "xzy", "/Test Project 2/");
        TestUtils.createTestProject(root, "A1z", "/Test Project 2/");
        BoardPage.createBoardPage("123").assertProjectListIsSorted();
    }

    @Test
    public void userCanAddSizeToAWorkItem() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").enterAverageCase("5").enterWorstCase("10")
            .clickSaveButton();
        //Check that the size on the feature or story matches the Average Case value in P.E.T
        page.clickPETButton().checkPetAverageCaseValue("5").checkPetWorstCaseValue("10");
    }

    @Test
    public void userCanRenameAWorkItem() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickEditFeatureButton("feature name").enterName("new feature name").clickSaveButton();

        page.assertFeatureNotPresent("feature name");
        page.assertFeatureIsPresent("new feature name");
    }
    
    @Test
    public void userCanAddDatedEntriesInTheJournal() throws Exception {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        JournalPage journalPage = page.clickJournalButton();
        page.setCurrentUser("Project Champion");
        journalPage.clickAddEntry().enterJournalDate("2012-01-12").enterJournalText("test journal text").clickOkButton();
        journalPage.assertJournalTextIsPresent("Project Champion wrote on 2012-01-12", "test journal text");
        
        ChartPage cumulativeChartPage = page.clickCumulativeFlowChartButton();
        cumulativeChartPage.assertJournalTextIsPresent("Project Champion wrote on 2012-01-12", "test journal text");
        
        ChartPage featureCycleTimeChartPage = page.clickFeatureCycleTimeChartButton();
        featureCycleTimeChartPage.assertJournalTextIsPresent("Project Champion wrote on 2012-01-12", "test journal text");
        
        ChartPage burnUpChartPage = page.clickBurnUpChartButton();
        burnUpChartPage.assertJournalTextIsPresent("Project Champion wrote on 2012-01-12", "test journal text");
    }
    
    @Test
    public void userGetsValidationErrorWhenAddingIncompleteEntriesInTheJournal() throws Exception {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        JournalPage journalPage = page.clickJournalButton();
        journalPage.clickAddEntry().clickOkButton();
        journalPage.assertValidationErrorShows("Entry date cannot be empty");
        journalPage.clickAddEntry().enterJournalDate("2012-01-12").clickOkButton();
        journalPage.assertValidationErrorShows("Journal text cannot be empty");
        journalPage.clearEntryDate();
        journalPage.clickAddEntry().enterJournalText("test journal text").clickOkButton();
        journalPage.assertValidationErrorShows("Entry date cannot be empty");
        journalPage.clickCancelButton();
        journalPage.assertJournalTextIsNotPresent("test journal text");
    }

    @Test
    public void userCanExcludeAWorkItemFromReports() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickEditFeatureButton("feature name").assertExcludeBoxIs(false);
        page.clickBacklogButton();
        page.clickEditFeatureButton("feature name").tickExcludeBox().clickSaveButton();
        page.clickEditFeatureButton("feature name").assertExcludeBoxIs(true);
    }

    @Test
    public void userCanChangeTheParentOfAWorkItem() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature 1").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature 2").clickSaveButton();
        page.clickAdvance("feature 1");
        page.clickAdvance("feature 2");
        WallPage wall = page.clickWallButton();
        //        
        //        wall.clickAddStoryButton("feature 1").enterName("story").clickSaveButton();
        //        
        //        wall.clickEditStoryButton("story").setParent("feature 2").clickSaveButton();
        //27/09/11 - feature 2 never gets to the wall. 
        // TODO: Ask Ben!
        //wall.clickEditStoryButton("story").assertParentIs("feature 2");
    }

    @Test
    public void userCanViewAChart() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        ChartPage chartPage = page.clickFeatureCycleTimeChartButton();
        chartPage.assertImageIsValidPng(
            String.format("cycle-time-chart.png?level=feature&startDate=%s&endDate=%s&workStream=",
                getDefaultStartDate(),
                getDefaultEndDate()
                ));
    }

    String getDefaultEndDate() {
        return LocalDate.fromCalendarFields(Calendar.getInstance()).toString("yyyy-MM-dd");
    }

    String getDefaultStartDate() {
        return LocalDate.fromCalendarFields(Calendar.getInstance()).minusMonths(4).toString("yyyy-MM-dd");
    }

    @Test
    public void userCanViewABurnUpChart() throws IOException {
        BoardPage wallPage = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        ChartPage chartPage = wallPage.clickBurnUpChartButton();
        chartPage.assertImageIsValidPng(
            String.format(
                "burn-up-chart.png?level=feature&startDate=%s&endDate=%s&workStream=",
                getDefaultStartDate(),
                getDefaultEndDate()
                ));
    }

    @Test
    public void mustHaveItemShouldBeIndicatedInBacklog() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickAddFeatureButton().enterName("feature name must have").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature name nice to have").clickSaveButton();

        BoardPage backlog = page.clickPETButton().clickMustHave(1).clickBacklogButton();

        backlog.assertItemNameIsIndicatedMustHave(1);
        backlog.assertItemNameIsIndicatedNiceToHave(2);
    }

    @Test
    @Ignore
    public void mustHaveItemShouldBeIndicatedInComplete() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickAddFeatureButton().enterName("feature name must have").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature name nice to have").clickSaveButton();

        BoardPage complete = page.clickPETButton().clickMustHave(1).clickCompleteButton();

        complete.assertItemNameIsIndicatedMustHave(1);
        complete.assertItemNameIsIndicatedNiceToHave(2);
    }

    @Test
    public void userCanGoFromEditPageToPrintPage() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        PrintPage printPage = page.clickAddFeatureButton().enterName("feature name to print")
            .clickSaveAndPrintButton();

        printPage.assertIsPrintPage();
        printPage.assertItemHasName(1, "feature name to print");
    }

    @Test
    @Ignore("this requirement is not longer valid - displaying comments instead of notes")
    public void quotesAreEscapedOnWall() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");

        page
            .clickAddFeatureButton()
            .enterName("feature name to print")
            .enterNotes("This is \"note\" with.")
            .clickSaveButton()
            .clickAdvance("feature name to print");

        WallPage wallPage = page.clickWallButton();

        String notes = wallPage.getNotesForItem(1);

        assertEquals("Notes: This is \"note\" with.", notes);
    }

    @Test
    @Ignore("this requirement is not longer valid - displaying comments instead of notes")
    public void htmlEntitiesAreEscapedOnWall() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");

        WallPage wallPage = page.clickAddFeatureButton().enterName("feature name to print")
            .enterNotes("This is note with <div>HTMLtags</div>.").clickSaveButton()
            .clickAdvance("feature name to print")
            .clickWallButton();

        // if HTML entities are not escaped, this method throws an exception
        String notes = wallPage.getNotesForItem(1);

        assertEquals("Notes: This is note with <div>HTMLtags</div>.", notes);
    }

    @Test
    @Ignore("not possible to test with jWebUnit 3.0 - cannot simulate pressing enter")
    public void userCanCreateAStoryWithHashInQuickEditor() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton().enterQuickName("abc");
    }

    @Test
    public void columnsWithBrokenWIPLimitShouldGoRed() throws IOException {
        // end-to-end project has WIP limit for second phase set to 2
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");

        page.clickAddFeatureButton().enterName("feature 1").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature 2").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature 3").clickSaveButton();

        page.clickBacklogButton().clickAdvance("feature 1").clickAdvance("feature 2");

        WallPage wall = page.clickWallButton();

        wall.assertWipNotBroken("feature wall");

        wall = wall.clickBacklogButton().clickAdvance("feature 3").clickWallButton();

        wall.assertWipBroken("feature wall");

    }
    
    @Test
    public void userCanRenameAProject() throws IOException {
        BoardPage wallPage = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        AdminPage adminPage = wallPage.clickAdminButton();
        adminPage.clickEditProject().enterName("Renamed Test Project").clickSubmitQueryButton();
        
        wallPage.assertProjectNotPresent("Test project");
        wallPage.assertProjectIsPresent("Renamed Test Project");
    }
    
    @Test
    public void cycleTimeChartsVisibleWhenNewPhaseIsAddedToAProject() throws IOException {
        BoardPage wallPage = openProject(kanbanHome, "Test project", "/test-project/");
        AdminPage adminPage = wallPage.clickAdminButton();
        ProjectPropertiesPage projectPropertiesPage = adminPage.clickEditProject();
        String currentProjectProperties = projectPropertiesPage.getProjectProperties();
        currentProjectProperties
            .replace(
                "workItemTypes.feature.phases=Backlog,Design,Implement,Accept,ReadyToDeploy,Deployed,Bugs,Blocks,Done",
                "workItemTypes.feature.phases=Backlog,Design,Implement,Test,Accept,ReadyToDeploy,Deployed,Bugs,Blocks,Done");
        projectPropertiesPage.enterProjectProperties(currentProjectProperties).clickSubmitQueryButton();
        wallPage.clickFeatureCycleTimeChartButton().assertImageIsValidPng(
            String.format(
                "cycle-time-chart.png?level=feature&startDate=%s&endDate=%s&workStream=",
                getDefaultStartDate(),
                getDefaultEndDate()
                ));
    }
    
    @Test
    public void verifyCompBoardPhaseWidth() throws IOException {
        
        BoardPage wallPage = openProject(kanbanHome, "Test project", "/test-project/");
        KanbanService service = new KanbanService();
        String name = "CompTest";
                
        KanbanProject project = service.getKanbanProject("Test project");
        
        int item = project.addWorkItem(WorkItem.ROOT_WORK_ITEM_ID, project.getWorkItemTypes().getByName("feature"), name, 0, 0, 0, "", "555555", false, "", new LocalDate(2010, 05, 1));
        
        WorkItem workItem = project.getWorkItemById(item);
        
        workItem.setDate("Backlog", new LocalDate(2011, 05, 2));
        workItem.setDate("Design", new LocalDate(2011, 05, 5));
        workItem.setDate("Implement", new LocalDate(2011, 05, 8));
        workItem.setDate("Accept", new LocalDate(2011, 05, 10));
        workItem.setDate("Deployed", new LocalDate(2011, 05, 13));
        workItem.setDate("Bugs", new LocalDate(2011, 05, 18));
        workItem.setDate("Blocks", new LocalDate(2011, 05, 22));
        workItem.setDate("Done", new LocalDate(2011, 05, 30));
        
        project.save();
      
        BoardPage completePage = wallPage.clickCompleteButton();
        
        completePage.assertFeatureIsPresent(name);
        completePage.assertCompleteItemWidthIsCorrect(item, 5, 0);

    }  
        
    @Test
    public void defaultStartDateWhenCumulativeFlowChartOpened() throws IOException {
        BoardPage wallPage = openProject(kanbanHome, "Test project", "/test-project/");
        ChartPage chartPage = wallPage.clickCumulativeFlowChartButton();
        
        LocalDate endDateParsed = LocalDate.fromCalendarFields(Calendar.getInstance());
        String startDate = endDateParsed.minusMonths(KanbanBoardController.DEFAULT_MONTHS_DISPLAY).toString(
            "yyyy-MM-dd");
        chartPage.assertStartDateEquals(startDate);
        
    }

    
    //    @Test
    //    public void userCanDownloadStories() throws IOException {
    //        BoardPage wallPage = openProject("Test project");
    //        String responseContent = wallPage.clickDownloadFeaturesButton();
    //        File expectedFile = new File(SystemUtils.getUserDir(), "/src/test/resources/end-to-end-test/feature.csv");
    //        String expectedContent = FileUtils.readFileToString(expectedFile);
    //        assertThat(responseContent, is(expectedContent));
    //    }

    //    @Test
    //    public void columnRedWhenWIPExceeded() throws IOException {
    //    	 BoardPage wallPage = openProject("Test project");
    //    	 //assert that th id"phase_2" background-color = #f00
    //    	//TODO Get help from someone at MetService for writing tests
    //    }

}
