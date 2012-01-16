package com.metservice.kanban;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.model.WorkItemType;

public class KanbanPropertiesFileTest {

    private KanbanPropertiesFile propertiesFile;

    @Before
    public void setUp() throws IOException, URISyntaxException {

        propertiesFile = new KanbanPropertiesFile(new InputStreamReader(getClass().getResourceAsStream(
            "test.properties")));
    }

    @Test
    public void setWipLimits() throws IOException {
        WorkItemType type = new WorkItemType("feature backlog", "feature wall", "feature completed");
        type.setName("feature");

        assertEquals("100,2,100", propertiesFile.getString("workItemTypes.feature.wipLimit"));

        propertiesFile.setColumnWipLimit(type, "feature wall", 5);
        assertEquals("100,5,100", propertiesFile.getString("workItemTypes.feature.wipLimit"));

        propertiesFile.setColumnWipLimit(type, "feature backlog", 1);
        assertEquals("1,5,100", propertiesFile.getString("workItemTypes.feature.wipLimit"));

        propertiesFile.setColumnWipLimit(type, "feature completed", null);
        assertEquals("1,5,", propertiesFile.getString("workItemTypes.feature.wipLimit"));

        propertiesFile.setColumnWipLimit(type, "feature backlog", null);
        assertEquals(",5,", propertiesFile.getString("workItemTypes.feature.wipLimit"));
    }
}
