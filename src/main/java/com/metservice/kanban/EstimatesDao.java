package com.metservice.kanban;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.KanbanProject;

@Service
public class EstimatesDao {

    private static final String PET_PROPERTIES = "pet.properties";
    private static final String PET_COSTPERPOINT = "pet.costperpoint";
    private static final String PET_COSTSOFAR = "pet.costsofar";
    private static final String PET_BUDGET = "pet.budget";

    private KanbanService kanbanService;

    public EstimatesProject loadProject(String projectName) throws IOException {

        KanbanProject kbProject = kanbanService.getKanbanProject(projectName);

        EstimatesProject project = new EstimatesProject();
        project.setProjectName(projectName);
        project.setKanbanProject(kbProject);

        // load project data if exists

        File propsFile = getPropsFile(projectName);

        if (propsFile.exists()) {
            InputStream propsIs = new FileInputStream(propsFile);
            Properties props = new Properties();
            try {
                props.load(propsIs);
            } finally {
                propsIs.close();
            }

            project.setBudget(Integer.parseInt((String) props.get(PET_BUDGET)));
            project.setCostSoFar(Integer.parseInt((String) props.get(PET_COSTSOFAR)));
            project.setEstimatedCostPerPoint(Integer.parseInt((String) props.get(PET_COSTPERPOINT)));
        } else {
            project.setBudget(0);
            project.setCostSoFar(0);
            project.setEstimatedCostPerPoint(0);
        }

        return project;
    }

    public void storeProjectEstimates(EstimatesProject project) throws IOException {
        // store in config file
        Properties props = new Properties();
        props.setProperty(PET_BUDGET, Integer.toString(project.getBudget()));
        props.setProperty(PET_COSTSOFAR, Integer.toString(project.getCostSoFar()));
        props.setProperty(PET_COSTPERPOINT, Integer.toString(project.getEstimatedCostPerPoint()));

        File propsFile = getPropsFile(project.getProjectName());
        OutputStream propsOs = new FileOutputStream(propsFile);
        try {
            props.store(propsOs, "Project Estimation Tool");
        } finally {
            propsOs.close();
        }
    }

    public void storeUpdatedFeatures(EstimatesProject project) throws IOException {
        project.getKanbanProject().save();
    }

    @Autowired
    public void setKanbanService(KanbanService kanbanService) {
        this.kanbanService = kanbanService;
    }

    private File getPropsFile(String projectName) {
        File home = new File(kanbanService.getHome(), projectName);
        return new File(home, PET_PROPERTIES);
    }
}
