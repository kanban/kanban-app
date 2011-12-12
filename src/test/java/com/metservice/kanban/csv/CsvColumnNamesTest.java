package com.metservice.kanban.csv;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class CsvColumnNamesTest {
    @Test
    public void knowsColumnIndices() {
        CsvColumnNames columnNames = new CsvColumnNames(new String[] {"A", "B", "C", "D"});
        
        assertThat(columnNames.getColumnIndex("A"), is(0));
        assertThat(columnNames.getColumnIndex("D"), is(3));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfColumnNameDoesNotExist() {
        CsvColumnNames columnNames = new CsvColumnNames(new String[] {"column name"});
        
        columnNames.getColumnIndex("name that does not exist");
    }
    
    @Test
    public void verifyIfAColumnExists() {
        CsvColumnNames columnNames = new CsvColumnNames(new String[] {"A", "B", "C", "D"});
        
        assertThat(columnNames.hasColumn("A"), is(true));
        assertThat(columnNames.hasColumn("E"), is(false));
    }
    
    
}
