package com.metservice.kanban.web;

import static com.metservice.kanban.model.WorkItem.ROOT_WORK_ITEM_ID;
import static com.metservice.kanban.utils.DateUtils.currentLocalDate;
import static com.metservice.kanban.utils.DateUtils.parseConventionalNewZealandDate;
import static java.lang.Integer.parseInt;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.charts.burnup.BurnUpChartGenerator;
import com.metservice.kanban.charts.burnup.DefaultBurnUpChartGenerator;
import com.metservice.kanban.charts.burnup.DefaultChartWriter;
import com.metservice.kanban.charts.cumulativeflow.CumulativeFlowChartBuilder;
import com.metservice.kanban.charts.cycletime.CycleTimeChartBuilder;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.utils.DateUtils;
import java.util.Collections;
import org.apache.commons.collections.CollectionUtils;

//TODO This class needs unit tests.

@Controller
@RequestMapping("{projectName}/{board}")
public class KanbanBoardController {

    private KanbanService kanbanService;

    public KanbanBoardController() {
        this.kanbanService = new KanbanService();
    }

    public KanbanBoardController(KanbanService kanbanService) {
        this.kanbanService = kanbanService;
    }

    @ModelAttribute("project")
    public synchronized KanbanProject populateProject(
            @PathVariable("projectName") String projectName) throws IOException {
        return kanbanService.getKanbanProject(projectName);
    }

    @ModelAttribute("redirectView")
    public synchronized RedirectView populateRedirectView(
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String board) {
        return new RedirectView("/projects/" + projectName + "/" + board, true);
    }

    @ModelAttribute("chartGenerator")
    public synchronized BurnUpChartGenerator populateChartGenerator() {
        return new DefaultBurnUpChartGenerator(new DefaultChartWriter());
    }

    // TODO get projectName from project throughout this class
    @RequestMapping("")
    public synchronized ModelAndView board(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String boardType) throws IOException {

        String scrollTop = extractScrollPositionInfoFromBoardType(boardType);
        boardType = cleanBoardType(boardType);

        Map<String, Object> model = buildModel(projectName, boardType);

        // TODO model used to have kanbanTransaction now it has kanban... need to fix view

        model.put("scrollTop", scrollTop == null ? "0" : scrollTop);

        if (boardType.equals("backlog")) {
            model.put("kanbanBacklog", project.getBacklog());
            model.put("type", project.getWorkItemTypes().getRoot().getValue());
            model.put("phase", project.getWorkItemTypes().getRoot().getValue().getPhases().get(0));
            return new ModelAndView("/backlog.jsp", model);
        } else if (boardType.equals("completed")) {
            model.put("type", project.getWorkItemTypes().getRoot().getValue());
            List<String> phases = project.getWorkItemTypes().getRoot().getValue().getPhases();
            model.put("phase", phases.get(phases.size() - 1));
            return new ModelAndView("/completed.jsp", model);
        }
        return new ModelAndView("/project.jsp", model);
    }

    private String cleanBoardType(String boardType) {
        int sep = boardType.indexOf(":");
        if (sep > -1) {
            return boardType.substring(0, sep);
        }
        return boardType;
    }

    private String extractScrollPositionInfoFromBoardType(String boardType) {
        int sep = boardType.indexOf(":");
        if (sep > -1) {
            return boardType.substring(sep + 1);
        }
        return null;
    }

    @RequestMapping(value = "advance-item-action", method = RequestMethod.POST)
    public synchronized RedirectView advanceItemAction(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("board") String boardType,
            @RequestParam("id") String id,
            @RequestParam("scrollTop") String scrollTop) throws IOException {

        project.advance(parseInt(id), currentLocalDate());
        project.save();

        return new RedirectView(includeScrollTopPosition(boardType, scrollTop));
    }

    private String includeScrollTopPosition(String boardType, String scrollTop) {
        return "../" + boardType + ":" + scrollTop;
    }

    @RequestMapping("add-item")
    public synchronized ModelAndView addItem(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String boardType,
            @RequestParam("id") int id)
        throws IOException {

        WorkItem parent = project.getWorkItemTree().getWorkItem(id);
        Map<String, Object> model = buildModel(projectName, boardType);

        String type = project.getWorkItemTypes().getRoot().getValue().getName();
        String parentName = "";
        int parentId = ROOT_WORK_ITEM_ID;
        String legend = "Add " + type;
        if (parent != null) {
            parentName = parent.getName();
            parentId = parent.getId();
            type = project.getWorkItemTypes().getTreeNode(parent.getType()).getChild(0).getValue().getName();
            legend = "Add a " + type + " to " + parentName;
        }

        model.put("workItem", parent);
        model.put("type", type);
        model.put("legend", legend);
        model.put("parentId", parentId);

        return new ModelAndView("/add.jsp", model);
    }

