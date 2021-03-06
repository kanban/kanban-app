package com.metservice.kanban;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.utils.DateUtils;

@Service
public class EstimatesDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EstimatesDao.class);

    private static final String PET_PROPERTIES = "pet.properties";
    private static final String PET_COSTPERPOINT = "pet.costperpoint";
    private static final String PET_BUDGET = "pet.budget";
    private static final String PET_COST_DAYS = "pet.cost";

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
            project.setEstimatedCostPerPoint(Integer.parseInt((String) props.get(PET_COSTPERPOINT)));

            String costDays = (String) props.get(PET_COST_DAYS);

            project.setDayCosts(getCostDailyMap(costDays));

        } else {
            project.setBudget(0);
            project.setEstimatedCostPerPoint(0);
            project.setDayCosts(getCostDailyMap(null));
        }

        return project;
    }

    public static Map<LocalDate, Integer> getCostDailyMap(String costStr) {
        Map<LocalDate, Integer> result = new TreeMap<LocalDate, Integer>();
        if (StringUtils.isEmpty(costStr)) {
            return result;
        }
        for (String dayAndCostStr : StringUtils.split(costStr, ";")) {
            try {
                String[] dayAndCost = StringUtils.split(dayAndCostStr, "\\|");
                result.put(LocalDate.parse(dayAndCost[0]), Integer.parseInt(dayAndCost[1]));
            } catch (Exception e) {
                LOGGER.error("Error getting cost daily map for item {}", dayAndCostStr);
                LOGGER.error("Got exception", e);
            }
        }

        return result;
    }

    public static String getCostDailyStr(Map<LocalDate, Integer> data) {
        StringBuilder result = new StringBuilder();

        for (LocalDate day : data.keySet()) {
            result.append(day.toString(DateUtils.DATE_FORMAT_STR)).append('|').append(data.get(day)).append(';');
        }

        return result.toString();
    }

    public void storeProjectEstimates(EstimatesProject project) throws IOException {
        // store in config file
        Properties props = new Properties();
        props.setProperty(PET_BUDGET, Integer.toString(project.getBudget()));
        props.setProperty(PET_COSTPERPOINT, Integer.toString(project.getEstimatedCostPerPoint()));
        props.setProperty(PET_COST_DAYS, getCostDailyStr(project.getDayCosts()));

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
