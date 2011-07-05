package com.metservice.kanban.charts.burnup;

import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class BurnUpDataModelTest {
    
    private static final LocalDate PAST = parseIsoDate("2011-01-01");
    private static final LocalDate PRESENT = parseIsoDate("2011-01-02");
    private static final LocalDate FUTURE = parseIsoDate("2011-01-03");    
    
    private BurnUpDataModel model;
    
    private WorkItem size1;
    private WorkItem size2;
    private WorkItem size4;
    private WorkItem size8;

    @Before
    public void before() {
        WorkItemType type = new WorkItemType("backlog", "in progress 1", "in progress 2", "completed");
        
        size1 = new WorkItem(1, type);        
        size1.setSize(1);

        size2 = new WorkItem(2, type);
        size2.setSize(2);

        size4 = new WorkItem(3, type);
        size4.setSize(4);
        
        size8 = new WorkItem(4, type);
        size8.setSize(8);
        
        model = new BurnUpDataModel(type, asList(size1, size2, size4, size8), PRESENT);        
    }
    
    @Test
    public void backlogSumOnlyIncludesBacklogItems() {
        advance(size1, "in progress 1", PAST);
        
        advance(size2, "backlog", PAST);
        advance(size2, "in progress 1", FUTURE);
        
        advance(size4, "backlog", PAST);
        
        assertThat(model.getBacklogSizeOnDate(PRESENT), is(6));        
    }
    
    @Test
    public void inProgressOnlyIncludesInProgressItems() {
        advance(size1, "completed", PAST);
        
        advance(size2, "in progress 1", PAST);
        advance(size2, "completed", FUTURE);
        
        advance(size4, "in progress 2", PAST);
        
        assertThat(model.getInProgressSizeOnDate(PRESENT), is(6));                
    }
    
    @Test
    public void completedOnlyIncludesCompletedItems() {
        advance(size1, "completed", PAST);
        advance(size2, "completed", PAST);
        advance(size4, "in progress 2", PAST);
        
        assertThat(model.getCompletedSizeOnDate(PRESENT), is(3));                
    }
    
    @Test
    public void phaseChangesMadeOnTheQueryDateAreCounted() {
        advance(size1, "backlog", PRESENT);
        advance(size2, "in progress 1", PRESENT);
        advance(size4, "completed", PRESENT);
        
        assertThat(model.getBacklogSizeOnDate(PRESENT), is(1));                
        assertThat(model.getInProgressSizeOnDate(PRESENT), is(2));                
        assertThat(model.getCompletedSizeOnDate(PRESENT), is(4));                
    }
    
    
    private void advance(WorkItem workItem, String phase, LocalDate date) {
        do {
            workItem.advance(date);
        } while (!workItem.getCurrentPhase().equals(phase));
    }
}