package com.metservice.kanban.csv;

import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static java.lang.Integer.parseInt;
import org.joda.time.LocalDate;

public class CsvRow {

    public static final int INTEGER_DEFAULT = 0;
    private static final boolean BOOLEAN_DEFAULT = false;
    private static final String STRING_DEFAULT = "";
    private final CsvColumnNames columnNames;
    private final String[] cells;

    public CsvRow(CsvColumnNames columnNames, String[] cells) {
        this.columnNames = columnNames;
        this.cells = cells;
    }

    public LocalDate getDate(String columnName) {
        if (!columnNames.hasColumn(columnName)) {
            return null;
        }
        String stringValue = getString(columnName);
        return stringValue == null ? null : parseIsoDate(stringValue);
    }

    public int getInt(String columnName) {
        if (!columnNames.hasColumn(columnName)) {
            return INTEGER_DEFAULT;
        }
        return parseInt(getString(columnName));
    }

    public String getString(String columnName) {
        if (!columnNames.hasColumn(columnName)) {
            return STRING_DEFAULT;
        } 
        int index = columnNames.getColumnIndex(columnName);
        String unparsedString = cells[index];
        return unparsedString.isEmpty() ? null : unparsedString;
    }

    public void setString(String columnName, String string) {
        int index = columnNames.getColumnIndex(columnName);
        cells[index] = string;
    }

    public Boolean getBoolean(String columnName) {
        if (!columnNames.hasColumn(columnName)) {
            return BOOLEAN_DEFAULT;
        } 
        return Boolean.parseBoolean(getString(columnName));
    }
}
