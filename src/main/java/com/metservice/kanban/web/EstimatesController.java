package com.metservice.kanban.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.EstimatesDao;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.EstimatesProject;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;

//TODO This class needs unit tests.
@Controller
@RequestMapping("{projectName}")
public class EstimatesController {

    private final static Logger logger = LoggerFactory.getLogger(EstimatesController.class);

    private static final String ESTIMATES_PROJECT_ATTR = "estimatesproject";
    private static final String KANBAN_PROJECT_ATTR = "project";
    private static final String ESTIMATES_JSP = "estimates.jsp";
    private static final String ESTIMATES_FEATURE_JSP = "petfeature.jsp";

    @Autowired
    EstimatesDao estimatesDao;

    @Autowired
    KanbanService kanbanService;

    @ModelAttribute(ESTIMATES_PROJECT_ATTR)
    public synchronized EstimatesProject populatePetProject(@PathVariable("projectName") String projectName) throws IOException {
        EstimatesProject project = estimatesDao.loadProject(projectName);

        return project;
    }

    @ModelAttribute(KANBAN_PROJECT_ATTR)
    public synchronized KanbanProject populateProject(@PathVariable("projectName") String projectName)
        throws IOException {
        return kanbanService.getKanbanProject(projectName);
    }

    @ModelAttribute("projectName")
    public String populateProjectName(@PathVariable("projectName") String bprojectName) {
        return bprojectName;
    }

    @RequestMapping("estimates")
    public ModelAndView showProject(@ModelAttribute("petproject") EstimatesProject estimatesProject) {

        Map<String, Object> model = new HashMap<String, Object>();

        return new ModelAndView(ESTIMATES_JSP, model);
    }

    @ModelAttribute("service")
    public synchronized KanbanService populateService()
        throws IOException {

        return kanbanService;
    }
    @RequestMapping("pet-set-project-property")
    public RedirectView setBudget(String name, int value,
                                  @ModelAttribute(ESTIMATES_PROJECT_ATTR) EstimatesProject project)
        throws IOException {

        if ("budget".equals(name)) {
            project.setBudget(value);
        } else if ("estimatedCostPerPoint".equals(name)) {
            project.setEstimatedCostPerPoint(value);
        } else {
            throw new IllegalArgumentException("name = " + name);
        }
        estimatesDao.storeProjectEstimates(project);
        return new RedirectView("estimates");
    }

    @RequestMapping("pet-edit-feature")
    public ModelAndView editFeature(int id, @ModelAttribute(ESTIMATES_PROJECT_ATTR) EstimatesProject project) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("pageTitle", "Edit feature");
        model.put("feature", project.getFeature(id));

        return new ModelAndView(ESTIMATES_FEATURE_JSP, model);
    }

    @RequestMapping("pet-save-feature")
    public RedirectView saveFeature(int id, int averageCaseEstimate, int worstCaseEstimate,
                                    @ModelAttribute(ESTIMATES_PROJECT_ATTR) EstimatesProject project)
        throws IOException {

        Assert.isTrue(id != 0);

        // get WI for feature
        WorkItem workItem = project.getKanbanProject().getWorkItemById(id);
        // update WI from feature
        workItem.setAverageCaseEstimate(averageCaseEstimate);
        workItem.setWorstCaseEstimate(worstCaseEstimate);

        estimatesDao.storeUpdatedFeatures(project);

        return new RedirectView("estimates");
    }

    @RequestMapping("pet-set-feature-included-in-estimates")
    public RedirectView excludeFeature(int id, boolean value,
                                       @ModelAttribute(ESTIMATES_PROJECT_ATTR) EstimatesProject project)
        throws IOException {

        boolean includedInEstimates = value;

        WorkItem feature = project.getKanbanProject().getWorkItemById(id);
        feature.setMustHave(includedInEstimates);

        estimatesDao.storeUpdatedFeatures(project);

        return new RedirectView("estimates");
    }

    @RequestMapping("pet-move-feature")
    public RedirectView moveFeature(int id, int targetId, String direction,
                                    @ModelAttribute(ESTIMATES_PROJECT_ATTR) EstimatesProject project)
        throws IOException {

        boolean after = "down".equals(direction);

        project.getKanbanProject().move(id, targetId, after);
        project.getKanbanProject().save();

        return new RedirectView("estimates");
    }

    @RequestMapping(value = "estimates-cost-daily-save", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public JsonStatus saveDailyCosts(@ModelAttribute(ESTIMATES_PROJECT_ATTR) EstimatesProject project,
                                     @RequestBody Data[] data) throws IOException {

        Map<LocalDate, Integer> dailyCosts = new TreeMap<LocalDate, Integer>();

        for (Data d : data) {
            if (StringUtils.isNotEmpty(d.cost) && StringUtils.isNotEmpty(d.date)) {
                try {
                    LocalDate date = LocalDate.parse(d.date);
                    Integer cost = Integer.parseInt(d.cost);
                    dailyCosts.put(date, cost);
                } catch (Exception e) {
                    logger.warn("Error parsing data for daily costs date = {}, cost = {}, skipping this value", d.date,
                        d.cost);
                    logger.warn("Got exception", e);
                }
            }
        }

        project.setDayCosts(dailyCosts);
        estimatesDao.storeProjectEstimates(project);

        return JsonStatus.createOkStatus();
    }

    public static class Data {

        public String date;
        public String cost;

        public Data(String date, String cost) {
            this.date = date;
            this.cost = cost;
        }

        public Data() {}
    }
}
