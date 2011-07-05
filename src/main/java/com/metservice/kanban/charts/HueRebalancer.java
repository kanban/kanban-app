package com.metservice.kanban.charts;
public class HueRebalancer {

    private final double[] evenlySpreadHsbHues;
    private final int numberOfSegments;

    public HueRebalancer(double... evenlySpreadHsbHues) {
        this.evenlySpreadHsbHues = evenlySpreadHsbHues;
        this.numberOfSegments = evenlySpreadHsbHues.length - 1;
    }

    public double balance(double unbalancedHue) {
        unbalancedHue %= 1.0;
        
        int segmentNumber = (int) (unbalancedHue * numberOfSegments);
        double positionWithinSegment = unbalancedHue * numberOfSegments - segmentNumber;

        double segmentStart = evenlySpreadHsbHues[segmentNumber];
        double segmentLength = evenlySpreadHsbHues[segmentNumber + 1] - segmentStart;

        return segmentStart + segmentLength * positionWithinSegment;
    }
}
