package com.metservice.kanban.charts;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import com.metservice.kanban.model.HtmlColour;

/**
 * A supplier of Paint, Stroke and Shape objects for use by Kanban charts. Given
 * the number of series (sections) to paint on a chart, KanbanDrawingSupplier 
 * generates a balanced palette of colours for maximum contrast on a chart.
 * @author Janella Espinas, Chris Cooper
 */
@SuppressWarnings("serial")
public class KanbanDrawingSupplier extends DefaultDrawingSupplier {

    private static final HueRebalancer hueRebalancer = new HueRebalancer(0.0f, 0.09f, 0.2f, 0.3f, 0.55f, 0.63f, 0.76f, 1.0f);
    private int numberOfSeries;

    /**
     * Default constructor for KanbanDrawingSupplier.
     * @param numberOfSeries - number of different sections (colours) on chart
     */
    public KanbanDrawingSupplier(int numberOfSeries) {
        this(getColours(numberOfSeries));
        this.numberOfSeries = numberOfSeries;
    }

    /**
     * Generates a balanced palette of colours for maximum contrast on a chart. 
     * @param numberOfSeries - number of different sections (colours) on chart
     * @return array of generated colours
     */
    public static Color[] getColours(int numberOfSeries) {
        Color[] colours = new Color[numberOfSeries];
        for (int i = 0; i < numberOfSeries; i++) {
            double rawHue = (double) i / (double) numberOfSeries;
            double rebalancedHue = hueRebalancer.balance(rawHue);
            colours[i] = Color.getHSBColor((float) rebalancedHue, 0.75f, 1.0f);
        }
        return colours;
    }
    
    /**
     * Generates a balanced palette of colours for maximum contrast on a chart for use in HTML. 
     * @param numberOfSeries - number of different sections (colours) on chart
     * @return array of generated colours for HTML
     */
    public static HtmlColour[] getHtmlColours(int numberOfSeries) {
        Color[] colors = getColours(numberOfSeries);
        HtmlColour[] colours = new HtmlColour[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colours[i] = new HtmlColour(colors[i]);
        }
        return colours;
    }

    /**
     * Private constructor for KanbanDrawingSupplier, given a template paintSequence. 
     * @param paintSequence - the paintSequence to use when drawing
     */
    private KanbanDrawingSupplier(Paint[] paintSequence) {
        super(paintSequence, paintSequence, null, null, null, null);
    }
    
    /**
     * Return the number of series (colours) in the KanbanDrawingSupplier.
     * @return the number of series (colours)
     */
    public int getNumberOfSeries() {
        return numberOfSeries;
    }
}
