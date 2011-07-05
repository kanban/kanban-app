package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import org.junit.Assert;
import org.junit.Test;
import com.metservice.kanban.model.Colour;


public class ColourTest {

    @Test
    public void testValidColour() {
        new Colour("123456");
        new Colour("AABBCC");
        new Colour("BCCD44");
        new Colour("000000");
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
