package com.metservice.kanban.model;

import java.awt.Color;

public class HtmlColour {
    private final String rgbInHexadecimal;
    
    public HtmlColour(String rgbInHexadecimal) {
    	if(rgbInHexadecimal.startsWith("#")){
    		rgbInHexadecimal = rgbInHexadecimal.substring(1);
    	}
        if (!rgbInHexadecimal.matches("\\p{XDigit}{6}")) {
            throw new IllegalArgumentException("invalid colour value: " + rgbInHexadecimal);
        }        
        this.rgbInHexadecimal = rgbInHexadecimal;
    }
    
    public HtmlColour(Color color) {
        String rgb = Integer.toHexString(color.getRGB());
        rgb = rgb.substring(2, rgb.length());
        this.rgbInHexadecimal = rgb.toUpperCase();
    }
    
    @Override
    public String toString() {
        return "#" + rgbInHexadecimal;
    }
}