    @RequestMapping("edit-item")
    public synchronized ModelAndView editItem(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String boardType,
            @RequestParam("id") Integer id)
        throws IOException {

        Map<String, Object> model = buildModel(projectName, boardType);
        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);
        model.put("workItem", workItem);
        model.put("children", project.getWorkItemTree().getChildren(id));
        model.put("parentAlternativesList", project.getWorkItemTree().getParentAlternatives(workItem));

        Map<String, String> map = new LinkedHashMap<String, String>();
        for (String phase : workItem.getType().getPhases()) {
            LocalDate date = workItem.getDate(phase);

            String dateString = "";
            if (date != null) {
                dateString = DateUtils.formatConventionalNewZealandDate(date);
            }
            map.put(phase, dateString);
        }
        model.put("phasesMap", map);

        return new ModelAndView("/edit.jsp", model);
    }

    @RequestMapping("add-item-action")
    public synchronized RedirectView addItemAction(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String boardType,
            @RequestParam("parentId") String parentId,
            @RequestParam("type") String type,
            @RequestParam("name") String name,
            @RequestParam("size") Integer size,
            @RequestParam("importance") Integer importance,
            @RequestParam("notes") String notes) throws IOException {

        int parentIdAsInteger = Integer.parseInt(parentId);
        WorkItemType typeAsWorkItemType = project.getWorkItemTypes().getByName(type);

        if (size == null) {
            size = 0;
        }
        if (importance == null) {
            importance = 0;
        }
        project.addWorkItem(parentIdAsInteger, typeAsWorkItemType, name, size, importance, notes, currentLocalDate());
        project.save();
        return new RedirectView("../" + boardType);
    }

    @RequestMapping(value = "print-items", method = RequestMethod.POST)
    public synchronized ModelAndView printItems(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String boardType,
            @RequestParam("printSelection") String[] ids)
        throws IOException {

        Map<String, Object> model = buildModel(projectName, boardType);
        model.put("ids", ids);

        return new ModelAndView("/printCards.jsp", model);
    }

    @RequestMapping("move-item-action")
    public synchronized RedirectView moveItemAction(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("board") String boardType,
            @RequestParam("id") String id,
            @RequestParam("targetId") String targetId,
            @RequestParam("after") Boolean after,
            @RequestParam("scrollTop") String scrollTop) throws IOException {
        int idAsInteger = parseInt(id);
        int targetIdAsInteger = parseInt(targetId);
        project.move(idAsInteger, targetIdAsInteger, after);
        project.save();
        return new RedirectView(includeScrollTopPosition(boardType, scrollTop));
    }

    @RequestMapping("reorder")
    public synchronized RedirectView reorder(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("board") String boardType,
            @RequestParam("id") Integer id,
            @RequestParam("ids") Integer[] ids,
            @RequestParam("scrollTop") String scrollTop) throws IOException {

        project.reorder(id, ids);
        project.save();

        return new RedirectView(includeScrollTopPosition(boardType, scrollTop));

    }

    @RequestMapping("edit-item-action")
    public synchronized RedirectView editItemAction(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("board") String boardType,
            @RequestParam("id") int id,
            @RequestParam("parentId") int parentId,
            @RequestParam("name") String name,
            @RequestParam("size") Integer size,
            @RequestParam("importance") Integer importance,
            @RequestParam("notes") String notes,
            @RequestParam(required = false, value = "excluded") boolean excluded,
            HttpServletRequest request)
        throws IOException, ParseException {

        @SuppressWarnings("unchecked")
        Map<String, String[]> parameters = request.getParameterMap();

        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);
        workItem.setName(name);
        workItem.setSize(size == null ? 0 : size);
        workItem.setImportance(importance == null ? 0 : importance);
        workItem.setNotes(notes);
        workItem.setExcluded(excluded);

        for (String phase : workItem.getType().getPhases()) {
            String key = "date-" + phase;
            String[] valueArray = parameters.get(key);
            if (valueArray == null || valueArray[0].trim().isEmpty()) {
                workItem.setDate(phase, null);
            } else {
                workItem.setDate(phase, parseConventionalNewZealandDate(valueArray[0]));
            }
        }

        if (workItem.getParentId() != parentId) {
            project.getWorkItemTree().reparent(id, parentId);
        }

        project.save();

        return new RedirectView("../" + boardType);
    }

    @RequestMapping("delete-item-action")
    public synchronized View deleteWorkItem(
            @ModelAttribute("project") KanbanProject project,
            @RequestParam("id") int id,
            @ModelAttribute("redirectView") View nextView) throws IOException {

        project.deleteWorkItem(id);
        project.save();
        return nextView;
    }

    @RequestMapping("chart")
    public synchronized ModelAndView chart(
            @RequestParam("chartName") String chartName,
            @RequestParam("workItemTypeName") String workItemTypeName,
            @PathVariable("projectName") String projectName) {

        ModelAndView modelAndView = new ModelAndView("/chart.jsp");
        modelAndView.addObject("workItemTypeName", workItemTypeName);
        modelAndView.addObject("imageName", chartName + ".png");
        modelAndView.addObject("projectName", projectName);
        return modelAndView;
    }

    // TODO check in this class for redundent model.put("kanban...

    @RequestMapping("cumulative-flow-chart.png")
    public synchronized void cumulativeFlowChartPng(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("board") String boardType,
            @RequestParam("level") String level,
            OutputStream outputStream) throws IOException {

        WorkItemType type = project.getWorkItemTypes().getByName(level);
        List<WorkItem> workItemList = project.getWorkItemTree().getWorkItemsOfType(type);

        CumulativeFlowChartBuilder builder = new CumulativeFlowChartBuilder();

        CategoryDataset dataset = builder.createDataset(type.getPhases(), workItemList);
        JFreeChart chart = builder.createChart(dataset);
        int width = dataset.getColumnCount() * 15;
        ChartUtilities.writeChartAsPNG(outputStream, chart, width < 800 ? 800 : width, 600);
    }

    @RequestMapping("cycle-time-chart.png")
    public synchronized void cycleTimeChartPng(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("board") String boardType,
            @RequestParam("level") String level,
            OutputStream outputStream) throws IOException {

        WorkItemType type = project.getWorkItemTypes().getByName(level);
        CycleTimeChartBuilder builder = new CycleTimeChartBuilder();
        List<WorkItem> workItemList = project.getWorkItemTree().getWorkItemsOfType(type);
        CategoryDataset dataset = builder.createDataset(builder.getCompletedWorkItemsInOrderOfCompletion(workItemList));
        JFreeChart chart = builder.createChart(dataset);
        int width = dataset.getColumnCount() * 15;
        ChartUtilities.writeChartAsPNG(outputStream, chart, width < 800 ? 800 : width, 600);
    }

    @RequestMapping("edit-project")
    public synchronized ModelAndView editProject(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String boardType,
            @RequestParam("createNewProject") boolean createNewProject) throws IOException {

        Map<String, Object> model = buildModel(projectName, boardType);
        model.put("settings", kanbanService.getProjectConfiguration(projectName).
            getKanbanPropertiesFile().getContentAsString());

        return new ModelAndView("/createProject.jsp", model);

    }

    @RequestMapping("create-project-action")
    public synchronized RedirectView createProjectAction(
            @ModelAttribute("project") KanbanProject project,
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String boardType,
            @RequestParam("newProjectName") String newProjectName,
            @RequestParam("content") String content) throws IOException {

        kanbanService.createProject(newProjectName, content);
        return openProject(projectName, boardType, newProjectName);
    }

    @RequestMapping("open-project")
    public synchronized RedirectView openProject(
            @PathVariable("projectName") String projectName,
            @PathVariable("board") String boardType,
            @RequestParam("newProjectName") String newProjectName) throws IOException {

        return new RedirectView("/projects/" + newProjectName + "/" + boardType, true);
    }

    private Map<String, Object> buildModel(String projectName, String boardType) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("projectName", projectName);
        model.put("boardType", boardType);
        return model;
    }

    @RequestMapping("burn-up-chart.png")
    public void burnUpChartPng(
            @ModelAttribute("project") KanbanProject project,
            @ModelAttribute("chartGenerator") BurnUpChartGenerator chartGenerator,
            OutputStream outputStream) throws IOException {

        WorkItemTree tree = project.getWorkItemTree();
        WorkItemType type = project.getWorkItemTypes().getRoot().getValue();
        List<WorkItem> topLevelWorkItems = tree.getWorkItemsOfType(type);

        chartGenerator.generateBurnUpChart(type, topLevelWorkItems, currentLocalDate(), outputStream);
    }
}
