package com.metservice.kanban.model;

public class Colour {
    private final String rgbInHexadecimal;
    
    public Colour(String rgbInHexadecimal) {
        if (!rgbInHexadecimal.matches("\\p{XDigit}{6}")) {
            throw new IllegalArgumentException("invalid colour value: " + rgbInHexadecimal);
        }        
        this.rgbInHexadecimal = rgbInHexadecimal;
    }
    
    @Override
    public String toString() {
        return "#" + rgbInHexadecimal;
    }
}
