package com.metservice.kanban.csv;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.FEBRUARY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.util.GregorianCalendar;
import org.joda.time.LocalDate;
import org.junit.Test;
import com.metservice.kanban.utils.DateUtils;

public class DateFormatUtilsTest {

    @Test
    public void parsesADate() {
        LocalDate parsedDate = DateUtils.parseIsoDate("2011-02-09");

        assertThat(parsedDate.getYear(), is(2011));
        assertThat(parsedDate.getMonthOfYear(), is(2));
        assertThat(parsedDate.getDayOfMonth(), is(9));
    }
    
    @Test(expected = RuntimeException.class)
    public void throwsRuntimeExceptionIfDateCannotBeParsed() {
        DateUtils.parseIsoDate("something that cannot be parsed as a date");
    }
    
    @Test
    public void formatsADate() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.set(YEAR, 2011);
        calendar.set(MONTH, FEBRUARY);
        calendar.set(DAY_OF_MONTH, 9);
        
        assertThat(DateUtils.parseIsoDate("2011-02-09"), is(LocalDate.fromCalendarFields(calendar)));
    }
}
