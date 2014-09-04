package com.metservice.kanban;

import static com.metservice.kanban.KanbanService.KANBAN_HOME_PROPERTY_NAME;
import static com.metservice.kanban.tests.util.TestUtils.createTestProject;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.io.IOException;

import com.metservice.kanban.utils.MessageUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class KanbanServiceTest {

    @Rule
    public TemporaryFolder kanbanHome = new TemporaryFolder();

    @Before
    public void before() {
        System.setProperty(KANBAN_HOME_PROPERTY_NAME, kanbanHome.getRoot().getAbsolutePath());
    }

    @Test
    public void getsKanbanHomeFromSystemProperty() {
        KanbanService service = new KanbanService();
        assertThat(service.getHome(), is(kanbanHome.getRoot()));
    }

    @Test
    public void getUserDirectoryWhenKanbanHomePropertyNotSet() {
        System.clearProperty(KANBAN_HOME_PROPERTY_NAME);
        KanbanService service = new KanbanService();
        assertThat(service.getHome(), is(new File(System.getProperty("user.home"), ".kanban")));
    }

    @Test
    public void subdirectoriesOfKanbanHomeAreProjects() {
        kanbanHome.newFolder("project1");
        kanbanHome.newFolder("project2");

        KanbanService service = new KanbanService();
        assertThat(service.getProjects(), hasItems("project1", "project2"));
    }

    @Test
    public void dotDirectoriesAreNotProjects() {
        kanbanHome.newFolder(".svn");
        kanbanHome.newFolder(".somethingElse");

        KanbanService service = new KanbanService();
        assertThat(service.getProjects(), not(hasItem(".svn")));
        assertThat(service.getProjects(), not(hasItem(".somethingElse")));
    }

    @Test
    public void canCreateNewProjects() throws IOException {
        File root = kanbanHome.getRoot();
        File projectHome = new File(root, "New project");
        File propertiesFile = new File(projectHome, "kanban.properties");

        KanbanService kanbanService = new KanbanService(root);
        kanbanService.createProject("New project", "property=value");

        assertThat(readFileToString(propertiesFile), is("property=value"));
        assertThat(projectHome.getParentFile(), is(root));
    }

    @Test(expected = IllegalArgumentException.class)
    public void refusesToCreateAProjectWithAnExistingName() throws IOException {
        createTestProject(kanbanHome.getRoot(), "Test project", "/end-to-end-test/");

        KanbanService kanbanService = new KanbanService(kanbanHome.getRoot());
        kanbanService.createProject("Test project", "some new settings");
    }

    @Test
    public void refusesToCreateAProjectWithoutPermissions() throws IOException {
        if (! kanbanHome.getRoot().setReadOnly()) {
            throw new RuntimeException("Has no permissions to set folder " +
                    MessageUtils.decorateSingleQuotes(kanbanHome.getRoot().getAbsolutePath()) + " as read-only");
        }

        KanbanService kanbanService = new KanbanService(kanbanHome.getRoot());
        try {
            kanbanService.createProject("Test project no permissions", "some new settings");
        } catch (IllegalArgumentException e) {
            final String message = e.getMessage();
            if ( ! message.contains("cannot create project") && ! message.contains(kanbanHome.getRoot().getAbsolutePath()) ) {
                 throw e;
            }
        }
    }

    @Test
    public void getFilteredProjectsSkipUnderscores() {
        kanbanHome.newFolder("project1");
        kanbanHome.newFolder("_project2");
        kanbanHome.newFolder("project3");
        kanbanHome.newFolder("_project4");

        KanbanService service = new KanbanService();

        assertThat(service.getProjects(), hasItems("project1", "_project2", "project3", "_project4"));
        assertThat(service.getFilteredProjects(), hasItems("project1", "project3"));
        assertThat(service.getFilteredProjects(), not(hasItem("_project2")));
        assertThat(service.getFilteredProjects(), not(hasItem("_project4")));

    }
}
