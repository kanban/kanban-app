package com.metservice.kanban.web;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.KanbanProject;

/**
 * Test library for KanbanAdminController.
 * @author Janella Espinas
 */
public class KanbanAdminControllerTest {

    @Test
    public void modelContainsKanban() throws IOException {
        KanbanService kanbanService = mock(KanbanService.class);
        KanbanProject kanban = mock(KanbanProject.class);

        when(kanbanService.getKanbanProject("project")).thenReturn(kanban);

        KanbanAdminController kanbanController = new KanbanAdminController();
        kanbanController.setKanbanService(kanbanService);

        assertThat(kanbanController.populateProject("project"), is(kanban));
    }

    @Test
    public void presentsAdminPage() throws IOException {
        KanbanAdminController kanbanController = new KanbanAdminController();
        KanbanProject project = mock(KanbanProject.class);
    	ModelAndView modelAndView = kanbanController.admin(project, "project");

        assertThat(modelAndView.getViewName(), is("/admin.jsp"));
    }
}
