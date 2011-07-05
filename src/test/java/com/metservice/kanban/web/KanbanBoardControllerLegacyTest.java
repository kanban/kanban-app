package com.metservice.kanban.web;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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
        kanbanController = new KanbanBoardController(fakeKanbanService);
        kanban = fakeKanbanService.getKanbanProject("test-project");
    }


  @Test
  public void testBoard() throws IOException {

        ModelAndView modelAndView = kanbanController.board(null, "test-project", "wall");
        assertThat(modelAndView.getViewName(), is("/project.jsp"));
        assertThat((String) modelAndView.getModel().get("projectName"), is("test-project"));
        assertThat((String) modelAndView.getModel().get("boardType"), is("wall"));
    }

    @Test
    public void testAddItem() throws IOException {
        
        ModelAndView modelAndView = kanbanController.addItem(kanban, "test-project", "wall", 1);
        assertThat(modelAndView.getViewName(), is("/add.jsp"));
        assertThat((String) modelAndView.getModel().get("projectName"), is("test-project"));
        assertThat((String) modelAndView.getModel().get("boardType"), is("wall"));
        assertThat(modelAndView.getModel().get("workItem"), notNullValue());
    }

    @Test
    public void testEditItem() throws IOException {
        ModelAndView modelAndView = kanbanController.editItem(kanban, "test-project", "wall", 1);
        assertThat(modelAndView.getViewName(), is("/edit.jsp"));
        assertThat((String) modelAndView.getModel().get("projectName"), is("test-project"));
        assertThat((String) modelAndView.getModel().get("boardType"), is("wall"));
        assertThat(modelAndView.getModel().get("workItem"), notNullValue());
    }

    @Test
    public void testPrintItems() throws IOException {
        ModelAndView modelAndView = kanbanController.printItems(kanban, "test-project", "wall", new String[] {
            "1",
            "2"});
        assertThat(modelAndView.getViewName(), is("/printCards.jsp"));
        assertThat((String) modelAndView.getModel().get("projectName"), is("test-project"));
        assertThat((String) modelAndView.getModel().get("boardType"), is("wall"));
        assertThat(modelAndView.getModel().get("ids"), notNullValue());
    }

    @Test
    public void testEditProject() throws IOException {
        ModelAndView modelAndView = kanbanController.editProject(kanban, "test-project", "wall", false);
        assertThat(modelAndView.getViewName(), is("/createProject.jsp"));
        assertThat((String) modelAndView.getModel().get("projectName"), is("test-project"));
        assertThat((String) modelAndView.getModel().get("boardType"), is("wall"));
    }

    @Ignore
    @Test
    public void testAdvanceItemAction() throws IOException {
        RedirectView view = kanbanController.advanceItemAction(kanban, "wall", "1", "0");
        assertThat(view.getUrl(), is("../wall"));
    }

    // This modifies the feature.csv file which then needs checking back in to SVN.
    // TODO We need to move our test working data out of the src hierarchy.
    @Ignore
    @Test
    public void testAddItemAction() throws IOException {
        RedirectView view = kanbanController.addItemAction(kanban, "test-project", "wall", "0", "feature", "test",
            5, 10, "");
        assertThat(view.getUrl(), is("../wall"));
    }

    @Test
    public void testMoveItemAction() throws IOException {
        RedirectView view = kanbanController.moveItemAction(kanban, "wall", "2",  "6", true, "512");
        assertThat(view.getUrl(), is("../wall:512"));
    }

    @Ignore
    @Test
    public void testCreateProjectAction() throws IOException {
        RedirectView view = kanbanController.createProjectAction(kanban, "test-project", "wall",
            "new-test-project", "");
        assertThat(view.getUrl(), is("../wall"));
    }

    @Test
    public void testOpenProject() throws IOException {
        RedirectView view = kanbanController.openProject("test-project", "wall", "new-test-project");
        assertThat(view.getUrl(), is("/projects/new-test-project/wall"));
    }
}
