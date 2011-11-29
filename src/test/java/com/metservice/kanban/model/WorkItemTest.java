package com.metservice.kanban.model;

import static com.metservice.kanban.model.WorkItem.ROOT_WORK_ITEM_ID;
import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.utils.WorkingDayUtils;

public class WorkItemTest {

    private static LocalDateFormatter formatter = new LocalDateFormatter();
    private static class LocalDateFormatter {
        public LocalDate parse(String pattern) throws ParseException {
            return LocalDate.fromDateFields(new SimpleDateFormat("dd/MM/yyyy").parse(pattern));
        }
    }
    private WorkItemType type;
    
    @Before
    public void setup() {
        type = new WorkItemType("feature", "phase1", "phase2", "phase3"); 
    }

    @Test
    public void testPhaseOnDateNull() throws ParseException {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItem workItem = new WorkItem(1, type); 
        assertThat(workItem.getPhaseOnDate(formatter.parse("14/02/2011")), nullValue());

    }
    
    @Test
    public void testTruncatedName() throws ParseException {
        WorkItem workItem = new WorkItem(1, type);
        //                1234567890123456879012345687901234567890
        assertThat(workItem.getTruncatedName(), is(workItem.getName()));
        workItem.setName("Very long name, Very long name, Very long name, Very long name, Very long name, Very long name, Very long name....");
        assertThat(workItem.getTruncatedName(), is("Very long name, Very long name, Very lon"));
        workItem.setName("Short name");
        assertThat(workItem.getTruncatedName(), is("Short name"));

    }
    

    @Test
    public void testPhaseOnDate() throws ParseException {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItem workItem = new WorkItem(1, type);
        workItem.setDate("phase1", formatter.parse("14/02/2011"));
        workItem.setDate("phase2", formatter.parse("18/02/2011"));

        assertThat(workItem.getPhaseOnDate(formatter.parse("14/02/2011")), is("phase1"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("15/02/2011")), is("phase1"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("16/02/2011")), is("phase1"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("17/02/2011")), is("phase1"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("18/02/2011")), is("phase2"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("19/02/2011")), is("phase2"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("20/02/2011")), is("phase2"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("20/02/2012")), is("phase2"));

        workItem.setDate("phase3", formatter.parse("19/02/2011"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("18/02/2011")), is("phase2"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("19/02/2011")), is("phase3"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("20/02/2011")), is("phase3"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("20/02/2012")), is("phase3"));

    }

    @Test
    public void testIsInFinalPhase() throws ParseException {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItem workItem = new WorkItem(1, type); 
        assertThat(workItem.isCompleted(), is(false));
        workItem.setDate("phase1", formatter.parse("14/02/2011"));
        assertThat(workItem.isCompleted(), is(false));
        workItem.setDate("phase2", formatter.parse("18/02/2011"));
        assertThat(workItem.isCompleted(), is(false));
        workItem.setDate("phase3", formatter.parse("20/02/2011"));
        assertThat(workItem.isCompleted(), is(true));

    }

    @Test
    public void testAdvance() throws ParseException {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItem workItem = new WorkItem(1, type); 
        workItem.setDate("phase1", formatter.parse("10/02/2011"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("10/02/2011")), is("phase1"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("11/02/2011")), is("phase1"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("12/02/2011")), is("phase1"));
        workItem.advance(formatter.parse("11/02/2011"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("10/02/2011")), is("phase1"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("11/02/2011")), is("phase2"));
        assertThat(workItem.getPhaseOnDate(formatter.parse("12/02/2011")), is("phase2"));

    }
    
