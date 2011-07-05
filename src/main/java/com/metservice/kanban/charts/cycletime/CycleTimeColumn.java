package com.metservice.kanban.charts.cycletime;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.utils.WorkingDayUtils;

public class CycleTimeColumn {

    private String name;

    private List<CycleTimeFragment> fragments;

    public CycleTimeColumn(String name) {
        this.name = name;
        fragments = new ArrayList<CycleTimeFragment>();
    }

    public void addFragment(String name, Integer weight) {
        fragments.add(new CycleTimeFragment(name, weight));
    }

    public int numberOfFragments() {
        return fragments.size();
    }

    public String getFragmentName(int index) {
        return fragments.get(index).getName();
    }

    public Integer getFragmentWeight(int index) {
        return fragments.get(index).getWeight();
    }

    public String getName() {
        return name;
    }

    public static CycleTimeColumn buildCycleTimeColumnFromWorkItem(WorkItem workItem) {
        List<String> phases = workItem.getType().getPhases();
        List<String> phasesAfterInitialPhase = phases.subList(1, phases.size());
        
        CycleTimeColumn column = new CycleTimeColumn(Integer.toString(workItem.getId()));
        
        LocalDate previousDate = null;
        String previousPhase = null;
        for (String phase : phasesAfterInitialPhase) {
            LocalDate date = dateWhenPhaseWasCompleted(workItem, phase);
            if (previousDate != null && previousPhase != null) {
                int diffInDays = WorkingDayUtils.getWorkingDaysBetween(previousDate, date);
                column.addFragment(previousPhase, diffInDays);
            }
            previousDate = date;
            previousPhase = phase;
        }
        return column;
    }

    private static LocalDate dateWhenPhaseWasCompleted(WorkItem workItem, String phase) {
        LocalDate date = workItem.getDate(phase);
        if (date == null) {
            String message = String.format("Phase %s was not completed for item %d-%s ",
                phase, workItem.getId(), workItem.getName());
            throw new InconsistentWorkItemException(message);
        }
        return date;
    }

}
