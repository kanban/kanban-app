package com.metservice.kanban.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.metservice.kanban.KanbanService;

@Controller
public class KanbanWelcomeController {

    @Autowired
    private KanbanService kanbanService;

    public KanbanWelcomeController() {
    }

    @RequestMapping("/welcome")
    public ModelAndView kanbanWelcome() {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("service", kanbanService);
        model.put("homeExists", kanbanService.getHome().exists());
        Collection<String> projects = kanbanService.getProjects();
        model.put("listOfProjects", projects);
        model.put("projectsCount", projects.size());

        return new ModelAndView("index.jsp", model);
    }

    public void setKanbanService(KanbanService kanbanService) {
        this.kanbanService = kanbanService;
    }
}
