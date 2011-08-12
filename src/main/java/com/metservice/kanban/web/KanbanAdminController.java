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

	@RequestMapping("")
    public synchronized ModelAndView admin(
    		@PathVariable("projectName") String projectName		
    		) throws IOException {
		System.out.println("admin view");
//
//        String scrollTop = extractScrollPositionInfoFromBoardType(boardType);
//        boardType = cleanBoardType(boardType);
//
//        Map<String, Object> model = buildModel(projectName, boardType);
//
//        // TODO model used to have kanbanTransaction now it has kanban... need to fix view
//
//        model.put("scrollTop", scrollTop == null ? "0" : scrollTop);
//
//        if (boardType.equals("backlog")) {
//            model.put("kanbanBacklog", project.getBacklog());
//            model.put("type", project.getWorkItemTypes().getRoot().getValue());
//            model.put("phase", project.getWorkItemTypes().getRoot().getValue().getPhases().get(0));
//            return new ModelAndView("/backlog.jsp", model);
//        } else if (boardType.equals("completed")) {
//            model.put("type", project.getWorkItemTypes().getRoot().getValue());
//            List<String> phases = project.getWorkItemTypes().getRoot().getValue().getPhases();
//            model.put("phase", phases.get(phases.size() - 1));
//            return new ModelAndView("/completed.jsp", model);
//        }
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
