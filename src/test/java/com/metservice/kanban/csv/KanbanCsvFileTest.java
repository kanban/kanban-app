package com.metservice.kanban.csv;

import static com.metservice.kanban.tests.util.TestUtils.emptyWorkItemList;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class KanbanCsvFileTest {

    @Rule
    public TemporaryFolder projectHome = new TemporaryFolder();

    @Test
    public void readsZeroWorkItemsWhenCsvFileDoesNotExist() throws IOException {
        File path = new File(projectHome.getRoot(), "does-not-exist.csv");

        KanbanCsvFile file = new KanbanCsvFile(path, new WorkItemType());
        assertThat(file.read(), emptyWorkItemList());
    }
    
    @Test
    public void writesWorkItems() throws IOException {
        WorkItemType featureType = new WorkItemType();
        WorkItem feature = new WorkItem(1, featureType);        
        List<WorkItem> workItemsToWrite = new ArrayList<WorkItem>();
        workItemsToWrite.add(feature);
        
        // Create file paths

        File fileToWriteTo = new File(projectHome.getRoot(), "write-me.csv");
        File fileToReadFrom = new File(projectHome.getRoot(), "read-me.csv");
        
        // Write file
        
        KanbanCsvFile writer = new KanbanCsvFile(fileToWriteTo, featureType);        
        writer.write(workItemsToWrite);
        
        // Duplicate the file in another location and read it back in
        
        copyFile(fileToWriteTo, fileToReadFrom);
        KanbanCsvFile reader = new KanbanCsvFile(fileToReadFrom, featureType);
        List<WorkItem> duplicateWorkItems = reader.read();
        
        // Verify that the written data is there
        
        assertThat(duplicateWorkItems.get(0), is(feature));
    }
}
