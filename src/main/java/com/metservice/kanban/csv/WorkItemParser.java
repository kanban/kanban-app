package com.metservice.kanban.csv;

import com.metservice.kanban.model.WorkItem;


public interface WorkItemParser {

    WorkItem parseWorkItem(CsvColumnNames columnNames, String[] row);

}