    @Test
    public void testStop() throws ParseException {
    	WorkItem workItem = new WorkItem(1, type); 
        workItem.setDate("phase1", formatter.parse("10/02/2011"));
        assertFalse(workItem.isBlocked());
        workItem.stop();
        assertTrue(workItem.isBlocked());        
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidAdvance() throws ParseException {
        // TODO Use TestKanbanBoardBuilder to construct test data instead

        WorkItem workItem = new WorkItem(1, type); 
        workItem.setDate("phase1", formatter.parse("10/02/2011"));
        workItem.setDate("phase2", formatter.parse("12/02/2011"));
        workItem.setDate("phase3", formatter.parse("13/02/2011"));
        workItem.advance(formatter.parse("11/02/2011"));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotGetCurrentPhaseIfAdvanceWasNeverCalled() {
        // Note: this behaviour is great for error detection in tests, if this behaviour becomes inconvenient, consider
        // adding a hasCurrentPhase() method

        WorkItemType featureType = new WorkItemType("phase");
        WorkItem feature = new WorkItem(1, featureType);
        feature.getCurrentPhase();
    }

    @Test
    public void canChangeParentByCreatingNewInstance() {
        WorkItemType featureType = new WorkItemType("phase");
        WorkItem workItem = new WorkItem(1, featureType, "phase");
        WorkItemTree tree = new DefaultWorkItemTree();
        tree.addWorkItem(workItem);
        int newParentId = tree.getNewId();

        WorkItem reparentedWorkItem = workItem.withNewParent(newParentId);

        assertThat(reparentedWorkItem.getId(), is(workItem.getId()));
        assertThat(reparentedWorkItem.getParentId(), is(newParentId));
        assertThat(reparentedWorkItem.getType(), is(workItem.getType()));
        assertThat(reparentedWorkItem.getName(), is(workItem.getName()));
        assertThat(reparentedWorkItem.getAverageCaseEstimate(), is(workItem.getAverageCaseEstimate()));
        assertThat(reparentedWorkItem.getImportance(), is(workItem.getImportance()));
        assertThat(reparentedWorkItem.getNotes(), is(workItem.getNotes()));
        assertThat(reparentedWorkItem.getDate("phase"), is(workItem.getDate("phase")));
        assertThat(reparentedWorkItem.getCurrentPhase(), is("phase"));
    }

    @Test
    public void canConstructAlreadyAdvancedToAGivenPhase() {
        WorkItemType type = new WorkItemType("phase 1", "phase 2");

        WorkItem workItem = new WorkItem(2, 1, type, "phase 2");

        assertThat(workItem.getId(), is(2));
        assertThat(workItem.getParentId(), is(1));
        assertThat(workItem.getType(), is(type));
        assertThat(workItem.getCurrentPhase(), is("phase 2"));
    }
    
    @Test
    public void canConstructWithoutSpecifyingAParentId() {
        WorkItemType type = new WorkItemType();
        WorkItem workItem = new WorkItem(6, type);
        
        assertThat(workItem.getId(), is(6));
        assertThat(workItem.getParentId(), is(ROOT_WORK_ITEM_ID));
        assertThat(workItem.getType(), is(type));
    }
    
    @Test
    public void canConstructWithoutSpecifyingAParentIdAndAlreadyAdvancedToAGivenPhase() {
        WorkItemType type = new WorkItemType("phase 1", "phase 2");
        WorkItem workItem = new WorkItem(11, type, "phase 2");
        
        assertThat(workItem.getId(), is(11));
        assertThat(workItem.getParentId(), is(ROOT_WORK_ITEM_ID));
        assertThat(workItem.getType(), is(type));
        assertThat(workItem.getCurrentPhase(), is("phase 2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotPassANonexistentPhaseToConstructor() {
        WorkItemType type = new WorkItemType("phase");

        new WorkItem(1, type, "nonexistent phase");
    }
    
    @Test
    public void datesCanBeSetUsingStrings() throws ParseException {
        WorkItem workItem = new WorkItem(1, new WorkItemType("phase"));
        workItem.setDateAsString("phase", "2011-05-31");
        
        assertThat(workItem.getDate("phase"), is(parseIsoDate("2011-05-31")));
    }
    
    @Test
    public void getPhaseDurationsHasCorrectDurationsPartial() throws ParseException{
        WorkItem workItem = new WorkItem(1, type);
        workItem.setDate("phase1", formatter.parse("10/07/2011"));
        workItem.setDate("phase2", formatter.parse("18/07/2011"));

        Map<String, Integer> phaseDurations = workItem.getPhaseDurations();
        
        assertThat(phaseDurations.keySet().size(), is(4));
        assertThat(phaseDurations.get("phase1"), is(5));
        LocalDate today = new LocalDate();
        int days = WorkingDayUtils.
                getWorkingDaysBetween(formatter.parse("18/07/2011"), today);
        assertThat(phaseDurations.get("phase2"), is(days));
        assertThat(phaseDurations.get("phase3"), is(0));
    }
    
    @Test
    public void getPhaseDurationsHasCorrectDurationsComplete() throws ParseException{
        WorkItem workItem = new WorkItem(1, type);
        workItem.setDate("phase1", formatter.parse("10/07/2011"));
        workItem.setDate("phase2", formatter.parse("18/07/2011"));
        workItem.setDate("phase3", formatter.parse("25/07/2011"));

        Map<String, Integer> phaseDurations = workItem.getPhaseDurations();
        
        assertThat(phaseDurations.keySet().size(), is(4));
        assertThat(phaseDurations.get("phase1"), is(5));
        assertThat(phaseDurations.get("phase2"), is(5));
        
        LocalDate today = new LocalDate();
        int days = WorkingDayUtils.
                getWorkingDaysBetween(formatter.parse("25/07/2011"), today);
        assertThat(phaseDurations.get("phase3"), is(days));
    }

    @Test
    public void setWorkStreamAsStringTrims() {
        WorkItem workItem = new WorkItem(1, type);

        workItem.setWorkStreamsAsString("a  ,  b,  c   ");

        assertThat(workItem.getWorkStreamsAsString(), is("a,b,c"));
        assertThat(workItem.getWorkStreams().size(), is(3));

        assertThat(workItem.getWorkStreams().get(0), is("a"));
        assertThat(workItem.getWorkStreams().get(1), is("b"));
        assertThat(workItem.getWorkStreams().get(2), is("c"));
    }

    @Test
    public void setWorkStreamAsStringEmptyOrNull() {
        WorkItem workItem = new WorkItem(1, type);

        workItem.setWorkStreamsAsString("");
        assertThat(workItem.getWorkStreamsAsString(), is(""));
        assertThat(workItem.getWorkStreams().size(), is(0));

        workItem.setWorkStreamsAsString(null);
        assertThat(workItem.getWorkStreamsAsString(), is(""));
        assertThat(workItem.getWorkStreams().size(), is(0));
    }
}
