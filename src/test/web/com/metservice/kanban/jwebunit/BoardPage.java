package com.metservice.kanban.jwebunit;

import static com.metservice.kanban.tests.util.TestUtils.createTestProject;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import net.sourceforge.jwebunit.junit.WebTester;
import org.junit.rules.TemporaryFolder;

public class BoardPage {

    protected final WebTester tester;

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
        return new BoardPage(tester);
    }
    
    public BoardPage clickCompleteButton() {
        tester.clickElementByXPath("//a[@id='complete']");
        return this;
    }

    public PETPage clickPETButton() {
        tester.clickElementByXPath("//a[@id='pet']");
        return new PETPage(tester);
    }

    public WallPage clickWallButton() {
        //        tester.clickElementByXPath("//a[@id='wall']");
        tester.clickLink("wall");
        tester.assertTitleEquals("Kanban: wall");
        return new WallPage(tester);
    }
    
    public BoardPage clickAdvance(String name) {
        String xPath = "//td[.='" + name + "']/../td[8]/a/img";
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

    public void assertItemNameIsIndicatedMustHave(int i) {
        String itemClass = tester.getElementAttributeByXPath("//td[@id='item-name-" + i + "']", "class");
        assertTrue("Item should contain itemMustHave style", itemClass.contains("itemMustHave"));
        assertFalse("Item mustn't contain itemNiceToHave style", itemClass.contains("itemNiceToHave"));
    }

    public void assertItemNameIsIndicatedNiceToHave(int i) {
        String itemClass = tester.getElementAttributeByXPath("//td[@id='item-name-" + i + "']", "class");
        assertTrue("Item should contain itemNiceToHave style", itemClass.contains("itemNiceToHave"));
        assertFalse("Item mustn't contain itemMustHave style", itemClass.contains("itemMustHave"));
    }

    public BoardPage enterQuickName(String nameValue) {
        tester.clickElementByXPath("//input[@id='quick-editor-name'");
        tester.setTextField("name", nameValue);
        return this;
    }


}
