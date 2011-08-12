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

@Controller
@RequestMapping("{projectName}/admin")
public class KanbanAdminController {
	private KanbanService kanbanService;

	public KanbanAdminController(){
		this.kanbanService = new KanbanService();
	}
	
	public KanbanAdminController(KanbanService kanbanService){
		this.kanbanService = kanbanService;
	}

	@ModelAttribute("project")
	public synchronized KanbanProject populateProject(
			@PathVariable("projectName") String projectName) throws IOException {
		return kanbanService.getKanbanProject(projectName);
	}
	
	@RequestMapping("")
    public synchronized ModelAndView admin(
			@ModelAttribute("project") KanbanProject project,
    		@PathVariable("projectName") String projectName		
    		) throws IOException {

        Map<String, Object> model = buildModel(projectName);
        return new ModelAndView("/admin.jsp", model);
    }

    private Map<String, Object> buildModel(String projectName) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("projectName", projectName);
        model.put("username", "hullo");
        return model;
    }
	
}
