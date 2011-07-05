package com.metservice.kanban.csv;

import static com.metservice.kanban.utils.DateUtils.formatIsoDate;
import static com.metservice.kanban.utils.DateUtils.parseIsoDate;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import org.joda.time.LocalDate;
import org.junit.Test;

public class CsvRowTest {

    @Test
    public void canParseCellAsDate() {
        LocalDate arbitraryDate = parseIsoDate("2011-06-02");

        CsvRow row = createSingleCellRow("column", formatIsoDate(arbitraryDate));

        assertThat(row.getDate("column"), is(arbitraryDate));
    }

    @Test
    public void parsesEmptyStringAsNullDate() {
        CsvRow row = createSingleCellRow("column", "");
        assertThat(row.getDate("column"), nullValue());
    }

    @Test
    public void canParseCellAsInt() {
        CsvRow row = createSingleCellRow("column", "42");
        assertThat(row.getInt("column"), is(42));
    }

    @Test
    public void canParseCellAsBoolean() {
        CsvRow row = createSingleCellRow("column", "true");
        assertThat(row.getBoolean("column"), is(true));
    }

    @Test
    public void canReturnCellsAsTheirNaturalStringValue() {
        CsvRow row = createSingleCellRow("column", "arbitrary string");
        assertThat(row.getString("column"), is("arbitrary string"));
    }

    private static final CsvRow createSingleCellRow(String columnName, String value) {
        String[] cell = new String[] {value};
        CsvColumnNames columnNames = new CsvColumnNames(new String[] {columnName});
        return new CsvRow(columnNames, cell);
    }

    @Test
    public void returnsJustTheCellInTheNamedColumn() {
        CsvColumnNames columnNames = new CsvColumnNames(new String[] {"A", "B", "C"});
        String[] cells = new String[] {"alpha", "bravo", "charlie"};
        CsvRow row = new CsvRow(columnNames, cells);

        assertThat(row.getString("A"), is("alpha"));
        assertThat(row.getString("C"), is("charlie"));
    }

    @Test
    public void dealWithBooleanTypesAsWell() {
        CsvColumnNames columnNames = new CsvColumnNames(new String[] {"A", "B"});
        String[] cells = new String[] {"true", "false"};
        CsvRow row = new CsvRow(columnNames, cells);

        assertThat(row.getBoolean("A"), is(true));
        assertThat(row.getBoolean("B"), is(false));
    }

    @Test
    public void correctDefaultValueShouldReturnWhenColumnIsNotAvailable() {
        CsvColumnNames columnNames = new CsvColumnNames(new String[] {"column"});
        String[] cells = new String[] {"garbage"};
        CsvRow row = new CsvRow(columnNames, cells);

        assertThat(row.getBoolean("nonexistent"), is(false));
        assertThat(row.getString("nonexistent"), is(""));
        assertThat(row.getDate("nonexistent"), nullValue());
        assertThat(row.getInt("nonexistent"), is(0));
    }

}
