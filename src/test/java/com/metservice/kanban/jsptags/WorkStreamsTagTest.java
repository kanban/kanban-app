package com.metservice.kanban.jsptags;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public class WorkStreamsTagTest {

    static String OUTPUT_FORMAT = "var %s = [ %s ];\n" +
        "       $(function() {\n" +
        "               $('#workStreams').tagit({\n" +
        "                       tagSource   : workStreams,\n" +
        "                       triggerKeys : [ 'enter', 'comma' ],\n" +
        "                       initialTags : [ %s ],\n" +
        "                       select: true               });\n" +
        "        });\n";
    
    WorkStreamsTag tag;
    PageContext contextMock;
    StringWriter outputStream;
    JspWriter writerMock;
    KanbanProject projectMock;

    @Before
    public void setUp() throws IOException {
        tag = new WorkStreamsTag();
        contextMock = mock(PageContext.class);

        outputStream = new StringWriter();
        writerMock = mock(JspWriter.class);
        projectMock = mock(KanbanProject.class);

        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                outputStream.write((String) invocation.getArguments()[0]);
                return null;
            }
            
        }).when(writerMock).write(anyString());
        
        tag.setPageContext(contextMock);
        when(contextMock.getOut()).thenReturn(writerMock);
    }

    @Test
    public void testProjectIsNotSetPrintsError() throws JspException {
        assertEquals(Tag.EVAL_PAGE, tag.doStartTag());

        String result = outputStream.toString();

        assertEquals("ERROR: Project is not set.", result);
    }

    @Test
    public void testNoInitialWorkStreams() throws JspException {

        Set<String> workStreams = new TreeSet<String>();

        workStreams.add("ws1");
        workStreams.add("ws2");

        when(projectMock.getWorkStreams()).thenReturn(workStreams);

        tag.setProject(projectMock);
        tag.setName("aName");

        assertEquals(Tag.EVAL_PAGE, tag.doStartTag());

        String result = outputStream.toString();

        assertEquals(String.format(OUTPUT_FORMAT, "aName", "'ws1','ws2',''", ""), result);
    }

    @Test
    public void testInitialWorkStreams() throws JspException {

        Set<String> workStreams = new TreeSet<String>();

        workStreams.add("ws1");
        workStreams.add("ws2");

        when(projectMock.getWorkStreams()).thenReturn(workStreams);

        tag.setProject(projectMock);
        tag.setName("aName");
        tag.setInitialWorkStream("ws1");

        assertEquals(Tag.EVAL_PAGE, tag.doStartTag());

        String result = outputStream.toString();

        assertEquals(String.format(OUTPUT_FORMAT, "aName", "'ws1','ws2',''", "'ws1'"), result);
    }

    @Test
    public void testInitialWorkStreamsFromWorkItem() throws JspException {

        Set<String> workStreams = new TreeSet<String>();

        workStreams.add("ws1");
        workStreams.add("ws2");

        WorkItem workItem = new WorkItem(1, new WorkItemType());
        workItem.setWorkStreamsAsString("a,b");

        when(projectMock.getWorkStreams()).thenReturn(workStreams);

        tag.setProject(projectMock);
        tag.setName("aName");
        tag.setWorkItem(workItem);

        assertEquals(Tag.EVAL_PAGE, tag.doStartTag());

        String result = outputStream.toString();

        assertEquals(String.format(OUTPUT_FORMAT, "aName", "'ws1','ws2',''", "'a','b'"), result);
    }
}
