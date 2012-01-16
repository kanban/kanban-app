package com.metservice.kanban.jwebunit;

import net.sourceforge.jwebunit.junit.WebTester;

public class JournalPage extends BoardPage{

    public JournalPage(WebTester tester) {
        super(tester);
    }

    public JournalPage clickAddEntry() {
        tester.clickElementByXPath("//a[@id='add-entry-button']");
        tester.assertTextInElement("ui-dialog-title-journal-add-dialog", "Add journal item");
        return this;
    }
    
    public JournalPage enterJournalDate(String text){
        tester.setTextField("journal-date", text);
        return this;
    }
    
    public JournalPage enterJournalText(String text){
        tester.setTextField("journal-text", text);
        return this;
    }
    
    public JournalPage clickOkButton() {
        tester.clickButtonWithText("Ok");
        return new JournalPage(tester);
    }
    
    public JournalPage clickCancelButton() {
        tester.clickButtonWithText("Cancel");
        return new JournalPage(tester);
    }
    
    public void clearEntryDate(){
        tester.setTextField("journal-date", "");
    }
    
    public void assertValidationErrorShows(String text){
        tester.assertTextInElement("validation-error", text);
        tester.assertTextPresent(text);
    }
    
    public void assertJournalTextIsNotPresent(String text){
        final String journalTextId = "journal-text-1";
        //TODO: adding a timeout isn't the best way to wait for the page to reload to check element 
        //is not present. It may potentially cause random test failures server may not respond in the
        //timeout period. Also test run times takes longer.
        int timeout = 5000;
        //wait for 5 seconds for the page to reload
        try {
            Thread.sleep(timeout);
        } catch (Exception e) {
        }
        
        tester.assertElementNotPresent(journalTextId);
    }
}
