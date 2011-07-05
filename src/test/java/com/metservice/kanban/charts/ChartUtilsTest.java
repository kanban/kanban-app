package com.metservice.kanban.charts;

import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.util.Collections;
import java.util.List;
import org.hamcrest.collection.IsCollectionWithSize;
import org.joda.time.LocalDate;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class ChartUtilsTest {

    private final WorkItemType TWO_PHASES = new WorkItemType("phase 1", "phase 2");

    @Test
    public void canReturnWorkingDaysForEmptyWorkItemsList() {
        List<LocalDate> dates = ChartUtils.getWorkingDaysForWorkItems(Collections.<WorkItem> emptyList(),
            parseIsoDate("2011-06-13"));
        assertThat(dates, IsCollectionWithSize.hasSize(1));
        assertThat(dates.get(0), is(parseIsoDate("2011-06-13")));
    }

    @Test
    public void canReturnWorkingDaysForWorkItems() {
        WorkItem workItem = new WorkItem(1, new WorkItemType("phase"));
        workItem.setDateAsString("phase", "2011-06-10");

        List<LocalDate> dates = ChartUtils.getWorkingDaysForWorkItems(asList(workItem), parseIsoDate("2011-06-13"));

        assertThat(dates.get(0), is(parseIsoDate("2011-06-10")));
        assertThat(dates.get(1), is(parseIsoDate("2011-06-13")));
    }

    @Test
    public void canCalculateTheFirstDateForACollectionOfWorkItems() {
        WorkItem workItem1 = new WorkItem(1, TWO_PHASES);
        workItem1.setDateAsString("phase 1", "2011-06-02");
        workItem1.setDateAsString("phase 2", "2011-06-03");

        WorkItem workItem2 = new WorkItem(2, TWO_PHASES);
        workItem2.setDateAsString("phase 1", "2011-06-01");
        workItem2.setDateAsString("phase 2", "2011-06-03");

        LocalDate date = ChartUtils.getFirstDate(asList(workItem1, workItem2));

        assertThat(date, is(parseIsoDate("2011-06-01")));
    }

    @Test
    public void knowsTheWorkingDaysWithinAnInterval() {
        List<LocalDate> dates = ChartUtils.getWorkingDays(parseIsoDate("2011-05-30"), parseIsoDate("2011-06-02"));

        assertThat(dates.get(0), is(parseIsoDate("2011-05-30")));
        assertThat(dates.get(1), is(parseIsoDate("2011-05-31")));
        assertThat(dates.get(2), is(parseIsoDate("2011-06-01")));
        assertThat(dates.get(3), is(parseIsoDate("2011-06-02")));
    }

    @Test
    public void excludesWeekendsAndPublicHolidaysFromWorkingDays() {
        List<LocalDate> dates = ChartUtils.getWorkingDays(parseIsoDate("2011-06-03"), parseIsoDate("2011-06-07"));

        assertThat(dates.get(0), is(parseIsoDate("2011-06-03")));
        assertThat(dates.get(1), is(parseIsoDate("2011-06-07")));
    }

    @Test
    public void excludesFirstAndLastDatesOfWorkingDayRangesIfTheyFallOnWeekends() {
        List<LocalDate> dates = ChartUtils.getWorkingDays(parseIsoDate("2011-06-12"), parseIsoDate("2011-06-18"));

        assertThat(dates.get(0), is(parseIsoDate("2011-06-13")));
        assertThat(dates.get(dates.size() - 1), is(parseIsoDate("2011-06-17")));
    }
    
    @Test
    public void knowsTheNextWorkingDay() {
        LocalDate nextWorkingDay = ChartUtils.nextWorkingDayAfter(parseIsoDate("2011-06-03"));
        
        assertThat(nextWorkingDay, is(parseIsoDate("2011-06-07")));
    }
}
