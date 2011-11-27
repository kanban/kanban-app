package com.metservice.kanban.csv;

import static com.metservice.kanban.csv.CsvConstants.AVERAGE_CASE_ESIMATE;
import static com.metservice.kanban.csv.CsvConstants.COLOR_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.EXCLUDED_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.ID_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.IMPORTANCE_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.MUST_HAVE;
import static com.metservice.kanban.csv.CsvConstants.NAME_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.NOTES_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.PARENT_ID_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.STOPPED_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.WORK_STREAMS;
import static com.metservice.kanban.csv.CsvConstants.WORST_CASE_ESIMATE;
import static com.metservice.kanban.utils.DateUtils.formatIsoDate;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import au.com.bytecode.opencsv.CSVWriter;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class KanbanCsvWriter {

    private static final int NUMBER_OF_METADATA_COLUMNS = 12;

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
        data[3] = AVERAGE_CASE_ESIMATE;
        data[4] = WORST_CASE_ESIMATE;
        data[5] = IMPORTANCE_COLUMN_NAME;
        data[6] = NOTES_COLUMN_NAME;
        data[7] = EXCLUDED_COLUMN_NAME;
        data[8] = STOPPED_COLUMN_NAME;
        data[9] = COLOR_COLUMN_NAME;
        data[10] = MUST_HAVE;
        data[11] = WORK_STREAMS;

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
        data[3] = Integer.toString(workItem.getAverageCaseEstimate());
        data[4] = Integer.toString(workItem.getWorstCaseEstimate());
        data[5] = Integer.toString(workItem.getImportance());
        data[6] = workItem.getNotes();
        data[7] = Boolean.toString(workItem.isExcluded());
        data[8] = Boolean.toString(workItem.isStopped());
        data[9] = workItem.getColour().toString().substring(1);
        data[10] = Boolean.toString(workItem.isMustHave());
        data[11] = workItem.getWorkStreamsAsString();

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
