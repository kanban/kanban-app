package com.metservice.kanban.csv;

import static com.metservice.kanban.csv.CsvConstants.*;
import static com.metservice.kanban.utils.DateUtils.formatIsoDate;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import au.com.bytecode.opencsv.CSVWriter;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class KanbanCsvWriter {

    private static final int NUMBER_OF_METADATA_COLUMNS = 9;

    private final CSVWriter csvWriter;
    private final List<String> phases;
    
    public KanbanCsvWriter(Writer writer, WorkItemType workItemType) {
        this.csvWriter = new CSVWriter(writer);
        this.phases = workItemType.getPhases();
    }

    public void write(List<WorkItem> workItems) throws IOException {
        writeColumnHeadings();
        for (WorkItem workItem : workItems) {
            writeWorkItem(workItem);
        }
    }

    private void writeColumnHeadings() {
        String[] data = new String[phases.size() + NUMBER_OF_METADATA_COLUMNS];
        data[0] = ID_COLUMN_NAME;
        data[1] = PARENT_ID_COLUMN_NAME;
        data[2] = NAME_COLUMN_NAME;
        data[3] = SIZE_COLUMN_NAME;
        data[4] = IMPORTANCE_COLUMN_NAME;
        data[5] = NOTES_COLUMN_NAME;
        data[6] = EXCLUDED_COLUMN_NAME;
        data[7] = STOPPED_COLUMN_NAME;
        data[8] = COLOR_COLUMN_NAME;

        int arrayIndex = NUMBER_OF_METADATA_COLUMNS;
        for (String phase : phases) {
            data[arrayIndex] = phase;
            arrayIndex++;
        }

        csvWriter.writeNext(data);
    }

    private void writeWorkItem(WorkItem workItem) {
        String[] data = new String[phases.size() + NUMBER_OF_METADATA_COLUMNS];
        data[0] = Integer.toString(workItem.getId());
        data[1] = Integer.toString(workItem.getParentId());
        data[2] = workItem.getName();
        data[3] = Integer.toString(workItem.getSize());
        data[4] = Integer.toString(workItem.getImportance());
        data[5] = workItem.getNotes();
        data[6] = "" + workItem.isExcluded();
        data[7] = "" + workItem.isStopped();
        data[8] = workItem.getColour().toString().substring(1);

        int arrayIndex = NUMBER_OF_METADATA_COLUMNS;
        for (String phase : phases) {
            if (workItem.hasDate(phase)) {
                data[arrayIndex] = formatIsoDate(workItem.getDate(phase));
            }
            arrayIndex++;
        }

        csvWriter.writeNext(data);
    }

    public void close() throws IOException {
        csvWriter.close();
    }

    public void closeQuietly() {
        try {
            csvWriter.close();
        } catch (Exception e) {
        }
    }
}
