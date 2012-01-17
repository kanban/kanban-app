package com.metservice.kanban.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    private static final String PET_PROJECT_ATTR = "petproject";
    private static final String KANBAN_PROJECT_ATTR = "project";
    private static final String PET_PROJECT_JSP = "petproject.jsp";
    private static final String PET_FEATURE_JSP = "petfeature.jsp";

    @Autowired
    EstimatesDao petDao;

    @Autowired
    KanbanService kanbanService;

    @ModelAttribute(PET_PROJECT_ATTR)
    public synchronized EstimatesProject populatePetProject(@PathVariable("projectName") String projectName) throws IOException {
        EstimatesProject project = petDao.loadProject(projectName);

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

    @RequestMapping("pet-project")
    public ModelAndView showProject(@ModelAttribute("petproject") EstimatesProject petProject) {

        Map<String, Object> model = new HashMap<String, Object>();

        return new ModelAndView(PET_PROJECT_JSP, model);
    }

    @RequestMapping("pet-set-project-property")
    public RedirectView setBudget(String name, int value, @ModelAttribute(PET_PROJECT_ATTR) EstimatesProject project)
        throws IOException {

        if ("budget".equals(name)) {
            project.setBudget(value);
        } else if ("costSoFar".equals(name)) {
            project.setCostSoFar(value);
        } else if ("estimatedCostPerPoint".equals(name)) {
            project.setEstimatedCostPerPoint(value);
        } else {
            throw new IllegalArgumentException("name = " + name);
        }
        petDao.storeProjectEstimates(project);
        return new RedirectView("pet-project");
    }

    @RequestMapping("pet-edit-feature")
    public ModelAndView editFeature(int id, @ModelAttribute(PET_PROJECT_ATTR) EstimatesProject project) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("pageTitle", "Edit feature");
        model.put("feature", project.getFeature(id));

        return new ModelAndView(PET_FEATURE_JSP, model);
    }

    @RequestMapping("pet-save-feature")
    public RedirectView saveFeature(int id, int averageCaseEstimate, int worstCaseEstimate,
                                    @ModelAttribute(PET_PROJECT_ATTR) EstimatesProject project)
        throws IOException {

        Assert.isTrue(id != 0);

        // get WI for feature
        WorkItem workItem = project.getKanbanProject().getWorkItemById(id);
        // update WI from feature
        workItem.setAverageCaseEstimate(averageCaseEstimate);
        workItem.setWorstCaseEstimate(worstCaseEstimate);

        petDao.storeUpdatedFeatures(project);

        return new RedirectView("pet-project");
    }

    @RequestMapping("pet-set-feature-included-in-estimates")
    public RedirectView excludeFeature(int id, boolean value, @ModelAttribute(PET_PROJECT_ATTR) EstimatesProject project)
        throws IOException {

        boolean includedInEstimates = value;

        WorkItem feature = project.getKanbanProject().getWorkItemById(id);
        feature.setMustHave(includedInEstimates);

        petDao.storeUpdatedFeatures(project);

        return new RedirectView("pet-project");
    }

    @RequestMapping("pet-move-feature")
    public RedirectView moveFeature(int id, int targetId, String direction, @ModelAttribute(PET_PROJECT_ATTR) EstimatesProject project)
        throws IOException {

        boolean after = "down".equals(direction);

        project.getKanbanProject().move(id, targetId, after);
        project.getKanbanProject().save();

        return new RedirectView("pet-project");
    }

}
