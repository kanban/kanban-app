package com.metservice.kanban.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.KanbanProject;

public class KanbanBoardControllerLegacyTest {

    private KanbanBoardController kanbanController;
    private KanbanService fakeKanbanService;
    private KanbanProject kanban;

    @Before
    public void setup() throws IOException {
        fakeKanbanService = new KanbanService(new File(SystemUtils.getUserDir(), "/src/test/resources"));
        kanbanController = new KanbanBoardController();
        kanbanController.setKanbanService(fakeKanbanService);
        kanban = fakeKanbanService.getKanbanProject("test-project");
    }


  @Test
  public void testBoard() throws IOException {

        ModelAndView modelAndView = kanbanController.wallBoard(kanban, "test-project", null,
            new HashMap<String, String>());
        assertThat(modelAndView.getViewName(), is("/project.jsp"));
        assertThat((String) modelAndView.getModel().get("projectName"), is("test-project"));
        assertThat((String) modelAndView.getModel().get("boardType"), is("wall"));
    }

    @Test
    public void testAddItem() throws IOException {
        
        ModelAndView modelAndView = kanbanController.addItem(kanban, "test-project", 1);
        assertThat(modelAndView.getViewName(), is("/add.jsp"));
        assertThat(modelAndView.getModel().get("legend"), notNullValue());
        assertThat(modelAndView.getModel().get("parentId"), notNullValue());
        assertThat(modelAndView.getModel().get("type"), notNullValue());
    }

    @Test
    public void testEditItem() throws IOException {
        ModelAndView modelAndView = kanbanController.editItem(kanban, "test-project", "wall", 1);
        assertThat(modelAndView.getViewName(), is("/edit.jsp"));
        assertThat(modelAndView.getModel().get("workItem"), notNullValue());
        assertThat(modelAndView.getModel().get("children"), notNullValue());
        assertThat(modelAndView.getModel().get("parentAlternativesList"), notNullValue());
        assertThat(modelAndView.getModel().get("phasesMap"), notNullValue());
    }

    @Test
    public void testPrintItems() throws IOException {
        ModelAndView modelAndView = kanbanController.printItems(kanban, "test-project", new String[] {
            "1",
            "2"});
        assertThat(modelAndView.getViewName(), is("/printCards.jsp"));
        assertThat(modelAndView.getModel().get("items"), notNullValue());
    }

    @Test
    public void testEditProject() throws IOException {
        ModelAndView modelAndView = kanbanController.editProject(kanban, "test-project", false);
        assertThat(modelAndView.getViewName(), is("/editProject.jsp"));
        assertThat((String) modelAndView.getModel().get("projectName"), is("test-project"));
        assertThat((String) modelAndView.getModel().get("boardType"), is("wall"));
    }

    @Test
    public void testCreateProject() throws IOException {
        ModelAndView modelAndView = kanbanController.editProject(kanban, "test-project", true);
        assertThat(modelAndView.getViewName(), is("/createProject.jsp"));
        assertThat((String) modelAndView.getModel().get("projectName"), is("test-project"));
        assertThat((String) modelAndView.getModel().get("boardType"), is("wall"));
    }
    
    @Ignore
    @Test
    public void testAdvanceItemAction() throws IOException {
        RedirectView view = kanbanController.advanceItemAction(kanban, "wall", "1");
        assertThat(view.getUrl(), is("../wall"));
    }

    // This modifies the feature.csv file which then needs checking back in to SVN.
    // TODO We need to move our test working data out of the src hierarchy.
    // NOTE: Edited by Nick & Janella
    @Ignore
    @Test
    public void testAddItemAction() throws IOException {
    	MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("type", "feature");
        request.addParameter("size", "5");
        request.addParameter("importance", "10");
        request.addParameter("notes", "");
        request.addParameter("color", "000FFF");
        RedirectView view = kanbanController.addItemAction(kanban, "wall", 0, "feature", "test", "5", "8",
            "10", "", "000FFF", null, "", request);

        assertThat(view.getUrl(), is("../wall"));
    }

    @Test
    public void testMoveItemAction() throws IOException {
        RedirectView view = kanbanController.moveItemAction(kanban, "wall", "2",  "6", true, "512");
        assertThat(view.getUrl(), is("../wall?scrollTop=512"));
    }

    @Ignore
    @Test
    public void testCreateProjectAction() throws IOException {
        RedirectView view = kanbanController.createProjectAction(kanban, "test-project", "new-test-project", "");
        assertThat(view.getUrl(), is("../wall"));
    }

    @Test
    public void testOpenProject() throws IOException {
        RedirectView view = kanbanController.openProject("test-project", "wall", "new-test-project", null, null);
        assertThat(view.getUrl(), is("/projects/new-test-project/wall"));
    }
}
