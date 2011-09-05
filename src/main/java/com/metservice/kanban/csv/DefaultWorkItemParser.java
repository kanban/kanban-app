package com.metservice.kanban.csv;

import static com.metservice.kanban.csv.CsvConstants.EXCLUDED_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.ID_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.IMPORTANCE_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.NAME_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.NOTES_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.PARENT_ID_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.SIZE_COLUMN_NAME;
import static com.metservice.kanban.csv.CsvConstants.COLOR_COLUMN_NAME;
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
        String color = dataRow.getString(COLOR_COLUMN_NAME);

        WorkItem workItem = new WorkItem(id, parentId, type);
        workItem.setName(name);
        workItem.setSize(size);
        workItem.setImportance(importance);
        workItem.setNotes(notes);
        workItem.setExcluded(excluded);
        workItem.setColour(color);
        

        for (String phase : type.getPhases()) {
            LocalDate date = dataRow.getDate(phase);
            workItem.setDate(phase, date);
        }
        return workItem;
    }
}
