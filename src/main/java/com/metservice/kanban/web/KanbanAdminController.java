package com.metservice.kanban.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.KanbanProject;

/**
 * Controller for Admin page and functions.
 * @author Janella Espinas, Nathan Green
 */
@Controller
@RequestMapping("{projectName}/admin")
public class KanbanAdminController {
	private KanbanService kanbanService;

	/**
	 * Default constructor for KanbanAdminController. Initialises its own KanbanService.
	 */
	public KanbanAdminController(){
		this.kanbanService = new KanbanService();
	}
	
	/**
	 * Constructor for KanbanAdminController given a KanbanService.
	 * @param kanbanService
	 */
	public KanbanAdminController(KanbanService kanbanService){
		this.kanbanService = kanbanService;
	}

	/**
	 * Helper method to return the current project for the current KanbanService.
	 * [n.b., this is called every time there is an @ModelAttribute("project")
	 * argument in a view method.]
	 * @param projectName - the name of the project
	 * @return the current project
	 * @throws IOException
	 */
	@ModelAttribute("project")
	public synchronized KanbanProject populateProject(
			@PathVariable("projectName") String projectName) throws IOException {
		return kanbanService.getKanbanProject(projectName);
	}
	
	/**
	 * Default admin view (/{projectName}/admin).
	 * @param project
	 * @param projectName
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("")
    public synchronized ModelAndView admin(
			@ModelAttribute("project") KanbanProject project,
    		@PathVariable("projectName") String projectName		
    		) throws IOException {

		// Build the model
        Map<String, Object> model = buildModel(projectName);
        
        return new ModelAndView("/admin.jsp", model);
    }

	/**
	 * Builds the default model for Admin views, and adds common settings to the
	 * model.
	 * @param projectName - the name of the project
	 * @return the default Admin view model
	 */
    private Map<String, Object> buildModel(String projectName) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("projectName", projectName);
        // TODO remove; used as an example to show how to add settings, etc.
        model.put("username", "hullo"); 
        return model;
    }
	
}
