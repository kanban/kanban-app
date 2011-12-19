package com.metservice.kanban;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import com.google.common.base.Preconditions;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemComment;
import com.metservice.kanban.model.WorkItemTree;

// TODO This class needs unit tests.
public class KanbanCommentsFile {
    private File file;

    private Map<Integer, List<WorkItemComment>> completeCommentsList;

    public KanbanCommentsFile(File file) throws IOException {
        this.file = file;

        if (this.file.createNewFile()) {
            performWrite(new HashMap<Integer, List<WorkItemComment>>());
        }
    }

    public void writeAllComments(WorkItemTree workItems) throws IOException {
        completeCommentsList = new HashMap<Integer, List<WorkItemComment>>();
        for (WorkItem workItem : workItems.getWorkItemList()) {
            completeCommentsList.put(workItem.getId(), workItem.getComments());
        }

        performWrite(completeCommentsList);
    }

    private void performWrite(Map<Integer, List<WorkItemComment>> mapToWrite) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        try {
            oos.writeObject(mapToWrite);
        } finally {
            IOUtils.closeQuietly(oos);
        }
    }

    @SuppressWarnings("unchecked")
    public void readAllComments() throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        try {
            completeCommentsList = (Map<Integer, List<WorkItemComment>>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        } finally {
            IOUtils.closeQuietly(ois);
        }
    }

    public List<WorkItemComment> getCommentsFor(int id) {
        Preconditions.checkState(completeCommentsList != null,
                "Can not get comments before reading them for the first time.  Call readAllComments() first.");
        return completeCommentsList.get(id);
    }

}
