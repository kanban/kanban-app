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

/**
 * Controller for reading and storing configurations from the CSV data and 
 * .properties files for a given project.
 * @author Janella Espinas, Liam O'Connor
 */
@Controller
public class KanbanDataController {

    private KanbanService kanbanService;

    /**
     * Default constructor for KanbanDataController. Initialises its own KanbanService.
     */
    public KanbanDataController() {
        this.kanbanService = new KanbanService();
    }

    /**
     * Constructor for KanbanDataController given a KanbanService.
     * @param kanbanService - the core KanbanService
     */
    public KanbanDataController(KanbanService kanbanService) {
        this.kanbanService = kanbanService;
    }
    
    /**
     * Populates the Kanban application with a given project name and WorkItemType. This application
     * is then presented on a given server.
     * @param projectName - the name of the project
     * @param workItemTypeName - the type of WorkItem
     * @param response - the HttpServlet for the site
     * @throws FileNotFoundException
     * @throws IOException
     */
    @RequestMapping("/download/{projectName}")
    public void download(@PathVariable("projectName") String projectName, 
        @RequestParam("workItemTypeName") String workItemTypeName,
        HttpServletResponse response) throws FileNotFoundException, IOException {
        
        KanbanProjectConfiguration configuration = kanbanService.getProjectConfiguration(projectName);
        WorkItemType workItemType = configuration.getWorkItemTypes().getByName(workItemTypeName);
        
        // copies and saves the csv file
        File file = configuration.getDataFile(workItemType);
        response.setContentType("text/csv");
        response.setContentLength((int)file.length());
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        IOUtils.copy(new FileInputStream(file), response.getOutputStream());
    }
}
