package com.metservice.kanban.model;

import java.io.File;
import java.io.IOException;
import com.metservice.kanban.KanbanPropertiesFile;

//TODO This class needs unit tests.

public class KanbanProjectConfiguration {
    private final File projectHome;
    private final KanbanBoardConfiguration phaseSequences;
    private final TreeNode<WorkItemType> rootWorkItemType;
    private final WorkItemTypeCollection workItemTypes;

    public KanbanProjectConfiguration(File projectHome, KanbanBoardConfiguration phaseSequences,
            TreeNode<WorkItemType> rootWorkItemType, WorkItemTypeCollection workItemTypes) throws IOException {
        this.projectHome = projectHome;
        this.phaseSequences = phaseSequences;
        this.rootWorkItemType = rootWorkItemType;
        this.workItemTypes = workItemTypes;
    }

    public TreeNode<WorkItemType> getRootWorkItemType() {
        return rootWorkItemType;
    }

    public WorkItemTypeCollection getWorkItemTypes() {
        return workItemTypes;
    }

    public File getDataFile(WorkItemType workItemType) {
        return new File(projectHome, workItemType.getName() + ".csv");
    }
    
    public File getJournalFile() throws IOException {
    	File journalFile = new File(projectHome, "journal.txt");
    	journalFile.createNewFile();
    	return journalFile;
    }

    public KanbanBoardConfiguration getPhaseSequences() {
        return phaseSequences;
    }

    public KanbanPropertiesFile getKanbanPropertiesFile() throws IOException {
        return new KanbanPropertiesFile(new File(projectHome, "kanban.properties"));
    }
}
