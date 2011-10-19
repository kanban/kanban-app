package com.metservice.kanban.web;

import static com.metservice.kanban.model.WorkItem.ROOT_WORK_ITEM_ID;
import static com.metservice.kanban.utils.DateUtils.currentLocalDate;
import static com.metservice.kanban.utils.DateUtils.parseConventionalNewZealandDate;
import static java.lang.Integer.parseInt;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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

//TODO This class needs unit tests.

/**
 * @author Nicholas Malcolm - malcolnich - 300170288
 */
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
                                                      @PathVariable("projectName") String projectName)
        throws IOException {
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

        // TODO model used to have kanbanTransaction now it has kanban... need
        // to fix view

        model.put("scrollTop", scrollTop == null ? "0" : scrollTop);

        if (boardType.equals("backlog")) {
            model.put("kanbanBacklog", project.getBacklog());
            model.put("type", project.getWorkItemTypes().getRoot().getValue());
            model.put("phase", project.getWorkItemTypes().getRoot().getValue()
                .getPhases().get(0));
            return new ModelAndView("/backlog.jsp", model);
        } else if (boardType.equals("completed")) {
            model.put("type", project.getWorkItemTypes().getRoot().getValue());
            List<String> phases = project.getWorkItemTypes().getRoot()
                .getValue().getPhases();
            model.put("phase", phases.get(phases.size() - 1));
            return new ModelAndView("/completed.jsp", model);
        }
        else if (boardType.equals("journal")) {
            model.put("kanbanJournal", project.getJournalText());
            return new ModelAndView("/journal.jsp", model);
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
                                                       @RequestParam("id") String id) throws IOException {

        project.advance(parseInt(id), currentLocalDate());
        project.save();

        return new RedirectView("../" + boardType);
    }

    @RequestMapping(value = "stop-item-action", method = RequestMethod.POST)
    public synchronized RedirectView stopItemAction(
                                                    @ModelAttribute("project") KanbanProject project,
                                                    @PathVariable("board") String boardType,
                                                    @RequestParam("id") String id) throws IOException {

        project.stop(parseInt(id));
        project.save();

        // Redirect
        return new RedirectView("../" + boardType);

        //return new RedirectView(includeScrollTopPosition(boardType, scrollTop));
    }

    private String includeScrollTopPosition(String boardType, String scrollTop) {
        return "../" + boardType + ":" + scrollTop;
    }

    /** Creates empty item model to display in add form with preset parent id. **/
    @RequestMapping("add-item")
    public synchronized ModelAndView addItem(
                                             @ModelAttribute("project") KanbanProject project,
                                             @PathVariable("projectName") String projectName,
                                             @PathVariable("board") String boardType, @RequestParam("id") int id)
        throws IOException {

        // Search for parent id
        WorkItem parent = project.getWorkItemTree().getWorkItem(id);
        Map<String, Object> model = buildModel(projectName, boardType);

        // Get name of project
        String type = project.getWorkItemTypes().getRoot().getValue().getName();

        // Set defaults for new item
        String parentName = "";
        int parentId = ROOT_WORK_ITEM_ID;
        String legend = "Add " + type;

        // Change defaults if we have a parent item
        if (parent != null) {
            parentName = parent.getName();
            parentId = parent.getId();
            type = project.getWorkItemTypes().getTreeNode(parent.getType())
                .getChild(0).getValue().getName();
            legend = "Add a " + type + " to " + parentName;
        }

        // Pass defaults to the model hash
        model.put("workItem", parent);
        model.put("type", type);
        model.put("legend", legend);
        model.put("parentId", parentId);

        // Render the add form, passing the model with its hash values.
        return new ModelAndView("/add.jsp", model);
    }

    /**
     * Responds to edit-item request, passes the item in its current state to
     * the edit form
     * 
     * @param project
     * @param projectName
     * @param boardType
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping("edit-item")
    public synchronized ModelAndView editItem(
                                              @ModelAttribute("project") KanbanProject project,
                                              @PathVariable("projectName") String projectName,
                                              @PathVariable("board") String boardType,
                                              @RequestParam("id") Integer id) throws IOException {

        // Get a model ready to take some attributes
        Map<String, Object> model = buildModel(projectName, boardType);

        // Fetch the item we want to edit
        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);

        // Add some variables to the model hashmap
        model.put("workItem", workItem);
        model.put("children", project.getWorkItemTree().getChildren(id));
        model.put("parentAlternativesList", project.getWorkItemTree()
            .getParentAlternatives(workItem));

        // TODO: Figure out what this does
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

        // Pass the model to edit.jsp
        return new ModelAndView("/edit.jsp", model);
    }

    /**
     * Add item to Kanban project Needs a bunch of parameters in the request.
     * (Comes from add.jsp)
     **/
    @RequestMapping("add-item-action")
    public synchronized RedirectView addItemAction(
                                                   @ModelAttribute("project") KanbanProject project,
                                                   @PathVariable("projectName") String projectName,
                                                   @PathVariable("board") String boardType,
                                                   @RequestParam("name") String name,
                                                   HttpServletRequest request) throws IOException {

        //Default parentID to 0
        String temp = request.getParameter("parentId");
        int parentId = temp == null ? 0 : Integer.parseInt(temp);

        temp = request.getParameter("size");
        //size defaults to 0
        int size = (temp == null ? 0 : (temp.equals("") ? 0 : Integer.parseInt(temp)));

        temp = request.getParameter("importance");
        int importance = (temp == null ? 0 : (temp.equals("") ? 0 : Integer.parseInt(temp)));

        String notes = request.getParameter("notes");

        String color = request.getParameter("color");

        String workItemType = request.getParameter("type");

        WorkItemType typeAsWorkItemType = project.getWorkItemTypes().getByName(
            workItemType);

        // Add it and save it
        project.addWorkItem(parentId, typeAsWorkItemType, name, size,
            importance, notes, color, currentLocalDate());
        project.save();

        // Redirect
        return new RedirectView("../" + boardType);
    }

    @RequestMapping(value = "print-items", method = RequestMethod.POST)
    public synchronized ModelAndView printItems(@ModelAttribute("project") KanbanProject project,
                                                @PathVariable("projectName") String projectName,
                                                @PathVariable("board") String boardType,
                                                @RequestParam("printSelection") String[] ids) throws IOException {

        Map<String, Object> model = buildModel(projectName, boardType);
        model.put("ids", ids);

        return new ModelAndView("/printCards.jsp", model);
    }

    @RequestMapping("move-item-action")
    public synchronized RedirectView moveItemAction(@ModelAttribute("project") KanbanProject project,
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
                                             @RequestParam("id") Integer id, @RequestParam("ids") Integer[] ids,
                                             @RequestParam("scrollTop") String scrollTop) throws IOException {

        project.reorder(id, ids);
        project.save();

        return new RedirectView(includeScrollTopPosition(boardType, scrollTop));

    }

    /**
     * Responds to a form submission which passes an edited item
     * 
     * @param project
     * @param boardType
     * @return
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping("edit-item-action")
    public synchronized RedirectView editItemAction(
                                                    @ModelAttribute("project") KanbanProject project,
                                                    @PathVariable("board") String boardType,
                                                    @RequestParam("id") int id,
                                                    HttpServletRequest request) throws IOException, ParseException {

        @SuppressWarnings("unchecked")
        // Get the item which is being edited
        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);

        Map<String, String[]> parameters = request.getParameterMap();

        String temp = request.getParameter("parentId");
        int parentId = temp == null ? workItem.getParentId() : Integer.parseInt(temp);

        temp = request.getParameter("name");
        String name = temp == null ? workItem.getName() : request.getParameter("name");

        temp = request.getParameter("size");
        temp = (temp == null ? workItem.getSize() + "" : (temp.equals("") ? "0" : temp));
        int size = Integer.parseInt(temp);

        temp = request.getParameter("importance");
        temp = (temp == null ? workItem.getImportance() + "" : (temp.equals("") ? "0" : temp));
        int importance = Integer.parseInt(temp);

        temp = request.getParameter("notes");
        String notes = temp == null ? workItem.getNotes() : request.getParameter("notes");

        temp = request.getParameter("color");
        String color = temp == null ? workItem.getColour().toString() : request.getParameter("color");

        temp = request.getParameter("excluded");
        //		boolean excluded = temp == null ? workItem.isExcluded() : temp.equals("on");
        boolean excluded = temp == null ? false : temp.equals("on");

        // Save all the updates
        workItem.setName(name);
        workItem.setSize(size);
        workItem.setImportance(importance);
        workItem.setNotes(notes);
        workItem.setExcluded(excluded);
        workItem.setColour(color);

        // TODO Figure this out
        for (String phase : workItem.getType().getPhases()) {
            String key = "date-" + phase;
            String[] valueArray = parameters.get(key);
            if (valueArray != null) {
                if (valueArray[0].trim().isEmpty()) {
                    workItem.setDate(phase, null);
                } else {
                    workItem.setDate(phase,
                        parseConventionalNewZealandDate(valueArray[0]));
                }
            }

        }

        // If it's changed parent, reset the parent.
        if (workItem.getParentId() != parentId) {
            project.getWorkItemTree().reparent(id, parentId);
        }

        // Save the whole project
        project.save();

        // Go home.
        return new RedirectView("../" + boardType);
    }

    @RequestMapping("edit-journal-action")
    public synchronized RedirectView editJournalAction(
                                                       @ModelAttribute("project") KanbanProject project,
                                                       @PathVariable("board") String boardType,
                                                       @RequestParam("journalText") String journalText,
                                                       HttpServletRequest request) throws IOException, ParseException {

        project.writeJournalText(journalText);
        return new RedirectView("../" + boardType);

    }

    /**
     * Responds to a request to delete an item
     * 
     * @param project
     * @param id
     *            - the id of the item you want to delete
     * @param nextView
     *            - the view you want to redirect to
     * @return
     * @throws IOException
     */
    @RequestMapping("delete-item-action")
    public synchronized View deleteWorkItem(
                                            @ModelAttribute("project") KanbanProject project,
                                            @RequestParam("id") int id,
                                            @ModelAttribute("redirectView") View nextView) throws IOException {

        // Delete the workitem, save and redirect.
        project.deleteWorkItem(id);
        project.save();
        return nextView;
    }

    @RequestMapping("chart")
    public synchronized ModelAndView chart(
                                           @ModelAttribute("project") KanbanProject project,
                                           @RequestParam("chartName") String chartName,
                                           @RequestParam("workItemTypeName") String workItemTypeName,
                                           @PathVariable("projectName") String projectName,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {

        ModelAndView modelAndView = new ModelAndView("/chart.jsp");
        modelAndView.addObject("workItemTypeName", workItemTypeName);
        modelAndView.addObject("imageName", chartName + ".png");
        modelAndView.addObject("projectName", projectName);
        modelAndView.addObject("startDate", startDate);
        modelAndView.addObject("endDate", endDate);
        modelAndView.addObject("kanbanJournal", project.getJournalText());
        return modelAndView;
    }

    // TODO check in this class for redundent model.put("kanban...

    @RequestMapping("cumulative-flow-chart.png")
    public synchronized void cumulativeFlowChartPng(
                                                    @ModelAttribute("project") KanbanProject project,
                                                    @PathVariable("board") String boardType,
                                                    @RequestParam("startDate") String startDate,
                                                    @RequestParam("endDate") String endDate,
                                                    @RequestParam("level") String level,
                                                    OutputStream outputStream)
        throws IOException {

        WorkItemType type = project.getWorkItemTypes().getByName(level);
        List<WorkItem> workItemList = project.getWorkItemTree()
            .getWorkItemsOfType(type);

        LocalDate start = null;
        LocalDate end = null;

        try {
            start = LocalDate.fromDateFields(DateFormat.getDateInstance().parse(startDate));
        } catch (ParseException e) {
            // keep start as null
        }
        try {
            end = LocalDate.fromDateFields(DateFormat.getDateInstance().parse(endDate));
        } catch (ParseException e) {
            // keep end as null
        }

        // add start and end date params here
        CumulativeFlowChartBuilder builder = new CumulativeFlowChartBuilder(start, end);

        CategoryDataset dataset = builder.createDataset(type.getPhases(),
            workItemList);
        JFreeChart chart = builder.createChart(dataset);
        int width = dataset.getColumnCount() * 15;
        ChartUtilities.writeChartAsPNG(outputStream, chart, width < 800 ? 800 : width, 600);
    }

    @RequestMapping("cycle-time-chart.png")
    public synchronized void cycleTimeChartPng(@ModelAttribute("project") KanbanProject project,
                                               @PathVariable("board") String boardType,
                                               @RequestParam("startDate") String startDate,
                                               @RequestParam("endDate") String endDate,
                                               @RequestParam("level") String level, OutputStream outputStream)
        throws IOException {

        WorkItemType type = project.getWorkItemTypes().getByName(level);
        CycleTimeChartBuilder builder = new CycleTimeChartBuilder();
        List<WorkItem> workItemList = project.getWorkItemTree()
            .getWorkItemsOfType(type);

        CategoryDataset dataset = builder.createDataset(builder
            .getCompletedWorkItemsInOrderOfCompletion(workItemList));
        JFreeChart chart = builder.createChart(dataset);
        int width = dataset.getColumnCount() * 15;
        ChartUtilities.writeChartAsPNG(outputStream, chart, width < 800 ? 800 : width, 600);
    }

    /**
     * Provides the variables of the current project to either a new project
     * form,
     * or to the edit project page, depending on createNewProject's boolean
     * value.
     * 
     * @param project
     * @param projectName
     * @param boardType
     * @param createNewProject
     *            - whether we want this to create a new project.
     * @return
     * @throws IOException
     */
    @RequestMapping("edit-project")
    public synchronized ModelAndView editProject(
                                                 @ModelAttribute("project") KanbanProject project,
                                                 @PathVariable("projectName") String projectName,
                                                 @PathVariable("board") String boardType,
                                                 @RequestParam("createNewProject") boolean createNewProject)
        throws IOException {

        Map<String, Object> model = buildModel(projectName, boardType);

        // Get the settings of this current project and pass it to the form.
        model.put("settings", kanbanService
            .getProjectConfiguration(projectName).getKanbanPropertiesFile()
            .getContentAsString());
        // Create a new project if true
        if (createNewProject) {
            return new ModelAndView("/createProject.jsp", model);
        }
        // else edit the current project
        else {
            return new ModelAndView("/editProject.jsp", model);
        }
    }

    @RequestMapping("edit-project-action")
    public synchronized RedirectView editProjectAction(
                                                       @ModelAttribute("project") KanbanProject project,
                                                       @PathVariable("projectName") String projectName,
                                                       @PathVariable("board") String boardType,
                                                       @RequestParam("newProjectName") String editProjectName,
                                                       @RequestParam("content") String content) throws IOException {

        kanbanService.editProject(editProjectName, content);
        return openProject(projectName, boardType, editProjectName);
    }

    @RequestMapping("edit-wiplimit-action")
    public synchronized ModelAndView editWIPLimitAction(
                                                        @ModelAttribute("project") KanbanProject project,
                                                        @PathVariable("projectName") String projectName,
                                                        @RequestParam("columnName") String columnName,
                                                        @RequestParam("columnType") String columnType,
                                                        @RequestParam("wipLimit") String wipLimit) throws IOException,
        ParseException {

        @SuppressWarnings("unchecked")
        String content = kanbanService
            .getProjectConfiguration(projectName).getKanbanPropertiesFile()
            .getContentAsString();

        Scanner sc = new Scanner(content);
        String temp = "";
        String newContent = "";

        //Keep track of how many columns there are
        int totalColumns = 0;
        //This is set once, 
        int insertAfterComma = -1;

        String wipLine = "workItemTypes." + columnType + ".wipLimit=";

        while (sc.hasNext()) {

            temp = sc.nextLine();
            //Find out where the new wipLimit should go
            if (temp.contains("workItemTypes." + columnType + ".phases=")) {
                String columns = temp.split("=")[1];
                for (String column : columns.split(",")) {
                    if (column.equals(columnName)) {
                        insertAfterComma = totalColumns;
                    }

                    totalColumns++;

                }
            }
            if (temp.contains(wipLine)) {
                String wipLimits = temp.split("=")[1];
                String[] limits = wipLimits.split(",");
                //limts.count should == totalColumns
                String newLimits = "";

                String limit = "";
                for (int i = 0; i < totalColumns; i++) {
                    //Get the old limit
                    try {
                        limit = limits[i];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        //Set default if no limit
                        limit = "-1";
                    }
                    //Do we need to set it to a new limit?
                    if (i == insertAfterComma) {
                        //Set the new limit
                        limit = wipLimit;
                    }

                    newLimits += limit + ",";
                }
                temp = wipLine + newLimits;
            }
            newContent += temp + "\n";
        }

        kanbanService.editProject(projectName, newContent);

        return new ModelAndView("/admin.jsp", null);
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

    /**
     * Opens a project and goes to boardType (e.g. wall, backlog etc)
     * 
     * @param projectName
     * @param boardType
     * @param newProjectName
     * @return
     * @throws IOException
     */
    @RequestMapping("open-project")
    public synchronized RedirectView openProject(
                                                 @PathVariable("projectName") String projectName,
                                                 @PathVariable("board") String boardType,
                                                 @RequestParam("newProjectName") String newProjectName)
        throws IOException {

        return new RedirectView(
            "/projects/" + newProjectName + "/" + boardType, true);
    }

    /**
     * Models are a hashmap used to pass attributes to a view.
     * 
     * @param projectName
     * @param boardType
     * @return the model hashmap
     */
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
                               @RequestParam("startDate") String startDate,
                               @RequestParam("endDate") String endDate,
                               OutputStream outputStream) throws IOException {

        WorkItemTree tree = project.getWorkItemTree();
        WorkItemType type = project.getWorkItemTypes().getRoot().getValue();
        List<WorkItem> topLevelWorkItems = tree.getWorkItemsOfType(type);

        LocalDate start = null;
        LocalDate end = null;

        try {
            start = LocalDate.fromDateFields(DateFormat.getDateInstance().parse(startDate));
        } catch (ParseException e) {
            // keep start as null
        }
        try {
            end = LocalDate.fromDateFields(DateFormat.getDateInstance().parse(endDate));
        } catch (ParseException e) {
            // keep end as null
        }

        chartGenerator.generateBurnUpChart(type, topLevelWorkItems, start,
            end, outputStream);
    }

    @RequestMapping("add-column-action")
    public synchronized RedirectView addColumn(
                                               @ModelAttribute("project") KanbanProject project,
                                               @PathVariable("projectName") String projectName,
                                               @PathVariable("board") String boardType,
                                               @RequestParam("name") String name) throws IOException {

        String orig = kanbanService
            .getProjectConfiguration(projectName).getKanbanPropertiesFile()
            .getContentAsString();

        Scanner sc = new Scanner(orig);
        String temp = "";
        String newContent = "";
        boolean addedCol = false;

        while (sc.hasNext() && name != null && name != "") {

            temp = sc.nextLine();
            if (temp.contains("phases") && !addedCol) {
                addedCol = true;
                String[] phases = temp.split(",");
                String last = phases[phases.length - 1];
                //FOr when cancel is hit on the add column button
                if (name.equals("null")) {
                }
                else {
                    last = name + "," + last;
                }

                for (int i = 0; i < phases.length - 1; i++) {
                    newContent += phases[i] + ",";
                }
                newContent += last + "\n";

            }
            else if (temp.contains("boards.wall")) {
                //FOr when cancel is hit on the add column button
                if (name.equals("null")) {
                    newContent += temp + "\n";
                }
                else {
                    temp += "," + name;
                    newContent += temp + "\n";
                }
            }
            else {
                newContent += temp + "\n";
            }

        }
        //For when Ok button is pressed and no input is entered
        if (newContent.length() < 10) {
            newContent = orig;
        }
        kanbanService.editProject(projectName, newContent);
        return new RedirectView(
            "/projects/" + projectName + "/" + boardType, true);
    }

    @RequestMapping("add-waitingcolumn-action")
    public synchronized RedirectView addWaitingColumn(
                                                      @ModelAttribute("project") KanbanProject project,
                                                      @PathVariable("projectName") String projectName,
                                                      @PathVariable("board") String boardType,
                                                      @RequestParam("name") String name) throws IOException {

        String orig = kanbanService
            .getProjectConfiguration(projectName).getKanbanPropertiesFile()
            .getContentAsString();

        Scanner sc = new Scanner(orig);
        String temp = "";
        String newContent = "";

        while (sc.hasNext() && name != null && name != "") {

            temp = sc.nextLine();
            if (temp.contains(name)) {
                String[] phases = temp.split(",|=");

                for (int i = 0; i < phases.length; i++) {

                    if (phases[i].equals(name)) {
                        newContent += "Pre - " + name + ",";
                    }
                    if (phases[i].contains(".")) {
                        newContent += phases[i] + "=";
                    }
                    else {
                        newContent += phases[i] + ",";
                    }

                }
                newContent += "\n";

            }
            else if (temp.contains("boards.wall")) {
                //FOr when cancel is hit on the add column button
                if (name.equals("null")) {
                    newContent += temp + "\n";
                }
                else {
                    temp += "," + name;
                    newContent += temp + "\n";
                }
            }

            else {
                newContent += temp + "\n";
            }

        }
        if (newContent.length() < 10) {
            newContent = orig;
        }

        kanbanService.editProject(projectName, newContent);

        return new RedirectView(
            "/projects/" + projectName + "/" + boardType, true);
    }

    @RequestMapping("delete-column-action")
    public synchronized RedirectView deleteColumn(
                                                  @ModelAttribute("project") KanbanProject project,
                                                  @PathVariable("projectName") String projectName,
                                                  @PathVariable("board") String boardType,
                                                  @RequestParam("name") String name) throws IOException {

        String orig = kanbanService
            .getProjectConfiguration(projectName).getKanbanPropertiesFile()
            .getContentAsString();

        Scanner sc = new Scanner(orig);
        String temp = "";
        String newContent = "";

        while (sc.hasNext() && name != null && name != "") {

            temp = sc.nextLine();
            if (temp.contains(name)) {
                String[] phases = temp.split(",|=");

                for (int i = 0; i <= phases.length - 1; i++) {

                    if (phases[i].equals(name)) {
                        //DO NOTHING 
                    }
                    else {
                        if (i == 0) {
                            newContent += phases[i] + "=";
                        }
                        else if (i == phases.length - 1) {
                            newContent += phases[i];
                        }
                        else if (i == phases.length - 2 && name.equals(phases[phases.length - 1])) {
                            newContent += phases[i];
                        }
                        else {

                            newContent += phases[i] + ",";
                        }
                    }
                }
                newContent += "\n";
            }
            else {
                newContent += temp + "\n";
            }
        }

        if (newContent.length() < 10) {
            newContent = orig;
        }

        kanbanService.editProject(projectName, newContent);

        return new RedirectView(
            "/projects/" + projectName + "/" + boardType, true);
    }
}
