package com.metservice.kanban.model;

import java.awt.Color;
import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;



public class ColourTest {

    @Test
    public void testValidHexColour() {
        new Colour("123456");
        new Colour("AABBCC");
        new Colour("BCCD44");
        new Colour("000000");
    }
    
    @Test
    public void testValidColor() {
        Colour colour = new Colour(new Color(0, 128, 255));
        assertThat(colour.toString(), is("#0080FF"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoreThanSixDigitsColour() {
        new Colour("1234566");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAlgarism() {
        new Colour("G332222");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidExpression() {
        new Colour("#332222");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidExpression2() {
        new Colour("#000000");
    }

    @Test
    public void testToString() {
        Colour colour = new Colour("FF0011");
        Assert.assertThat(colour.toString(), is("#FF0011"));
    }
    
}
