package com.metservice.kanban.charts.cumulativeflow;

import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class CumulativeFlowChartMatrixTest {

    private static LocalDateFormatter formatter = new LocalDateFormatter();
    private static class LocalDateFormatter {
        public LocalDate parse(String pattern) throws ParseException {
            return LocalDate.fromDateFields(new SimpleDateFormat("dd/MM/yyyy").parse(pattern));
        }
    }

    // TODO boil these tests down to their bare minimum (too many asserts)

    @Test
    public void testMatrixFirstCase() throws ParseException {
        WorkItemType type = new WorkItemType("phase1", "phase2", "phase3", "phase4");
        List<String> phases = type.getPhases();

        CumulativeFlowChartMatrix matrix = new CumulativeFlowChartMatrix(phases, null, new LocalDate(2011, 2, 24));
        List<WorkItem> workItems = buildListFirstCase(type);
        for (WorkItem workItem : workItems) {
            matrix.registerWorkItem(workItem);
        }

        verifyDate(phases, matrix, "08/02/2011", new int[] {1, 0, 0, 0});
        verifyDate(phases, matrix, "09/02/2011", new int[] {2, 0, 0, 0});
        verifyDate(phases, matrix, "10/02/2011", new int[] {3, 0, 0, 0});
        verifyDate(phases, matrix, "11/02/2011", new int[] {2, 1, 0, 0});
        verifyDate(phases, matrix, "14/02/2011", new int[] {1, 2, 0, 0});
        verifyDate(phases, matrix, "15/02/2011", new int[] {1, 1, 1, 0});
        verifyDate(phases, matrix, "16/02/2011", new int[] {1, 1, 1, 0});
        verifyDate(phases, matrix, "17/02/2011", new int[] {1, 0, 2, 0});
        verifyDate(phases, matrix, "18/02/2011", new int[] {1, 0, 2, 0});
        verifyDate(phases, matrix, "21/02/2011", new int[] {1, 0, 1, 1});
        verifyDate(phases, matrix, "22/02/2011", new int[] {1, 0, 1, 1});
        verifyDate(phases, matrix, "23/02/2011", new int[] {1, 0, 1, 1});
        verifyDate(phases, matrix, "24/02/2011", new int[] {0, 1, 1, 1});

        List<LocalDate> dates = matrix.getOrderedListOfDates();
        assertThat(dates.size(), is(13));
        assertThat(dates.get(0), is(new LocalDate(2011, 2, 8)));
        assertThat(dates.get(dates.size() - 1), is(new LocalDate(2011, 2, 24)));

        verifyExistenceOfDates(dates, new String[] {
            "08/02/2011",
            "09/02/2011",
            "10/02/2011",
            "11/02/2011",
            "14/02/2011",
            "15/02/2011",
            "16/02/2011",
            "17/02/2011",
            "18/02/2011",
            "21/02/2011",
            "22/02/2011",
            "23/02/2011",
            "24/02/2011",
            });

        verifyNonExistenceOfDates(dates, new String[] {
            "12/02/2011",
            "13/02/2011",
            "19/02/2011",
            "20/02/2011",
            });

    }

    @Test
    public void testMatrixExcludeItemFromReport() throws ParseException {
        WorkItemType type = new WorkItemType("phase1", "phase2", "phase3", "phase4");
        List<String> phases = type.getPhases();
        CumulativeFlowChartMatrix matrix = new CumulativeFlowChartMatrix(phases, null, new LocalDate(2011, 2, 24));
        List<WorkItem> workItems = buildListFirstCase(type);
        for (WorkItem workItem : workItems) {
            if (workItem.getId() == 2) {
                workItem.setExcluded(true);
            }
            matrix.registerWorkItem(workItem);
        }

        verifyDate(phases, matrix, "09/02/2011", new int[] {1, 0, 0, 0});
        verifyDate(phases, matrix, "10/02/2011", new int[] {2, 0, 0, 0});
        verifyDate(phases, matrix, "11/02/2011", new int[] {2, 0, 0, 0});
        verifyDate(phases, matrix, "14/02/2011", new int[] {1, 1, 0, 0});
        verifyDate(phases, matrix, "15/02/2011", new int[] {1, 0, 1, 0});
        verifyDate(phases, matrix, "16/02/2011", new int[] {1, 0, 1, 0});
        verifyDate(phases, matrix, "17/02/2011", new int[] {1, 0, 1, 0});
        verifyDate(phases, matrix, "18/02/2011", new int[] {1, 0, 1, 0});
        verifyDate(phases, matrix, "21/02/2011", new int[] {1, 0, 0, 1});
        verifyDate(phases, matrix, "22/02/2011", new int[] {1, 0, 0, 1});
        verifyDate(phases, matrix, "23/02/2011", new int[] {1, 0, 0, 1});
        verifyDate(phases, matrix, "24/02/2011", new int[] {0, 1, 0, 1});

        List<LocalDate> dates = matrix.getOrderedListOfDates();
        assertThat(dates.size(), is(12));
        assertThat(dates.get(0), is(new LocalDate(2011, 2, 9)));
        assertThat(dates.get(dates.size() - 1), is(new LocalDate(2011, 2, 24)));

        verifyExistenceOfDates(dates, new String[] {
            "09/02/2011",
            "10/02/2011",
            "11/02/2011",
            "14/02/2011",
            "15/02/2011",
            "16/02/2011",
            "17/02/2011",
            "18/02/2011",
            "21/02/2011",
            "22/02/2011",
            "23/02/2011",
            "24/02/2011",
            });

        verifyNonExistenceOfDates(dates, new String[] {
            "12/02/2011",
            "13/02/2011",
            "19/02/2011",
            "20/02/2011",
            });

    }

    @Test
    public void testMatrixSecondCase() throws ParseException {
        
        WorkItemType type = new WorkItemType("phase1", "phase2", "phase3", "phase4", "phase5");
        List<String> phases = type.getPhases();

        CumulativeFlowChartMatrix matrix = new CumulativeFlowChartMatrix(phases, null, new LocalDate(2011, 1, 17));
        List<WorkItem> workItems = buildListSecondCase(type);
        for (WorkItem workItem : workItems) {
            matrix.registerWorkItem(workItem);
        }

        verifyDate(phases, matrix, "05/01/2011", new int[] {1, 0, 0, 0, 0});
        verifyDate(phases, matrix, "06/01/2011", new int[] {1, 1, 0, 0, 0});
        verifyDate(phases, matrix, "07/01/2011", new int[] {1, 1, 0, 0, 0});
        verifyDate(phases, matrix, "10/01/2011", new int[] {0, 2, 1, 0, 0});
        verifyDate(phases, matrix, "11/01/2011", new int[] {0, 1, 2, 0, 0});
        verifyDate(phases, matrix, "12/01/2011", new int[] {0, 0, 3, 0, 0});
        verifyDate(phases, matrix, "13/01/2011", new int[] {0, 0, 3, 0, 0});
        verifyDate(phases, matrix, "14/01/2011", new int[] {0, 0, 3, 0, 0});

        List<LocalDate> dates = matrix.getOrderedListOfDates();

        assertThat(dates.size(), is(9));
        assertThat(dates.get(0), is(new LocalDate(2011, 1, 5)));
        assertThat(dates.get(dates.size() - 1), is(new LocalDate(2011, 1, 17)));

        verifyExistenceOfDates(dates, new String[] {"05/01/2011",
            "06/01/2011",
            "07/01/2011",
            "10/01/2011",
            "11/01/2011",
            "13/01/2011",
            "14/01/2011",
            "17/01/2011"});

        verifyNonExistenceOfDates(dates, new String[] {"02/01/2011",
            "03/01/2011",
            "04/01/2011",
            "08/01/2011",
            "09/01/2011",
            "15/01/2011",
            "16/01/2011"});

    }

    @Test
    public void testMatrixThirdCase() throws ParseException {
        
        WorkItemType type = new WorkItemType("phase1", "phase2", "phase3", "phase4", "phase5");
        List<String> phases = type.getPhases();

        CumulativeFlowChartMatrix matrix = new CumulativeFlowChartMatrix(phases, null, new LocalDate(2011, 4, 9));
        List<WorkItem> workItems = buildListThirdCase(type);
        for (WorkItem workItem : workItems) {
            matrix.registerWorkItem(workItem);
        }

        verifyDate(phases, matrix, "29/03/2011", new int[] {1, 0, 0, 0, 0});
        verifyDate(phases, matrix, "30/03/2011", new int[] {1, 1, 0, 0, 0});
        verifyDate(phases, matrix, "31/03/2011", new int[] {2, 1, 0, 0, 0});
        verifyDate(phases, matrix, "01/04/2011", new int[] {2, 0, 1, 0, 0});
        verifyDate(phases, matrix, "04/04/2011", new int[] {0, 1, 2, 0, 0});
        verifyDate(phases, matrix, "05/04/2011", new int[] {0, 0, 3, 0, 0});
        verifyDate(phases, matrix, "06/04/2011", new int[] {0, 0, 3, 0, 0});
        verifyDate(phases, matrix, "07/04/2011", new int[] {0, 0, 3, 0, 0});
        verifyDate(phases, matrix, "08/04/2011", new int[] {0, 0, 2, 1, 0});

        List<LocalDate> dates = matrix.getOrderedListOfDates();

        assertThat(dates.size(), is(9));
        assertThat(dates.get(0), is(new LocalDate(2011, 3, 29)));
        assertThat(dates.get(dates.size() - 1), is(new LocalDate(2011, 4, 8)));

        verifyExistenceOfDates(dates, new String[] {
            "29/03/2011",
            "30/03/2011",
            "31/03/2011",
            "01/04/2011",
            "04/04/2011",
            "05/04/2011",
            "06/04/2011",
            "07/04/2011",
            "08/04/2011"});

        verifyNonExistenceOfDates(dates, new String[] {"02/04/2011", "03/04/2011"});

    }

    private void verifyExistenceOfDates(List<LocalDate> dates, String[] datesAsString) throws ParseException {
        for (String d : datesAsString) {
            LocalDate date = formatter.parse(d);
            assertThat("date " + date + " should not belong to the given list", dates.contains(date), is(true));
        }
    }

    private void verifyNonExistenceOfDates(List<LocalDate> dates, String[] datesAsString) throws ParseException {
        for (String d : datesAsString) {
            LocalDate date = formatter.parse(d);
            assertThat(dates.contains(date), is(false));
        }
    }

    private Integer getNumberOfWorkItems(List<String> phases, CumulativeFlowChartMatrix matrix, LocalDate localDate,
            String phase) throws ParseException {
        return matrix.getQuantityOfItemsOnDateAndPhase(localDate, phase);
    }

    public static List<WorkItem> buildListFirstCase(WorkItemType type) throws ParseException {
        List<WorkItem> workItems = new ArrayList<WorkItem>();
        WorkItem workItem1 = new WorkItem(1, type);
        WorkItem workItem2 = new WorkItem(2, type);
        WorkItem workItem3 = new WorkItem(3, type);

        workItem1.setDate("phase1", formatter.parse("10/02/2011"));
        workItem1.setDate("phase2", formatter.parse("14/02/2011"));
        workItem1.setDate("phase3", formatter.parse("15/02/2011"));
        workItem1.setDate("phase4", formatter.parse("21/02/2011"));

        workItem2.setDate("phase1", formatter.parse("08/02/2011"));
        workItem2.setDate("phase2", formatter.parse("11/02/2011"));
        workItem2.setDate("phase3", formatter.parse("17/02/2011"));

        workItem3.setDate("phase1", formatter.parse("09/02/2011"));
        workItem3.setDate("phase2", formatter.parse("24/02/2011"));

        workItems.add(workItem1);
        workItems.add(workItem2);
        workItems.add(workItem3);
        return workItems;
    }

    public static List<WorkItem> buildListSecondCase(WorkItemType type) throws ParseException {
        List<WorkItem> workItems = new ArrayList<WorkItem>();

        WorkItem workItem1 = new WorkItem(1, type);
        WorkItem workItem2 = new WorkItem(2, type);
        WorkItem workItem3 = new WorkItem(3, type);

        workItem1.setDate("phase1", formatter.parse("02/01/2011"));
        workItem1.setDate("phase2", formatter.parse("06/01/2011"));
        workItem1.setDate("phase3", formatter.parse("09/01/2011"));

        workItem2.setDate("phase1", formatter.parse("06/01/2011"));
        workItem2.setDate("phase2", formatter.parse("08/01/2011"));
        workItem2.setDate("phase3", formatter.parse("11/01/2011"));

        workItem3.setDate("phase1", formatter.parse("08/01/2011"));
        workItem3.setDate("phase2", formatter.parse("09/01/2011"));
        workItem3.setDate("phase3", formatter.parse("12/01/2011"));
        workItem3.setDate("phase4", formatter.parse("15/01/2011"));

        workItems.add(workItem1);
        workItems.add(workItem2);
        workItems.add(workItem3);
        return workItems;
    }

    public static List<WorkItem> buildListThirdCase(WorkItemType type) throws ParseException {
        List<WorkItem> workItems = new ArrayList<WorkItem>();

        WorkItem workItem1 = new WorkItem(1, type);
        WorkItem workItem2 = new WorkItem(2, type);
        WorkItem workItem3 = new WorkItem(3, type);

        workItem1.setDate("phase1", formatter.parse("29/03/2011"));
        workItem1.setDate("phase2", formatter.parse("30/03/2011"));
        workItem1.setDate("phase3", formatter.parse("01/04/2011"));

        workItem2.setDate("phase1", formatter.parse("30/03/2011"));
        workItem2.setDate("phase2", formatter.parse("02/04/2011"));
        workItem2.setDate("phase3", formatter.parse("04/04/2011"));

        workItem3.setDate("phase1", formatter.parse("31/03/2011"));
        workItem3.setDate("phase2", formatter.parse("02/04/2011"));
        workItem3.setDate("phase3", formatter.parse("05/04/2011"));
        workItem3.setDate("phase4", formatter.parse("08/04/2011"));

        workItems.add(workItem1);
        workItems.add(workItem2);
        workItems.add(workItem3);
        return workItems;
    }

    private void verifyDate(List<String> phases, CumulativeFlowChartMatrix matrix, String dateAsString, int[] pattern)
        throws ParseException {
        double[][] data = matrix.getData();
        List<LocalDate> dates = matrix.getOrderedListOfDates();
        int index = findIndex(dates, dateAsString);

        int phaseIndex = 0;
        for (String phase : phases) {
            assertThat(getNumberOfWorkItems(phases, matrix, stringToLocalDate(dateAsString), phase),
                is(pattern[phaseIndex]));
            assertThat(data[phases.size() - 1 - phaseIndex][index], is(new Double(pattern[phaseIndex])));
            phaseIndex++;
        }
    }

    private LocalDate stringToLocalDate(String dateAsString) throws ParseException {
        return formatter.parse(dateAsString);
    }

    private int findIndex(List<LocalDate> dates, String dateAsString) throws ParseException {
        int index = 0;
        for (LocalDate date : dates) {
            if (date.equals(formatter.parse(dateAsString))) {
                return index;
            }
            index++;
        }
    	System.out.println("\n\n\nCan't parse " + dateAsString);
        fail();
        return -1;
    }

}
