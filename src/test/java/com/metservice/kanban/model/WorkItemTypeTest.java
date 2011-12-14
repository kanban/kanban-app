package com.metservice.kanban.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class WorkItemTypeTest {

    @Test
    public void knowsTheOrderOfPhases() {
        WorkItemType type = new WorkItemType("phase 1", "phase 2");

        assertThat(type.getPhaseAfter("phase 1"), is("phase 2"));
    }

    @Test
    public void knowsTheFinalPhase() {
        WorkItemType type = new WorkItemType("phase 1", "phase 2");

        assertThat(type.hasPhaseAfter("phase 1"), is(true));
        assertThat(type.hasPhaseAfter("phase 2"), is(false));
    }
    
    @Test
    public void thePhaseAfterNullIsTheFirstPhase() {
        WorkItemType type = new WorkItemType("phase");
        
        assertThat(type.hasPhaseAfter(null), is(true));
        assertThat(type.getPhaseAfter(null), is("phase"));
    }
    
    @Test
    public void thereIsNoPhaseAfterNullIfThereAreNoPhases() {
        WorkItemType typeWithNoPhases = new WorkItemType();
        
        assertThat(typeWithNoPhases.hasPhaseAfter(null), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfAskedForThePhaseAfterTheLastOne() {
        WorkItemType type = new WorkItemType("phase");
        type.getPhaseAfter("phase");
    }

    @Test
    public void hasAMutableNameProperty() {
        WorkItemType workItemType = new WorkItemType();
        workItemType.setName("name");

        assertThat(workItemType.getName(), is("name"));
    }
    
    @Test
    public void knowsTheBacklogPhase() {
        WorkItemType type = new WorkItemType("phase 1","phase 2");
        assertThat(type.getBacklogPhase(), is("phase 1"));
    }

    @Test
    public void knowsTheCompletedPhase() {
        WorkItemType type = new WorkItemType("phase 1","phase 2");
        assertThat(type.getCompletedPhase(), is("phase 2"));
    }
    
    @Test
    public void testIsPhaseBefore() {
        WorkItemType type = new WorkItemType("phase 1", "phase 2", "phase 3");
        assertTrue(type.isPhaseBefore("phase 1", "phase 2"));
        assertTrue(type.isPhaseBefore("phase 1", "phase 3"));
        assertTrue(type.isPhaseBefore("phase 2", "phase 3"));

        assertFalse(type.isPhaseBefore("phase 2", "phase 2"));
        assertFalse(type.isPhaseBefore("phase 3", "phase 2"));
    }

    @Test
    public void testIsPhaseAfter() {
        WorkItemType type = new WorkItemType("phase 1", "phase 2", "phase 3");
        assertTrue(type.isPhaseAfter("phase 3", "phase 1"));
        assertTrue(type.isPhaseAfter("phase 3", "phase 2"));
        assertTrue(type.isPhaseAfter("phase 2", "phase 1"));

        assertFalse(type.isPhaseAfter("phase 2", "phase 2"));
        assertFalse(type.isPhaseAfter("phase 2", "phase 3"));
    }
}
