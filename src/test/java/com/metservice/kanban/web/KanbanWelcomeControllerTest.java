package com.metservice.kanban.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;
import com.metservice.kanban.KanbanService;


public class KanbanWelcomeControllerTest {

    private KanbanWelcomeController kanbanWelcomeController;
    private KanbanService serviceMock;

    @Before
    public void setUp() {
        kanbanWelcomeController = new KanbanWelcomeController();
        serviceMock = mock(KanbanService.class);
        kanbanWelcomeController.setKanbanService(serviceMock);
    }

    @Test
    public void kanbanWelcomeTest() {
        
        when(serviceMock.getHome()).thenReturn(new File("."));
        
        when(serviceMock.getProjects()).thenReturn(new ArrayList<String>());

        ModelAndView welcome = kanbanWelcomeController.kanbanWelcome();

        assertEquals("index.jsp", welcome.getViewName());

        assertNotNull(welcome.getModel().get("service"));

        assertNotNull("homeExists");
        assertNotNull("listOfProjects");
        assertNotNull("projectsCount");

    }
}
