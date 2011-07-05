package com.metservice.kanban.utils;

import org.joda.time.LocalDate;

//TODO This class needs unit tests.
//TODO Consider replacing with Jodatime

public class Day implements Comparable<Day> {

    private LocalDate date;
    public Day(LocalDate date) {

        this.date = date;
    }

    @Override
    public int compareTo(Day o) {
        return this.date.compareTo(o.date);
    }

    public String toString() {
        return DateUtils.formatConventionalNewZealandDate(date);
    }

}
