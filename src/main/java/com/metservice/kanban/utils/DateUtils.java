package com.metservice.kanban.utils;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO This class needs unit tests.


/**
 * Helper class for dates, specifically configured for New Zealand timezone.
 */
public class DateUtils {

    private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final DateTimeZone NEW_ZEALAND_TIME = DateTimeZone.forID("Pacific/Auckland");
    

    public static final DateTimeFormatter CONVENTIONAL_NEW_ZEALAND_DATE_FORMAT;
    public static final DateTimeFormatter ISO_FORMAT = ISODateTimeFormat.date();

    public static final String DATE_FORMAT_STR = "yyyy-MM-dd";

    static {
        CONVENTIONAL_NEW_ZEALAND_DATE_FORMAT = new DateTimeFormatterBuilder()
                .appendDayOfMonth(2)
                .appendLiteral('/')
                .appendMonthOfYear(2)
                .appendLiteral('/')
                .appendYear(4, 4)
                .toFormatter();
    }

    private DateUtils() {
    }

    /**Returns a LocalDate parsed in conventional New Zealand (dd/mm/yy) format.
     * @param dateString
     * @return
     */
    public static LocalDate parseConventionalNewZealandDate(String dateString) {
        return CONVENTIONAL_NEW_ZEALAND_DATE_FORMAT.parseDateTime(dateString).toLocalDate();
    }
    
    /**
     * Returns a LocalDate parsed in the default ISO (yy/mm/dd) format.
     * @param dateString
     * @return
     */
    public static LocalDate parseIsoDate(String dateString) {
        return ISO_FORMAT.parseDateTime(dateString).toLocalDate();
    }

    /**
     * Converts from LocalDate format to New Zealand (dd/mm/yy) format.
     * @param date
     * @return
     */
    public static String formatConventionalNewZealandDate(LocalDate date) {
        return date.toString(CONVENTIONAL_NEW_ZEALAND_DATE_FORMAT);
    }

    /**
     * Converts from LocalDate format to ISO (yy/mm/dd) format.
     * @param date
     * @return
     */
    public static String formatIsoDate(LocalDate date) {
        return date.toString(ISO_FORMAT);
    }
    
    /**
     * Returns the LocalDate specifically in the New Zealand timezone.
     * @return
     */
    public static LocalDate currentLocalDate() {
        return new LocalDate(NEW_ZEALAND_TIME);
    }

    public static LocalDate parseDate(String startDate, LocalDate defaultValue) {
        LocalDate start;
        try {
            if (StringUtils.isNotEmpty(startDate)) {
                start = LocalDate.parse(startDate);
            } else {
                start = defaultValue;
            }
        } catch (RuntimeException e) {
            logger.warn("Cannot parse date {}", startDate);
            logger.warn("Got exception: ", e);
            start = defaultValue;
        }
        return start;
    }
}
