package com.metservice.kanban.jwebunit;

import net.sourceforge.jwebunit.junit.WebTester;
import static org.junit.Assert.assertTrue;

public class ProjectPropertiesPage {

    private final WebTester tester;

    public ProjectPropertiesPage(WebTester tester) {
        this.tester = tester;
    }

    public ProjectPropertiesPage enterName(String name) {
        tester.setTextField("newProjectName", name);
        return this;
    }
    
    public ProjectPropertiesPage enterProjectProperties(String name) {
        tester.setTextField("content", name);
        return this;
    }
    
    public String getProjectProperties(){
        return tester.getElementById("project-properties").getTextContent();
    }
    
    public BoardPage clickSubmitQueryButton() {
        tester.clickButton("submit-query-button");
        return new BoardPage(tester);
    }
    
    public void dumpPageSourceToConsole() {
        System.out.println(tester.getPageSource());
    }
    
    public ProjectPropertiesPage submitInvalidQuery() {
        tester.clickButton("submit-query-button");
        return this;
    }
    
    public void assertErrorDialogIsPresent() {
        tester.assertElementPresent("error-dialog");
        String errorTxt = "Project name contains incorrect characters at least one of (/\\|<>*?&:\")";
        assertTrue(tester.getElementById("error-dialog").getTextContent().trim().contentEquals(errorTxt));
    }
    
    public ProjectPropertiesPage clickErrorDialogOKButton() {
        tester.clickButtonWithText("Ok");
        return this;
    }
    
    public void checkProjectName(String name){
        String projectName = tester.getElementTextByXPath("html/body/form/fieldset[1]/input").trim();
        assertTrue(projectName.contentEquals(name));
    }
    
}
