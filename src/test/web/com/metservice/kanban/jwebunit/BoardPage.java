package com.metservice.kanban.jwebunit;

import static com.metservice.kanban.tests.util.TestUtils.createTestProject;
import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;
import java.io.IOException;

import org.junit.rules.TemporaryFolder;

import net.sourceforge.jwebunit.junit.WebTester;

public class BoardPage {

    private final WebTester tester;

    public static BoardPage openProject(TemporaryFolder kanbanHome, String projectName, String sourceResourcePath) throws IOException {
    	File root = kanbanHome.getRoot();
    	cleanProject(kanbanHome);
        createTestProject(root, projectName, sourceResourcePath);
    	return createBoardPage(projectName);
    }
    
    public static BoardPage createBoardPage(String projectName){
        WebTester tester = new WebTester();
        tester.beginAt("http://localhost:8008/kanban");
        tester.clickLinkWithExactText(projectName);
        return new BoardPage(tester);
    }

    public static void cleanProject(TemporaryFolder kanbanHome) throws IOException {
        File root = kanbanHome.getRoot();
        deleteDirectory(root);
        root.mkdir();
    }
    
    public BoardPage(WebTester tester) {
        this.tester = tester;
    }

    public BoardPage clickBacklogButton() {
        tester.clickElementByXPath("//a[@id='backlog-button']");
        return this;
    }
    
    public PETPage clickPETButton() {
        tester.clickElementByXPath("//a[@id='pet']");
        return new PETPage(tester);
    }

    public WallPage clickWallButton() {
        tester.clickElementByXPath("//a[@id='wall']");
        return new WallPage(tester);
    }
    
    public BoardPage clickAdvance(String name) {
        String xPath ="//td[.='" + name + "']/../td[8]/img"; 
        tester.assertElementPresentByXPath(xPath);            
        tester.clickElementByXPath(xPath);
        return this;
    }
    
    public WorkItemPage clickAddFeatureButton() {
        tester.clickElementByXPath("//a[@id='add-top-level-item-button']");
        return new WorkItemPage(tester);
    }

    public WorkItemPage clickAddStoryButton() {
    	return clickAddFeatureButton();
    }
    
    public WorkItemPage clickAddStoryButton(String name) {
        // If this fails we couldn't find the <div>.
        tester.assertElementPresentByXPath(
            "//div[@class='feature' and .//span[@class='work-item-name' and .='" + name + "']]");
        
        // If this fails, we found the <div> but couldn't find the <img> inside it.
        tester.clickElementByXPath(
            "//div[@class='feature' and .//span[@class='work-item-name' and .='" + name + "']]//img[@class='add']");
        return new WorkItemPage(tester);
    }
    
    

    public WorkItemPage clickEditStoryButton(String name) {
        return clickEditButton("story", name);
    }

    public WorkItemPage clickEditFeatureButton(String name) {
        return clickEditButton("feature", name);
    }

    private WorkItemPage clickEditButton(String workItemTypeName, String workItemName) {
        tester.clickElementByXPath("//td[.='" + workItemName + "']/../td[2]/a");
        return new WorkItemPage(tester);
    }

    public ChartPage clickFeatureCycleTimeChartButton() {
        tester.clickElementByXPath("//a[@id='cycle-time-chart-1-button']");
        return new ChartPage(tester);
    }
    
    public ChartPage clickBurnUpChartButton() {
        tester.clickElementByXPath("//a[@id='burn-up-chart-button']");
        return new ChartPage(tester);
    }
    
    public String clickDownloadFeaturesButton() {
        tester.clickElementByXPath("//div[@id='feature-download-button']");
        return tester.getPageSource();
    }

    public void assertFeatureNotPresent(String name) {
        tester.assertElementNotPresentByXPath("//td[.='" + name + "']");
    }

    public void assertFeatureIsPresent(String name) {
        tester.assertElementPresentByXPath("//td[.='" + name + "']");
    }
    
    public void assertProjectListIsSorted(){
    	tester.assertElementPresentByXPath("//select[@id=\"projectPicker\"]/option[1][contains(text(), \"123\")]");
    	tester.assertElementPresentByXPath("//select[@id=\"projectPicker\"]/option[2][contains(text(), \"A1z\")]");
    	tester.assertElementPresentByXPath("//select[@id=\"projectPicker\"]/option[3][contains(text(), \"ABC\")]");
    	tester.assertElementPresentByXPath("//select[@id=\"projectPicker\"]/option[4][contains(text(), \"acb\")]");
    	tester.assertElementPresentByXPath("//select[@id=\"projectPicker\"]/option[5][contains(text(), \"XYZ\")]");
    	tester.assertElementPresentByXPath("//select[@id=\"projectPicker\"]/option[6][contains(text(), \"xzy\")]");
    }
}
