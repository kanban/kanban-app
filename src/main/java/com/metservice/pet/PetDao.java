package com.metservice.pet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.KanbanProject;

@Service
public class PetDao {

    private static final String PET_COSTPERPOINT = "pet.costperpoint";
    private static final String PET_COSTSOFAR = "pet.costsofar";
    private static final String PET_BUDGET = "pet.budget";

    private KanbanService kanbanService;

    public Project loadProject(String projectName) throws IOException {

        KanbanProject kbProject = kanbanService.getKanbanProject(projectName);

        Project project = new Project();
        project.setProjectName(projectName);
        project.setKanbanProject(kbProject);

        // load project data if exists

        File propsFile = getPropsFile(projectName);

        if (propsFile.exists()) {
            InputStream propsIs = new FileInputStream(propsFile);
            Properties props = new Properties();
            props.load(propsIs);
            propsIs.close();

            project.setBudget(Integer.parseInt((String) props.get(PET_BUDGET)));
            project.setCostSoFar(Integer.parseInt((String) props.get(PET_COSTSOFAR)));
            project.setEstimatedCostPerPoint(Integer.parseInt((String) props.get(PET_COSTPERPOINT)));
        }
        else {
            project.setBudget(0);
            project.setCostSoFar(0);
            project.setEstimatedCostPerPoint(0);
        }

        return project;
    }

    public void storeProjectEstimates(Project project) throws IOException {
        // store in config file
        Properties props = new Properties();
        props.setProperty(PET_BUDGET, "" + project.getBudget());
        props.setProperty(PET_COSTSOFAR, "" + project.getCostSoFar());
        props.setProperty(PET_COSTPERPOINT, "" + project.getEstimatedCostPerPoint());

        File propsFile = getPropsFile(project.getProjectName());
        OutputStream propsOs = new FileOutputStream(propsFile);
        props.store(propsOs, "Project Estimation Tool");
        propsOs.close();
    }

    public void storeUpdatedFeatures(Project project) throws IOException {
        project.getKanbanProject().save();
    }

    @Autowired
    public void setKanbanService(KanbanService kanbanService) {
        this.kanbanService = kanbanService;
        System.out.println("Set kanban service");
    }

    private File getPropsFile(String projectName) {
        File home = new File(kanbanService.getHome(), projectName);
        File propsFile = new File(home, "pet.properties");
        return propsFile;
    }
}
