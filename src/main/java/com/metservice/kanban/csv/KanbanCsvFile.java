package com.metservice.kanban.csv;

import static org.apache.commons.io.FileUtils.copyFile;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

//TODO This class needs more unit tests.

public class KanbanCsvFile {

    private static final int MAX_TEMPORARY_FILES = 25;

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

        cleanUpTemproaryFiles();
    }

    /**
     * Keep the newest {@link #MAX_TEMPORARY_FILES} (
     * {@value #MAX_TEMPORARY_FILES}) temporary files and delete the rest.
     */
    public void cleanUpTemproaryFiles() {

        File directory = file.getParentFile();
        List<String> files = Arrays.asList(directory.list());
        Collections.sort(files);
        Collections.reverse(files);

        Pattern tempFilePattern = Pattern.compile(file.getName() + "\\.\\d+\\.temp");

        int tempFilesFound = 0;

        File fileToClean;
        for (String file : files) {

            fileToClean = new File(directory.getAbsolutePath() + File.separatorChar + file);

            if (tempFilePattern.matcher(file).matches()) {
                tempFilesFound++;
                if (tempFilesFound > MAX_TEMPORARY_FILES) {
                    fileToClean.delete();
                }
            }
        }

    }

    @Override
    public String toString() {
        return file.toString();
    }
}
