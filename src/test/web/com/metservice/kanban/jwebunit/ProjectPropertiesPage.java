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
    
    public BoardPage clickSubmitQueryButton() {
        tester.clickButton("submit-query-button");
        return new BoardPage(tester);
    }
    
    public ProjectPropertiesPage enterNotes(String name) {
        tester.setTextField("notes", name);
        return this;
    }

    public ProjectPropertiesPage enterAverageCase(String size) {
        tester.setTextField("averageCaseEstimate", size);
        return this;
    }
    
    public ProjectPropertiesPage enterWorstCase(String size) {
        tester.setTextField("worstCaseEstimate", size);
        return this;
    }

    public ProjectPropertiesPage setParent(String name) {
        tester.selectOption("parentId", name);
        return this;
    }

    public ProjectPropertiesPage tickExcludeBox() {
        tester.getTestingEngine().setWorkingForm("edit", 0);
        tester.checkCheckbox("excluded");
        return this;
    }

    public ProjectPropertiesPage untickExcludeBox() {
        tester.getTestingEngine().setWorkingForm("edit", 0);
        tester.uncheckCheckbox("excluded");
        return this;
    }
    
    public BoardPage clickSaveButton() {
        tester.clickButton("save-button");
        return new BoardPage(tester);
    }

    public PrintPage clickSaveAndPrintButton() {
        tester.clickButton("save-and-print-button");
        return new PrintPage(tester);
    }

    public BoardPage clickDeleteButtonAndConfirm() {
        tester.setExpectedJavaScriptConfirm("Permanently delete this work item?", true);
        tester.clickButton("delete-button");
        return new BoardPage(tester);
    }

    public ProjectPropertiesPage assertParentIs(String featureName) {
        tester.assertSelectedOptionEquals("parentId", featureName);
        return this;
    }
    
    public ProjectPropertiesPage assertExcludeBoxIs(Boolean excluded) {
        tester.getTestingEngine().setWorkingForm("edit", 0);
        if (excluded) {
            tester.assertCheckboxSelected("excluded");
        } else {
            tester.assertCheckboxNotSelected("excluded");
        }
        return this;
    }

    public void dumpPageSourceToConsole() {
        System.out.println(tester.getPageSource());
    }
}
