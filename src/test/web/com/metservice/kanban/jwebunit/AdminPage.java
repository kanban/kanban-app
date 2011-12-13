package com.metservice.kanban.jwebunit;

import static com.metservice.kanban.tests.util.TestUtils.createTestProject;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import net.sourceforge.jwebunit.junit.WebTester;
import org.junit.rules.TemporaryFolder;

public class AdminPage {

    protected final WebTester tester;

    public static AdminPage openProject(TemporaryFolder kanbanHome, String projectName, String sourceResourcePath) throws IOException {
    	File root = kanbanHome.getRoot();
    	cleanProject(kanbanHome);
        createTestProject(root, projectName, sourceResourcePath);
    	return createBoardPage(projectName);
    }
    
    public static AdminPage createBoardPage(String projectName){
        WebTester tester = new WebTester();
        tester.beginAt("http://localhost:8008/kanban");
        tester.clickLinkWithExactText(projectName);
        return new AdminPage(tester);
    }

    public static void cleanProject(TemporaryFolder kanbanHome) throws IOException {
        File root = kanbanHome.getRoot();
        deleteDirectory(root);
        root.mkdir();
    }
    
    public AdminPage(WebTester tester) {
        this.tester = tester;
    }

    public ProjectPropertiesPage clickEditProject() {
        tester.clickLinkWithExactText("Edit Project");
        return new ProjectPropertiesPage(tester);
    } 
}
