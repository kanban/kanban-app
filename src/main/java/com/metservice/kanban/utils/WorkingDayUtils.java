package com.metservice.kanban.utils;

import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import org.joda.time.LocalDate;

//TODO This class needs unit tests.

/**
 * @author Janella Espinas, Liam O'Connor
 * 
 * Helper utilities revolving around letting people enjoying their public holidays and weekends.
 * After all, isn't that what Labour Day is all about?
 */
public class WorkingDayUtils {

    private static final String[] PUBLIC_HOLIDAYS = new String[] {
        "2010-10-25",
        "2010-12-27",
        "2010-12-28",
        "2011-01-03",
        "2011-01-04",
        "2011-01-25",
        "2011-02-06",
        "2011-04-22",
        "2011-04-25",
        "2011-06-06",
        "2011-10-24",
        "2011-12-26",
        "2011-12-27",
        "2012-01-23",
    };

    private WorkingDayUtils() {
    }

    /**
     * Takes two dates and works out the number of working days between them.
     * @param inceptionDate
     * @param acceptanceDate
     * @return
     */
    public static int getWorkingDaysBetween(LocalDate inceptionDate, LocalDate acceptanceDate) {
        int days = 0;
        LocalDate currentDate = inceptionDate;

        while (currentDate.isBefore(acceptanceDate)) {
            if (isWorkingDay(currentDate)) {
                days++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return days;
    }

    /**
     * Takes a date and works out whether or not it is a work day or not.
     * @param localDate
     * @return
     */
    public static boolean isWorkingDay(LocalDate localDate) {
        if (localDate.getDayOfWeek() == SATURDAY || localDate.getDayOfWeek() == SUNDAY) {
            return false;
        }
        for (String publicHoliday : PUBLIC_HOLIDAYS) {
            if (localDate.toString().equals(publicHoliday)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * If a day is working day, returns it, otherwise search for the next
     * working day (e.g. Monday)
     * 
     * @param localDate
     * @return
     */
    public static LocalDate nextWorkingDay(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        LocalDate nextDay = localDate;
        while (!isWorkingDay(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }
}
