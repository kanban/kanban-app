package com.metservice.kanban.jwebunit;

import static com.metservice.kanban.KanbanService.KANBAN_HOME_PROPERTY_NAME;
import static com.metservice.kanban.jwebunit.BoardPage.openProject;
import static com.metservice.kanban.tests.util.TestUtils.createTestProject;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import java.io.File;
import java.io.IOException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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

    @Before
    public void cleanProject() throws IOException {
        File root = kanbanHome.getRoot();

        deleteDirectory(root);
        root.mkdir();
        createTestProject(root, "Test project");
    }

    @Test
    public void userCanDeleteAWorkItem() {
        BoardPage page = openProject("Test project");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickEditFeatureButton("feature name").clickDeleteButtonAndConfirm();

        page.assertFeatureNotPresent("feature name");
    }

    @Test
    public void userCanRenameAWorkItem() {
        BoardPage page = openProject("Test project");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickEditFeatureButton("feature name").enterName("new feature name").clickSaveButton();

        page.assertFeatureNotPresent("feature name");
        page.assertFeatureIsPresent("new feature name");
    }

    @Test
    public void userCanExcludeAWorkItemFromReports() {
        BoardPage page = openProject("Test project");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickEditFeatureButton("feature name").assertExcludeBoxIs(false);
        page.clickBacklogButton();
        page.clickEditFeatureButton("feature name").tickExcludeBox().clickSaveButton();
        page.clickEditFeatureButton("feature name").assertExcludeBoxIs(true);
    }

    @Test
    public void userCanChangeTheParentOfAWorkItem() {
        BoardPage page = openProject("Test project");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature 1").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature 2").clickSaveButton();
        page.clickAdvance("feature 1");
        page.clickAdvance("feature 2");
        WallPage wall = page.clickWallButton();
        
        wall.clickAddStoryButton("feature 1").enterName("story").clickSaveButton();
        
        wall.clickEditStoryButton("story").setParent("feature 2").clickSaveButton();
        wall.clickEditStoryButton("story").assertParentIs("feature 2");
    }

    @Test
    public void userCanViewAChart() {
        BoardPage wallPage = openProject("Test project");
        ChartPage chartPage = wallPage.clickFeatureCycleTimeChartButton();
        chartPage.assertImageIsValidPng("cycle-time-chart.png?level=feature");

    }

    @Test
    public void userCanViewABurnUpChart() {
        BoardPage wallPage = openProject("Test project");
        ChartPage chartPage = wallPage.clickBurnUpChartButton();
        chartPage.assertImageIsValidPng("burn-up-chart.png?level=feature");
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
