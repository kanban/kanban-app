package com.metservice.kanban.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.csv.KanbanCsvFile;
import com.metservice.kanban.model.KanbanProjectConfiguration;
import java.util.Collection;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class KanbanDataController {

    private KanbanService kanbanService;

    public KanbanDataController() {
        this.kanbanService = new KanbanService();
    }

    @RequestMapping("/download-csv")
    public void downloadCsv(@PathVariable("projectName") String projectName, 
        HttpServletResponse response) throws FileNotFoundException, IOException {
        
        //TODO:Test bad project name
        
        KanbanProjectConfiguration configuration = kanbanService.getProjectConfiguration(projectName);
        KanbanPersistence persistence = new KanbanPersistence(configuration);
        
        
        
        Collection<KanbanCsvFile> files = persistence.getFiles();
        
        
        
        if (files.size() == 1) {
            //TODO: Test single level project
            response.setContentType("text/csv");
        } else if (files.size() > 1) {
            //TODO: Test multi level project
            response.setContentType("application/zip");
        } else {
            //TODO: Is this possible if you've found a valid project?
        }
        
//        response.setContentLength(file.getFile().length);
//        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getFilename() +"\"");
//        IOUtils.copy(zipFile, response.getOutputStream());
              
    }
}
