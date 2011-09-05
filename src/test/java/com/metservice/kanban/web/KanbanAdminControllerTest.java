package com.metservice.kanban.web;

import static com.metservice.kanban.utils.DateUtils.parseConventionalNewZealandDate;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.charts.burnup.BurnUpChartGenerator;
import com.metservice.kanban.model.DefaultKanbanProject;
import com.metservice.kanban.model.DefaultWorkItemTree;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.TreeNode;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.model.WorkItemTypeCollection;

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

        KanbanAdminController kanbanController = new KanbanAdminController(kanbanService);
        assertThat(kanbanController.populateProject("project"), is(kanban));
    }

    @Test
    public void presentsAdminPage() throws IOException {
    	KanbanAdminController kanbanController = new KanbanAdminController(null);
        KanbanProject project = mock(KanbanProject.class);
    	ModelAndView modelAndView = kanbanController.admin(project, "project");

        assertThat(modelAndView.getViewName(), is("/admin.jsp"));
    }
}
