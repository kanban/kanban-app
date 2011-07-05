package com.metservice.kanban.csv;

import static org.apache.commons.lang.SystemUtils.LINE_SEPARATOR;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class KanbanCsvReaderTest {

    private static final String ESCAPED_QUOTATION_MARK_CHARACTER = "\"\"\"\"";

    @Test
    public void readsWorkItems() throws IOException {
        StringReader input = new StringReader("column names" + LINE_SEPARATOR
            + "work item 1" + LINE_SEPARATOR
            + "work item 2");

        WorkItemParser parser = mock(WorkItemParser.class);

        when(parser.parseWorkItem(anyColumnNames(), anyStringArray())).thenAnswer(new Answer<WorkItem>() {

            @Override
            public WorkItem answer(InvocationOnMock invocation) throws Throwable {
                String[] row = (String[]) invocation.getArguments()[1];
                String name = row[0];
                return createWorkItem(name);
            }
        });

        KanbanCsvReader reader = new KanbanCsvReader(input, parser);
        List<WorkItem> workItems = reader.read();

        assertThat(workItems.get(0).getName(), is("work item 1"));
        assertThat(workItems.get(1).getName(), is("work item 2"));
    }

    @Test
    public void parsesQuotedCsv() throws IOException {
        StringReader input = new StringReader("column names" + LINE_SEPARATOR + ESCAPED_QUOTATION_MARK_CHARACTER);

        WorkItemParser parser = stubWorkItemParser();

        KanbanCsvReader reader = new KanbanCsvReader(input, parser);
        reader.read();

        ArgumentCaptor<String[]> rowCaptor = ArgumentCaptor.forClass(String[].class);
        verify(parser).parseWorkItem(anyColumnNames(), rowCaptor.capture());

        String[] row = rowCaptor.getValue();
        assertThat(row[0], is("\""));
    }

    @Test
    public void parsesAllCells() throws IOException {
        StringReader input = new StringReader("column names" + LINE_SEPARATOR + "a,b,c");

        WorkItemParser parser = stubWorkItemParser();

        KanbanCsvReader reader = new KanbanCsvReader(input, parser);
        reader.read();

        ArgumentCaptor<String[]> rowCaptor = ArgumentCaptor.forClass(String[].class);
        verify(parser).parseWorkItem(anyColumnNames(), rowCaptor.capture());

        String[] row = rowCaptor.getValue();
        assertThat(row[0], is("a"));
        assertThat(row[1], is("b"));
        assertThat(row[2], is("c"));
    }

    @Test
    public void passesColumnNamesToParser() throws IOException {
        StringReader input = new StringReader("column 1,column 2,column 3" + LINE_SEPARATOR + "work item 1"
            + LINE_SEPARATOR + "work item 2");

        WorkItemParser parser = stubWorkItemParser();

        KanbanCsvReader reader = new KanbanCsvReader(input, parser);
        reader.read();

        ArgumentCaptor<CsvColumnNames> columnNamesCaptor = ArgumentCaptor.forClass(CsvColumnNames.class);
        verify(parser, times(2)).parseWorkItem(columnNamesCaptor.capture(), anyStringArray());

        CsvColumnNames columnNames = columnNamesCaptor.getValue();
        assertThat(columnNames.getColumnIndex("column 1"), is(0));
        assertThat(columnNames.getColumnIndex("column 3"), is(2));
        
        List<CsvColumnNames> columnNamesList = columnNamesCaptor.getAllValues();
        assertTrue(columnNamesList.get(0) == columnNamesList.get(1));
    }

    private WorkItemParser stubWorkItemParser() {
        WorkItemParser parser = mock(WorkItemParser.class);
        when(parser.parseWorkItem(anyColumnNames(), anyStringArray())).thenReturn(createWorkItem("work item"));
        return parser;
    }

    private WorkItem createWorkItem(String name) {
        WorkItem workItem = new WorkItem(0, 0, new WorkItemType("feature"));
        workItem.setName(name);
        return workItem;
    }

    private static CsvColumnNames anyColumnNames() {
        return Mockito.any(CsvColumnNames.class);
    }

    private static String[] anyStringArray() {
        return Mockito.any(String[].class);
    }
}
