package com.metservice.pet;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/{projectName}")
public class ProjectEstimationToolController {

    private static Project project = DemoProjectFactory.createDemoProject();

    @RequestMapping("project")
    public ModelAndView showProject() {
        return new ModelAndView("/project.jsp", "project", project);
    }

    @RequestMapping("set-project-property")
    public RedirectView setBudget(String name, int value) {
        if (name.equals("budget")) {
            project.setBudget(value);
        } else if (name.equals("costSoFar")) {
            project.setCostSoFar(value);
        } else if (name.equals("estimatedCostPerPoint")) {
            project.setEstimatedCostPerPoint(value);
        } else {
            throw new IllegalArgumentException("name = " + name);
        }
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
    public ModelAndView editFeature(int id) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("pageTitle", "Edit feature");
        model.put("feature", project.getFeature(id));

        return new ModelAndView("/feature.jsp", model);
    }

    @RequestMapping("save-feature")
    public RedirectView saveFeature(int id, Feature feature) {
        if (feature.getId() == Feature.BLANK_ID) {
            project.addFeature(feature);
        } else {
            project.setFeature(id, feature);
        }
        return new RedirectView("project");
    }

    @RequestMapping("set-feature-included-in-estimates")
    public RedirectView excludeFeature(int id, boolean value) {
        boolean includedInEstimates = value;
        if (includedInEstimates) {
            project.includeFeature(id);
        } else {
            project.excludeFeature(id);
        }
        return new RedirectView("project");
    }

    @RequestMapping("move-feature")
    public RedirectView moveFeature(int id, String direction) {
        if (direction.equals("up")) {
            project.moveFeatureUp(id);
        } else if (direction.equals("down")) {
            project.moveFeatureDown(id);
        } else {
            throw new IllegalArgumentException("direction = " + direction);
        }
        return new RedirectView("project");
    }

    @RequestMapping("complete-feature")
    public RedirectView completeFeature(int id) {
        project.completeFeature(id);
        return new RedirectView("project");
    }

    @RequestMapping("reset-demo")
    public RedirectView resetDemo() {
        project = DemoProjectFactory.createDemoProject();
        return new RedirectView("project");
    }

    @RequestMapping("/")
    public RedirectView root() {
        return new RedirectView("project");
    }
}
