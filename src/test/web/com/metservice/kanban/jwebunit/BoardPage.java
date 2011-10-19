package com.metservice.kanban.jwebunit;

import net.sourceforge.jwebunit.junit.WebTester;

public class BoardPage {

    private final WebTester tester;

    public static BoardPage openProject(String projectName) {
        WebTester tester = new WebTester();
        tester.beginAt("http://localhost:8008/kanban");
        tester.clickLinkWithExactText("Test project");
        return new BoardPage(tester);
    }

    public BoardPage(WebTester tester) {
        this.tester = tester;
    }

    public BoardPage clickBacklogButton() {
        tester.clickElementByXPath("//div[@id='backlog-button']/a");
        return this;
    }

    public WallPage clickWallButton() {
        tester.clickElementByXPath("//div[@id='wall']");
        return new WallPage(tester);
    }
    
    public BoardPage clickAdvance(String name) {
        String xPath ="//td[.='" + name + "']/../td[8]/img"; 
        tester.assertElementPresentByXPath(xPath);            
        tester.clickElementByXPath(xPath);
        return this;
    }
    
    public WorkItemPage clickAddFeatureButton() {
        tester.clickElementByXPath("//div[@id='add-top-level-item-button']/div/a");
        return new WorkItemPage(tester);
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
        tester.clickElementByXPath("//div[@id='burn-up-chart-button']");
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
}
