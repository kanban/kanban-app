package com.metservice.kanban.utils;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

//TODO This class needs unit tests.

public class DateUtils {

    public static final DateTimeZone NEW_ZEALAND_TIME = DateTimeZone.forID("Pacific/Auckland");
    

    public static final DateTimeFormatter conventionalNewZealandDateFormat;
    public static final DateTimeFormatter isoFormat = ISODateTimeFormat.date();

    static {
        conventionalNewZealandDateFormat = new DateTimeFormatterBuilder()
                .appendDayOfMonth(2)
                .appendLiteral('/')
                .appendMonthOfYear(2)
                .appendLiteral('/')
                .appendYear(4, 4)
                .toFormatter();
    }

    public static LocalDate parseConventionalNewZealandDate(String dateString) {
        return conventionalNewZealandDateFormat.parseDateTime(dateString).toLocalDate();
    }

    public static LocalDate parseIsoDate(String dateString) {
        return isoFormat.parseDateTime(dateString).toLocalDate();
    }

    public static String formatConventionalNewZealandDate(LocalDate date) {
        return date.toString(conventionalNewZealandDateFormat);
    }

    public static String formatIsoDate(LocalDate date) {
        return date.toString(isoFormat);
    }
    
    public static LocalDate currentLocalDate() {
        return new LocalDate(NEW_ZEALAND_TIME);
    }
}
