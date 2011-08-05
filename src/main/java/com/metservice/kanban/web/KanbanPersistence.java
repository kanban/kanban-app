package com.metservice.kanban.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.metservice.kanban.csv.KanbanCsvFile;
import com.metservice.kanban.model.DefaultWorkItemTree;
import com.metservice.kanban.model.KanbanProjectConfiguration;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;

//TODO This class needs unit tests.

/**
 * Persistent storage for the CSV files associated with the Kanban application.
 * @author Janella Espinas, Liam O'Connor
 */
public class KanbanPersistence {
    private final Collection<KanbanCsvFile> files = new ArrayList<KanbanCsvFile>();

    /**
     * Default constructor for KanbanPersistence. Reads filenames from a given KanbanProjectConfiguration
     * and populates the list of KanbanCsvFiles.
     * @param configuration - the project conguration
     * @throws IOException
     */
    public KanbanPersistence(KanbanProjectConfiguration configuration) throws IOException {
        for (WorkItemType workItemType : configuration.getWorkItemTypes()) {
            KanbanCsvFile file = new KanbanCsvFile(configuration.getDataFile(workItemType),
                workItemType);
            files.add(file);
        }
    }

    /**
     * Populates the WorkItemTree from CSV files stored in KanbanPersistence.
     * @return the WorkItemTree populated with WorkItems from the CSV files
     * @throws IOException
     */
    public WorkItemTree read() throws IOException {
        WorkItemTree index = new DefaultWorkItemTree();

        for (KanbanCsvFile file : files) {
            List<WorkItem> workItems = file.read();
            for (WorkItem workItem : workItems) {
                index.addWorkItem(workItem);
            }
        }
        return index;
    }
    
    /**
     * Writes changes made on a given WorkItemTree into its corresponding CSV file.
     * @param workItems
     * @throws IOException
     */
    public void write(WorkItemTree workItems) throws IOException {
        for (KanbanCsvFile file : files) {
            List<WorkItem> workItemsForItem = workItems.getWorkItemsOfType(file.getWorkItemType());
            file.write(workItemsForItem);
        }
    }
    
    /**
     * Returns the collection of CSV files stored in the KanbanPersistence.
     * @return the collection of CSV files
     */
    public Collection<KanbanCsvFile> getFiles() {
        return files;
    }
}
