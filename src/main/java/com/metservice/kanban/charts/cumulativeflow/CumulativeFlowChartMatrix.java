package com.metservice.kanban.charts.cumulativeflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.LocalDate;

import com.metservice.kanban.charts.ChartUtils;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.utils.WorkingDayUtils;

public class CumulativeFlowChartMatrix {

    private Map<LocalDate, Map<String, Integer>> map;
    private List<String> phases;
	private LocalDate startDate;
	private LocalDate endDate;

    // add start date and end date instead of "reference date" here
    public CumulativeFlowChartMatrix(List<String> phases, LocalDate startDate, LocalDate endDate) {
        this.phases = phases;
        this.startDate = startDate;
        this.endDate = endDate;
        map = new HashMap<LocalDate, Map<String, Integer>>();
    }

    public void registerWorkItem(WorkItem workItem) {
        if (workItem.isExcluded()) {
            return;
        }
        
        String phase = phases.get(0);
        LocalDate localDate = workItem.getDate(phase);
        while (!localDate.isAfter(endDate)) {
            if (phases.contains(phase) && WorkingDayUtils.isWorkingDay(localDate)) {
                register(localDate, phase);
            }
            localDate = nextWorkingDay(localDate);
            phase = workItem.getPhaseOnDate(localDate);
        }
    }

    public double[][] getData() {
        List<LocalDate> dates = getOrderedListOfDates();
        double[][] data = new double[phases.size()][dates.size()];
        int phaseIndex = phases.size() - 1;
        for (String phase : phases) {
            int dateIndex = 0;
            for (LocalDate date : dates) {
                data[phaseIndex][dateIndex] = getQuantityOfItemsOnDateAndPhase(date, phase);
                dateIndex++;
            }
            phaseIndex--;
        }
        return data;
    }

    private LocalDate nextWorkingDay(LocalDate date) {
        LocalDate nextDay = date.plusDays(1);
        while (!WorkingDayUtils.isWorkingDay(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    public Integer getQuantityOfItemsOnDateAndPhase(LocalDate date, String phase) {
        Map<String, Integer> phaseIntegerMap = map.get(date);
        if (phaseIntegerMap == null) {
            return 0;
        }
        
        Integer result = phaseIntegerMap.get(phase);
        return result == null ? 0 : result;
    }

    private void register(LocalDate date, String phase) {
        Map<String, Integer> phaseIntegerMap = map.get(date);
        if (phaseIntegerMap == null) {
            phaseIntegerMap = new HashMap<String, Integer>();
        }
        Integer count = phaseIntegerMap.get(phase);
        if (count == null) {
            count = 0;
        }
        phaseIntegerMap.put(phase, count + 1);
        map.put(date, phaseIntegerMap);
    }

    // limit dates to between start and end dates only
    @SuppressWarnings("unchecked")
    public List<LocalDate> getOrderedListOfDates() {
        List<LocalDate> dates = new ArrayList<LocalDate>(map.keySet());
        Collections.sort(dates);
        // make sure there are index values for the sublist
        int startIndex = dates.indexOf(startDate) == -1 ? 0 : dates.indexOf(startDate);
        int endIndex = dates.indexOf(endDate) == -1 ? dates.size() : dates.indexOf(endDate) + 1;
        if (endIndex > dates.size()){
        	endIndex = dates.size();
        }
        dates = dates.subList(startIndex, endIndex);

        return dates;
    }

}
