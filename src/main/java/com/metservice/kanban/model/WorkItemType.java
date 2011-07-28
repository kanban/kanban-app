package com.metservice.kanban.model;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;

public class WorkItemType {

    private static final HtmlColour DEFAULT_COLOUR = new HtmlColour("FFFFFF");

    private final List<String> phases;
    
    private String name;
    private HtmlColour cardColour = DEFAULT_COLOUR;
    private HtmlColour backgroundColour = DEFAULT_COLOUR;
    
    public WorkItemType(String... phases) {
        this.phases = new ArrayList<String>(asList(phases));
    }

    public void setCardColour(HtmlColour cardColour) {
        this.cardColour = cardColour;
    }

    public void setBackgroundColour(HtmlColour backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    public List<String> getPhases() {
        return unmodifiableList(phases);
    }

    public String getName() {
        return name;
    }

    public HtmlColour getCardColour() {
        return cardColour;
    }

    public HtmlColour getBackgroundColour() {
        return backgroundColour;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean hasPhaseAfter(String phase) {
        if (phase == null) {
            return phases.size() > 0;
        } else {
            return phases.indexOf(phase) < phases.size() - 1;
        }
    }

    public String getPhaseAfter(String phase) {
        if (!hasPhaseAfter(phase)) {
            throw new IllegalArgumentException("there is no phase after: " + phase);
        }
        int phaseIndex = phases.indexOf(phase);
        return phases.get(phaseIndex + 1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBacklogPhase() {
        return phases.get(0);
    }

    public String getCompletedPhase() {
        return phases.get(phases.size()-1);
    }
}
