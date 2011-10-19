package com.metservice.kanban.web;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import com.metservice.kanban.KanbanService;

public class KanbanDataControllerTest {

    private KanbanService fakeKanbanService;
    
    @Before
    public void setup() throws IOException {
        fakeKanbanService = new KanbanService(new File(SystemUtils.getUserDir(), "/src/test/resources"));
    }
    
    @Test
    public void downloadCsv() throws IOException {
        KanbanDataController dataController = new KanbanDataController();
        dataController.setKanbanService(fakeKanbanService);
        MockHttpServletResponse response = new MockHttpServletResponse();
        dataController.download("test-project", "story", response);
        assertThat((String)response.getHeader("Content-Disposition"), is("attachment; filename=\"story.csv\""));
        assertThat(response.getContentType(), is("text/csv"));
        File expectedFile = new File(SystemUtils.getUserDir(), "/src/test/resources/test-project/story.csv");
        String expectedContent = FileUtils.readFileToString(expectedFile);
        assertThat(response.getContentAsString(), is(expectedContent));
    }
}
