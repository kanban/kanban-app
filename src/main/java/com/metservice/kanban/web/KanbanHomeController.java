package com.metservice.kanban.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.KanbanService;

@Controller
public class KanbanHomeController {

    private KanbanService kanbanService;

    public KanbanHomeController() {
        this.kanbanService = new KanbanService();
    }

    @RequestMapping("/create-home")
    public View createKanbanHome() throws FileNotFoundException, IOException {
        File home = kanbanService.getHome();
        home.mkdirs();

        File sampleProject = new File(home, "Sample");
        sampleProject.mkdir();

        File sampleProjectConfiguration = new File(sampleProject, "kanban.properties");
        IOUtils.copy(KanbanHomeController.class.getResourceAsStream("/sample-kanban.properties"),
            new FileOutputStream(sampleProjectConfiguration));
        
        return new RedirectView("/", true);
    }
}
