package com.metservice.kanban.csv;

import static org.apache.commons.io.FileUtils.copyFile;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

//TODO This class needs more unit tests.

public class KanbanCsvFile {

    private final File file;

    private final WorkItemType workItemType;

    public KanbanCsvFile(File file, WorkItemType workItemType) {
        this.file = file;

        this.workItemType = workItemType;
    }

    public WorkItemType getWorkItemType() {
        return workItemType;
    }

    public List<WorkItem> read() throws IOException {
        if (!file.exists()) {
            return new ArrayList<WorkItem>();
        } else {
            return read(new FileReader(file));
        }
    }

    private List<WorkItem> read(Reader reader) throws IOException {
        KanbanCsvReader csvReader = new KanbanCsvReader(reader, workItemType);
        try {
            List<WorkItem> workItems = csvReader.read();
            csvReader.close();
            return workItems;
        } catch (IOException e) {
            throw new IOException("failure reading " + file, e);
        } catch (RuntimeException e) {
            throw new RuntimeException("failure reading " + file, e);
        } finally {
            csvReader.closeQuietly();
        }
    }

    public void write(List<WorkItem> workItems) throws IOException {
        File temporaryFile = new File(file.getAbsolutePath() + "." + System.currentTimeMillis() + ".temp");

        KanbanCsvWriter workItemWriter = new KanbanCsvWriter(new FileWriter(temporaryFile), workItemType);
        try {
            workItemWriter.write(workItems);
            workItemWriter.close();
        } catch (IOException e) {
            throw new IOException("failure writing " + file, e);
        } catch (RuntimeException e) {
            throw new RuntimeException("failure writing " + file, e);
        } finally {
            workItemWriter.closeQuietly();
        }

        file.delete();
        copyFile(temporaryFile, file);
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
