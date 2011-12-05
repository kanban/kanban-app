package com.metservice.kanban.jwebunit;

import net.sourceforge.jwebunit.junit.WebTester;

public class PETPage extends BoardPage {

    public String PET_TABLE_XPATH = "//table[@id=\"plannedFeatures\"]/tbody";

    public PETPage(WebTester tester) {
        super(tester);
    }

    public PETPage checkPetAverageCaseValue(String size){
        tester.assertElementPresentByXPath(PET_TABLE_XPATH + "/tr[3]/td[5][contains(text(), \""+size+"\")]");
        return this;
    }
    
    public PETPage checkPetWorstCaseValue(String size){
        tester.assertElementPresentByXPath(PET_TABLE_XPATH + "/tr[3]/td[6][contains(text(), \""+size+"\")]");
        return this;
    }
    
    public PETPage checkFeatureDescription(String description){
        tester.assertElementPresentByXPath(PET_TABLE_XPATH + "/tr[3]/td[3][contains(text(), \""+description+"\")]");
        return this;
    }

    public PETPage clickMustHave(int itemId) {

        tester.clickElementByXPath("//input[@id='must-have-" + itemId + "']");
        
        return new PETPage(tester);
    }
}
