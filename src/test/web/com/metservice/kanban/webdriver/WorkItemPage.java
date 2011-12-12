package com.metservice.kanban.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WorkItemPage {

    private final WebDriver driver;

    public WorkItemPage(WebDriver driver) {
        this.driver = driver;
    }

    public WorkItemPage enterName(String name) {
        System.out.println(driver.getPageSource());
        driver.findElement(By.name("name")).sendKeys(name);
        return this;
    }

    public BoardPage clickSaveButton() {
        driver.findElement(By.id("save-button")).click();
        return new BoardPage(driver);
    }

    public BoardPage clickDeleteButtonAndConfirm() {
        driver.findElement(By.id("delete-button"));
        driver.switchTo().alert().accept();

        return new BoardPage(driver);
    }
}
