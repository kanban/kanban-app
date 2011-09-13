package com.metservice.kanban.model;

import java.awt.Color;
import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;



public class HtmlColourTest {

    @Test
    public void testValidHexColour() {
        new HtmlColour("123456");
        new HtmlColour("AABBCC");
        new HtmlColour("BCCD44");
        new HtmlColour("000000");
    }
    
    @Test
    public void testValidColor() {
        HtmlColour colour = new HtmlColour(new Color(0, 128, 255));
        assertThat(colour.toString(), is("#0080FF"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoreThanSixDigitsColour() {
        new HtmlColour("1234566");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAlgarism() {
        new HtmlColour("G332222");
    }

    @Test
    public void testToString() {
        HtmlColour colour = new HtmlColour("FF0011");
        Assert.assertThat(colour.toString(), is("#FF0011"));
    }
    
}
