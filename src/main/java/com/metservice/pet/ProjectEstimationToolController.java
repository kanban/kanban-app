package com.metservice.pet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.model.WorkItem;

@Controller
@RequestMapping("/{projectName}")
public class ProjectEstimationToolController {

    private PetDao petDao;

    @Autowired
    public void setPetDao(PetDao petDao) {
        this.petDao = petDao;
    }

    @ModelAttribute("project")
    public synchronized Project populateProject(@PathVariable("projectName") String projectName) throws IOException {
        Project project = petDao.loadProject(projectName);

        return project;
    }

    @RequestMapping("project")
    public ModelAndView showProject(@ModelAttribute("project") Project project) {
        return new ModelAndView("/project.jsp", "project", project);
    }

    @RequestMapping("set-project-property")
    public RedirectView setBudget(String name, int value, @ModelAttribute("project") Project project)
        throws IOException {
        if (name.equals("budget")) {
            project.setBudget(value);
        } else if (name.equals("costSoFar")) {
            project.setCostSoFar(value);
        } else if (name.equals("estimatedCostPerPoint")) {
            project.setEstimatedCostPerPoint(value);
        } else {
            throw new IllegalArgumentException("name = " + name);
        }
        petDao.storeProjectEstimates(project);
        return new RedirectView("project");
    }

    @RequestMapping("add-feature")
    public ModelAndView addFeature() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("pageTitle", "Add feature");
        model.put("feature", new Feature());

        return new ModelAndView("/feature.jsp", model);
    }

    @RequestMapping("edit-feature")
    public ModelAndView editFeature(int id, @ModelAttribute("project") Project project) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("pageTitle", "Edit feature");
        model.put("feature", project.getFeature(id));

        return new ModelAndView("/feature.jsp", model);
    }

    @RequestMapping("save-feature")
    public RedirectView saveFeature(int id, Feature feature, @ModelAttribute("project") Project project)
        throws IOException {

        assert feature.getId() != Feature.BLANK_ID;

        project.setFeature(id, feature);

        // get WI for feature
        WorkItem workItem = project.getKanbanProject().getWorkItemTree().getWorkItem(feature.getId());
        // update WI from feature
        workItem.setBestCaseEstimate(feature.getBestCaseEstimate());
        workItem.setWorstCaseEstimate(feature.getWorstCaseEstimate());

        petDao.storeUpdatedFeatures(project);

        return new RedirectView("project");
    }

    @RequestMapping("set-feature-included-in-estimates")
    public RedirectView excludeFeature(int id, boolean value, @ModelAttribute("project") Project project)
        throws IOException {
        boolean includedInEstimates = value;

        Feature feature = project.getFeature(id);
        feature.setMustHave(includedInEstimates);
        WorkItem workItem = project.getKanbanProject().getWorkItemTree().getWorkItem(feature.getId());
        workItem.setMustHave(includedInEstimates);
        petDao.storeUpdatedFeatures(project);

        return new RedirectView("project");
    }

    @RequestMapping("move-feature")
    public RedirectView moveFeature(int id, int targetId, String direction, @ModelAttribute("project") Project project)
        throws IOException {

        boolean after = "down".equals(direction);

        project.getKanbanProject().move(id, targetId, after);
        project.getKanbanProject().save();

        return new RedirectView("project");
    }

    @RequestMapping("complete-feature")
    public RedirectView completeFeature(int id, @ModelAttribute("project") Project project) {
        project.completeFeature(id);
        return new RedirectView("project");
    }

    @RequestMapping("/")
    public RedirectView root() {
        return new RedirectView("project");
    }
}
