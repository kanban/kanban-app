package com.metservice.kanban.jwebunit;

import static org.junit.Assert.assertEquals;
import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.junit.WebTester;

public class PrintPage {

    private WebTester tester;

    public PrintPage(WebTester tester) {
        this.tester = tester;
    }

    public void assertIsPrintPage() {
        tester.assertTitleEquals("Kanban: printing items");
    }

    public void assertItemHasName(int number, String name) {
        IElement itemName = tester.getElementById("item-name-" + number);
        assertEquals(name, itemName.getTextContent());

    }

}
