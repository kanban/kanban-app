package com.metservice.kanban.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.KanbanProjectConfiguration;
import com.metservice.kanban.model.WorkItemType;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KanbanDataController {

    private KanbanService kanbanService;

    public KanbanDataController() {
        this.kanbanService = new KanbanService();
    }

    public KanbanDataController(KanbanService kanbanService) {
        this.kanbanService = kanbanService;
    }
    
    @RequestMapping("/download/{projectName}")
    public void download(@PathVariable("projectName") String projectName, 
        @RequestParam("workItemTypeName") String workItemTypeName,
        HttpServletResponse response) throws FileNotFoundException, IOException {
        
        KanbanProjectConfiguration configuration = kanbanService.getProjectConfiguration(projectName);
        WorkItemType workItemType = configuration.getWorkItemTypes().getByName(workItemTypeName);
        
        File file = configuration.getDataFile(workItemType);
        response.setContentType("text/csv");
        response.setContentLength((int)file.length());
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        IOUtils.copy(new FileInputStream(file), response.getOutputStream());
    }
}
