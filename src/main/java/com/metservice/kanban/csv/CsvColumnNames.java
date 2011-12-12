package com.metservice.kanban.csv;

import java.util.Arrays;

public class CsvColumnNames {

    private final String[] columnNames;

    public CsvColumnNames(String... columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public String toString() {
        return Arrays.toString(columnNames);
    }

    public int getColumnIndex(String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnName.equals(columnNames[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("column name \"" + columnName + "\" not found");
    }

    public boolean hasColumn(String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnName.equals(columnNames[i])) {
                return true;
            }
        }
        return false;
    }
    
    public int size() {
        return columnNames.length;
    }
}
