package com.metservice.kanban.csv;

import static com.metservice.kanban.csv.CsvConstants.BEST_CASE_ESIMATE;
import static com.metservice.kanban.csv.CsvConstants.COLOR_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.EXCLUDED_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.ID_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.IMPORTANCE_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.MUST_HAVE;
import static com.metservice.kanban.csv.CsvConstants.NAME_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.NOTES_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.PARENT_ID_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.SIZE_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.STOPPED_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.WORK_STREAMS;
import static com.metservice.kanban.csv.CsvConstants.WORST_CASE_ESIMATE;
import org.joda.time.LocalDate;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class DefaultWorkItemParser implements WorkItemParser {

    private final WorkItemType type;

    public DefaultWorkItemParser(WorkItemType type) {
        this.type = type;
    }

    @Override
    public WorkItem parseWorkItem(CsvColumnNames columnNames, String[] row) {
        CsvRow dataRow = new CsvRow(columnNames, row);

        int id = dataRow.getInt(ID_COLUMN_NAME);
        int parentId = dataRow.getInt(PARENT_ID_COLUMN_NAME);
        String name = dataRow.getString(NAME_COLUMN_NAME);
        int size = dataRow.getInt(SIZE_COLUMN_NAME);
        int importance = dataRow.getInt(IMPORTANCE_COLUMN_NAME);
        String notes = dataRow.getString(NOTES_COLUMN_NAME);
        boolean excluded = dataRow.getBoolean(EXCLUDED_COLUMN_NAME);
        boolean stopped = dataRow.getBoolean(STOPPED_COLUMN_NAME);
        String color = dataRow.getString(COLOR_COLUMN_NAME);
        int bestCase = dataRow.getInt(BEST_CASE_ESIMATE);
        int worstCase = dataRow.getInt(WORST_CASE_ESIMATE);
        boolean mustHave = dataRow.getBoolean(MUST_HAVE);
        String workStreams = dataRow.getString(WORK_STREAMS);

        WorkItem workItem = new WorkItem(id, parentId, type);
        workItem.setName(name);
        workItem.setSize(size);
        workItem.setImportance(importance);
        workItem.setNotes(notes);
        workItem.setExcluded(excluded);
        color = ( (color == null || color == "") ? "ffffff" : color);
        
        workItem.setColour(color);
        workItem.setStopped(stopped);
        workItem.setAverageCaseEstimate(bestCase);
        workItem.setWorstCaseEstimate(worstCase);
        workItem.setMustHave(mustHave);
        workItem.setWorkStreamsAsString(workStreams);

        for (String phase : type.getPhases()) {
            LocalDate date = dataRow.getDate(phase);
            workItem.setDate(phase, date);
        }
        return workItem;
    }
}
