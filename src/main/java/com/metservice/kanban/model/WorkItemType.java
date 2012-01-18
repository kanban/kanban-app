package com.metservice.kanban.model;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

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

    public List<String> getWallPhases() {
        return phases.subList(1, phases.size() - 1);
    }

    public String getCompletedPhase() {
        return phases.get(phases.size()-1);
    }

    public boolean isPhaseBefore(String phaseA, String phaseB) {
        int indexA = getPhases().indexOf(phaseA);
        int indexB = getPhases().indexOf(phaseB);
        return indexA < indexB;
    }

    public boolean isPhaseAfter(String phaseA, String phaseB) {
        return !StringUtils.equals(phaseA, phaseB) && !isPhaseBefore(phaseA, phaseB);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


}
