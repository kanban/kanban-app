package com.metservice.kanban.webdriver;

import static com.metservice.kanban.tests.util.TestUtils.createTestProject;
import static com.metservice.kanban.webdriver.BoardPage.openProject;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import java.io.File;
import java.io.IOException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@Ignore
public class EndToEndTest {

    private static Server server;

    private static TemporaryFolder kanbanHome = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws Exception {
        kanbanHome.create();
        System.setProperty("KANBAN_HOME", kanbanHome.getRoot().getAbsolutePath());

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
        createTestProject(root, "Test project", "/end-to-end-test/");
    }

    @Test
    public void userCanDeleteAWorkItem() {
        BoardPage page = openProject("Test project");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickEditFeatureButton("feature name").clickDeleteButtonAndConfirm();

        page.assertFeatureNotPresent("feature name");
    }
}
