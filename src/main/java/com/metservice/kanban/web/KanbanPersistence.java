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

public class KanbanPersistence {

    private final Collection<KanbanCsvFile> files = new ArrayList<KanbanCsvFile>();

    public KanbanPersistence(KanbanProjectConfiguration configuration) throws IOException {
        for (WorkItemType workItemType : configuration.getWorkItemTypes()) {
            KanbanCsvFile file = new KanbanCsvFile(configuration.getDataFile(workItemType),
                workItemType);
            files.add(file);
        }
    }

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

    public void write(WorkItemTree workItems) throws IOException {
        for (KanbanCsvFile file : files) {
            List<WorkItem> workItemsForItem = workItems.getWorkItemsOfType(file.getWorkItemType());
            file.write(workItemsForItem);
        }
    }
    
    public Collection<KanbanCsvFile> getFiles() {
        return files;
    }
}
