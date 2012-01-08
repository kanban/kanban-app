package com.metservice.kanban.jwebunit;

import net.sourceforge.jwebunit.junit.WebTester;

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
}
