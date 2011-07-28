package com.metservice.kanban.charts;

import com.metservice.kanban.model.HtmlColour;
import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.plot.DefaultDrawingSupplier;

@SuppressWarnings("serial")
public class KanbanDrawingSupplier extends DefaultDrawingSupplier {

    private static final HueRebalancer hueRebalancer = new HueRebalancer(0.0f, 0.09f, 0.2f, 0.3f, 0.55f, 0.63f, 0.76f, 1.0f);
    private int numberOfSeries;

    public KanbanDrawingSupplier(int numberOfSeries) {
        this(getColours(numberOfSeries));
        this.numberOfSeries = numberOfSeries;
    }

    public static Color[] getColours(int numberOfSeries) {
        Color[] colours = new Color[numberOfSeries];
        for (int i = 0; i < numberOfSeries; i++) {
            double rawHue = (double) i / (double) numberOfSeries;
            double rebalancedHue = hueRebalancer.balance(rawHue);
            colours[i] = Color.getHSBColor((float) rebalancedHue, 0.75f, 1.0f);
        }
        return colours;
    }
    
    public static HtmlColour[] getHtmlColours(int numberOfSeries) {
        Color[] colors = getColours(numberOfSeries);
        HtmlColour[] colours = new HtmlColour[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colours[i] = new HtmlColour(colors[i]);
        }
        return colours;
    }

    private KanbanDrawingSupplier(Paint[] paintSequence) {

        super(paintSequence, paintSequence, null, null, null, null);

    }
    
    public int getNumberOfSeries() {
        return numberOfSeries;
    }
}
