package com.metservice.kanban.web;

import static com.metservice.kanban.model.WorkItem.ROOT_WORK_ITEM_ID;
import static com.metservice.kanban.utils.DateUtils.currentLocalDate;
import static com.metservice.kanban.utils.DateUtils.parseConventionalNewZealandDate;
import static java.lang.Integer.parseInt;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.charts.burnup.BurnUpChartGenerator;
import com.metservice.kanban.charts.burnup.DefaultBurnUpChartGenerator;
import com.metservice.kanban.charts.burnup.DefaultChartWriter;
import com.metservice.kanban.charts.cumulativeflow.CumulativeFlowChartBuilder;
import com.metservice.kanban.charts.cycletime.CycleTimeChartBuilder;
import com.metservice.kanban.model.BoardIdentifier;
import com.metservice.kanban.model.KanbanBoard;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemComment;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.utils.DateUtils;
import com.metservice.kanban.utils.JsonLocalDateTimeConvertor;

//TODO This class needs more unit tests.

@Controller
@RequestMapping("{projectName}")
@SessionAttributes("workStreams")
public class KanbanBoardController {

    private static final int MAX_PROJECT_NAME_LENGTH = 32;

    private static final String PROJECT_NAME_INVALID_CHARS = "/\\|<>*?&:\"";

    public static final int DEFAULT_MONTHS_DISPLAY = 4;

    @Autowired
    private KanbanService kanbanService;
    private Gson gson;

    public KanbanBoardController() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonLocalDateTimeConvertor());
        this.gson = gsonBuilder.create();
    }

    @ModelAttribute("project")
    public synchronized KanbanProject populateProject(@PathVariable("projectName") String projectName)
        throws IOException {

        return kanbanService.getKanbanProject(projectName);
    }

    /*
        @ModelAttribute("boardName")
        public String populateBoardName(@PathVariable("board") String board) {
            return board;
        }
        */

    @ModelAttribute("projectName")
    public String populateProjectName(@PathVariable("projectName") String board) {
        return board;
    }

    @ModelAttribute("workStreams")
    public synchronized Map<String, String> populateWorkStreams() {
        return new HashMap<String, String>();
    }

    @ModelAttribute("chartGenerator")
    public synchronized BurnUpChartGenerator populateChartGenerator() {
        return new DefaultBurnUpChartGenerator(new DefaultChartWriter());
    }

    private Map<String, Object> initBoard(String boardType, String projectName, String error, String scrollTop) {
        //        boardType = cleanBoardType(boardType);

        Map<String, Object> model = buildModel(projectName, boardType);

        // TODO model used to have kanbanTransaction now it has kanban... need
        // to fix view

        model.put("scrollTop", scrollTop == null ? "0" : scrollTop);
        model.put("error", error);

        return model;
    }

    @RequestMapping("wall")
    public synchronized ModelAndView wallBoard(@ModelAttribute("project") KanbanProject project,
                                               @PathVariable("projectName") String projectName,
                                               @RequestParam(value = "scrollTop", required = false) String scrollTop,
                                               @RequestParam(value = "error", required = false) String error,
                                               @ModelAttribute("workStreams") Map<String, String> workStreams,
                                               @RequestParam(value = "highlight", required = false) String highlight)
        throws IOException {

        Map<String, Object> model = initBoard("wall", projectName, error, scrollTop);
        
        KanbanBoard board = project.getBoard(BoardIdentifier.WALL, workStreams.get(projectName));

        model.put("board", board);
        model.put("highlight", highlight);

        return new ModelAndView("/project.jsp", model);
    }

    @RequestMapping("backlog")
    public synchronized ModelAndView backlogBoard(@ModelAttribute("project") KanbanProject project,
                                                  @PathVariable("projectName") String projectName,
                                                  @RequestParam(value = "scrollTop", required = false) String scrollTop,
                                                  @RequestParam(value = "error", required = false) String error,
                                                  @ModelAttribute("workStreams") Map<String, String> workStreams)
        throws IOException {

        Map<String, Object> model = initBoard("backlog", projectName, error, scrollTop);

        model.put("kanbanBacklog", project.getBacklog(workStreams.get(projectName)));
        model.put("type", project.getWorkItemTypes().getRoot().getValue());
        model.put("phase", project.getWorkItemTypes().getRoot().getValue().getBacklogPhase());

        return new ModelAndView("/backlog.jsp", model);
    }

    @RequestMapping("journal")
    public synchronized ModelAndView journalBoard(@ModelAttribute("project") KanbanProject project,
                                                  @PathVariable("projectName") String projectName,
                                                  @RequestParam(value = "scrollTop", required = false) String scrollTop,
                                                  @RequestParam(value = "error", required = false) String error,
                                                  @ModelAttribute("workStreams") Map<String, String> workStreams)
        throws IOException {

        Map<String, Object> model = initBoard("journal", projectName, error, scrollTop);
        model.put("kanbanJournal", project.getJournalText());
        return new ModelAndView("/journal.jsp", model);
    }

    @RequestMapping("completed")
    public synchronized ModelAndView completedBoard(@ModelAttribute("project") KanbanProject project,
                                                    @PathVariable("projectName") String projectName,
                                                    @RequestParam(value = "scrollTop", required = false) String scrollTop,
                                                    @RequestParam(value = "error", required = false) String error,
                                                    @ModelAttribute("workStreams") Map<String, String> workStreams)
        throws IOException {

        Map<String, Object> model = initBoard("completed", projectName, error, scrollTop);

        model.put("type", project.getWorkItemTypes().getRoot().getValue());
        model.put("phase", project.getWorkItemTypes().getRoot().getValue().getCompletedPhase());
        model.put("board", project.getCompleted(workStreams.get(projectName)));

        return new ModelAndView("/completed.jsp", model);
    }

    @RequestMapping(value = "{board}/advance-item-action")
    public synchronized RedirectView advanceItemAction(@ModelAttribute("project") KanbanProject project,
                                                       @PathVariable("board") String boardType,
                                                       @RequestParam("id") String id,
                                                       @RequestParam("phase") String phase,
                                                       @RequestParam(value = "scrollTop", required = false) Integer scrollTop)
        throws IOException {

        // check item hasn't already been advanced
        WorkItem workItem = project.getWorkItemById(Integer.parseInt(id));
        if (!workItem.getCurrentPhase().equals(phase)) {
            //TODO display error to user
            return new RedirectView("../" + boardType
                    + "?error=Your board view was out of date, your request has been canceled and the board has been updated. Please review the board now and apply your changes.");
        }

        project.advance(parseInt(id), currentLocalDate());
        project.save();

        return new RedirectView("../" + boardType + "?scrollTop=" + scrollTop + "&highlight=" + id);
    }

    @RequestMapping(value = "{board}/block-item-action")
    public synchronized RedirectView stopItemAction(@ModelAttribute("project") KanbanProject project,
                                                    @PathVariable("board") String boardType,
                                                    @RequestParam("itemId") String id,
                                                    @RequestParam("comment") String comment,
                                                    @RequestParam("userName") String userName) throws IOException {

        int itemId = parseInt(id);

        WorkItem wi = project.getWorkItemById(itemId);

        project.stop(itemId);

        if (!StringUtils.isEmpty(comment)) {
            wi.addComment(createBlockedComment(wi.isBlocked(), comment, userName));
        }
        project.save();

        // Redirect
        return new RedirectView("../" + boardType);

        //return new RedirectView(includeScrollTopPosition(boardType, scrollTop));
    }

    static WorkItemComment createBlockedComment(boolean isBlockedReason, String comment, String userName) {
        if (isBlockedReason) {
            return new WorkItemComment(userName, "Blocked: " + comment);
        }
        else {
            return new WorkItemComment(userName, "Unblocked: " + comment);
        }
    }

    private String includeScrollTopPosition(String boardType, String scrollTop) {
        return "../" + boardType + "?scrollTop=" + scrollTop;
    }

    /** Creates empty item model to display in add form with preset parent id. **/
    @RequestMapping("{board}/add-item")
    public synchronized ModelAndView addItem(@ModelAttribute("project") KanbanProject project,
                                             @PathVariable("projectName") String projectName,
                                             @RequestParam("id") int id) throws IOException {

        // Search for parent id
        WorkItem parent = project.getWorkItemTree().getWorkItem(id);

        WorkItemType type = project.getWorkItemTypes().getRoot().getValue();
        String parentName = "";
        int parentId = ROOT_WORK_ITEM_ID;
        String legend = "Add " + type;

        if (parent != null) {
            parentName = parent.getName();
            parentId = parent.getId();
            type = project.getWorkItemTypes().getTreeNode(parent.getType()).getChild(0).getValue();
            legend = "Add a " + type + " to " + parentName;
        }

        Map<String,Object> model = new HashMap<String, Object>();

        model.put("legend", legend);
        model.put("parentId", parentId);
        model.put("type", type);
        model.put("topLevel", true);

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
    @RequestMapping("{board}/edit-item")
    public synchronized ModelAndView editItem(@ModelAttribute("project") KanbanProject project,
                                              @PathVariable("board") String board,
                                              @PathVariable("projectName") String projectName,
                                              @RequestParam("id") Integer id) throws IOException {

        // Get a model ready to take some attributes
        Map<String, Object> model = new HashMap<String, Object>();
        //buildModel(projectName, boardType);

        // Fetch the item we want to edit
        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);

        // Add some variables to the model hashmap
        model.put("workItem", workItem);
        model.put("children", project.getWorkItemTree().getChildren(id));
        model.put("parentAlternativesList", project.getWorkItemTree().getParentAlternatives(workItem));
        model.put("board", board);

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
    @RequestMapping("{board}/add-item-action")
    public synchronized RedirectView addItemAction(@ModelAttribute("project") KanbanProject project,
                                                   @PathVariable("board") String board,
                                                   @RequestParam(value = "parentId", required = false) Integer parentId,
                                                   @RequestParam("type") String type,
                                                   @RequestParam("name") String name,
                                                   @RequestParam("averageCaseEstimate") String averageCaseEstimateStr,
                                                   @RequestParam(value = "worstCaseEstimate", required = false) String worstCaseEstimateStr,
                                                   @RequestParam("importance") String importanceStr,
                                                   @RequestParam("notes") String notes,
                                                   @RequestParam("color") String color,
                                                   @RequestParam(value = "excluded", required = false) String excludedStr,
                                                   @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                                   @RequestParam(value = "workStreamsSelect", required = false) String workStreams) throws IOException {

        WorkItemType typeAsWorkItemType = project.getWorkItemTypes().getByName(type);

        if (parentId == null) {
            parentId = WorkItem.ROOT_WORK_ITEM_ID;
        }

        int averageCaseEstimate = parseInteger(averageCaseEstimateStr, 0);
        int importance = parseInteger(importanceStr, 0);
        boolean excluded = parseBoolean(excludedStr);
        int worstCaseEstimate = parseInteger(worstCaseEstimateStr, 0);

        // Add it and save it
        int newId = project.addWorkItem(parentId, typeAsWorkItemType, name, averageCaseEstimate, worstCaseEstimate,
            importance, notes, color, excluded, workStreams, currentLocalDate());
        project.save();

        if ("print".equals(redirectTo)) {
            return new RedirectView("../print-items?printSelection=" + newId);
        }
        else {
            return new RedirectView("../" + board);
        }
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
    @RequestMapping("{board}/edit-item-action")
    public synchronized RedirectView editItemAction(@ModelAttribute("project") KanbanProject project,
                                                    @PathVariable("board") String boardType,
                                                    @RequestParam("id") int id,
                                                    @RequestParam("parentId") Integer parentId,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("averageCaseEstimate") String averageCaseEstimateStr,
                                                    @RequestParam("worstCaseEstimate") String worstCaseEstimateStr,
                                                    @RequestParam("importance") String importanceStr,
                                                    @RequestParam("notes") String notes,
                                                    @RequestParam("color") String color,
                                                    @RequestParam(value = "excluded", required = false) String excludedStr,
                                                    @RequestParam(value = "workStreamsSelect", required = false) String workStreams,
                                                    @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                                    HttpServletRequest request) throws IOException, ParseException {
    
        // Get the item which is being edited
        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);
    
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameters = request.getParameterMap();
    
        int averageCaseEstimate = parseInteger(averageCaseEstimateStr, 0);
        int worstCaseEstimate = parseInteger(worstCaseEstimateStr, 0);
        int importance = parseInteger(importanceStr, 0);
        boolean excluded = parseBoolean(excludedStr);
    
        // Save all the updates
        workItem.setName(name);
        workItem.setAverageCaseEstimate(averageCaseEstimate);
        workItem.setWorstCaseEstimate(worstCaseEstimate);
        workItem.setImportance(importance);
        workItem.setNotes(notes);
        workItem.setExcluded(excluded);
        workItem.setColour(color);
        workItem.setWorkStreamsAsString(workStreams);
    
        // TODO Figure this out
        for (String phase : workItem.getType().getPhases()) {
            String key = "date-" + phase;
            String[] valueArray = parameters.get(key);
            if (valueArray != null) {
                if (valueArray[0].trim().isEmpty()) {
                    workItem.setDate(phase, null);
                } else {
                    workItem.setDate(phase, parseConventionalNewZealandDate(valueArray[0]));
                }
            }
        }
    
        // If it's changed parent, reset the parent.
        if (workItem.getParentId() != parentId) {
            project.getWorkItemTree().reparent(id, parentId);
        }
    
        // Save the whole project
        project.save();
    
        if ("print".equals(redirectTo)) {
            return new RedirectView("../print-items?printSelection=" + id);
        }
        else {
            return new RedirectView("../" + boardType);
        }
    }

    @RequestMapping(value = "print-items")
    public synchronized ModelAndView printItems(@ModelAttribute("project") KanbanProject project,
                                                @PathVariable("projectName") String projectName,
                                                @RequestParam("printSelection") String[] ids) throws IOException {

        List<WorkItem> items = new ArrayList<WorkItem>();

        for (String id : ids) {
            items.add(project.getWorkItemTree().getWorkItem(parseInteger(id, 0)));
        }

        return new ModelAndView("/printCards.jsp", "items", items);
    }

    @RequestMapping("{board}/move-item-action")
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

    @RequestMapping("{board}/reorder")
    public synchronized RedirectView reorder(@ModelAttribute("project") KanbanProject project,
                                             @PathVariable("board") String boardType,
                                             @RequestParam("id") Integer id, @RequestParam("ids") Integer[] ids,
                                             @RequestParam("scrollTop") String scrollTop) throws IOException {

        project.reorder(id, ids);
        project.save();

        return new RedirectView(includeScrollTopPosition(boardType, scrollTop));

    }

    @RequestMapping(value = "{board}/edit-item/{id}/name", method = RequestMethod.POST)
    public synchronized ResponseEntity<String> updateItemName(@ModelAttribute("project") KanbanProject project,
                                                              @PathVariable("board") String boardType,
                                                              @PathVariable("id") int id,
                                                              @RequestParam("newValue") String newValue)
                    throws IOException {
        // why are these methods marked as synchronized?!?

        // Get the item which is being edited
        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);

        workItem.setName(newValue);
        project.save();

        // Go home.
        return new ResponseEntity<String>(
                String.format("Name change successfully.  New name: %s", workItem.getName()), HttpStatus.OK);
    }

    @RequestMapping(value = "{board}/edit-item/{id}/size", method = RequestMethod.POST)
    public synchronized ResponseEntity<String> updateItemSize(@ModelAttribute("project") KanbanProject project,
                                                              @PathVariable("board") String boardType,
                                                              @PathVariable("id") int id,
                                                              @RequestParam("newValue") String newValue)
                    throws IOException {
        // why are these methods marked as synchronized?!?

        // Get the item which is being edited
        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);

        workItem.setAverageCaseEstimate(parseInteger(newValue, 0));
        project.save();

        // Go home.
        return new ResponseEntity<String>(
                String.format("Size change successfully.  New size: %s", workItem.getAverageCaseEstimate()), HttpStatus.OK);
    }

    @RequestMapping(value = "{board}/edit-item/{id}/importance", method = RequestMethod.POST)
    public synchronized ResponseEntity<String> updateItemImportance(@ModelAttribute("project") KanbanProject project,
                                                                    @PathVariable("board") String boardType,
                                                                    @PathVariable("id") int id,
                                                                    @RequestParam("newValue") String newValue)
                    throws IOException {
        // why are these methods marked as synchronized?!?

        // Get the item which is being edited
        WorkItem workItem = project.getWorkItemTree().getWorkItem(id);

        workItem.setImportance(parseInteger(newValue, 0));
        project.save();

        // Go home.
        return new ResponseEntity<String>(
            String.format("Importance change successfully.  New importance: %s", workItem.getImportance()),
            HttpStatus.OK);
    }

    @RequestMapping("edit-journal-action")
    public synchronized RedirectView editJournalAction(@ModelAttribute("project") KanbanProject project,
                                                       @RequestParam("journalText") String journalText,
                                                       HttpServletRequest request) throws IOException, ParseException {

        project.writeJournalText(journalText);
        return new RedirectView("journal");

    }

    /**
     * Responds to a request to delete an item
     *
     * @param project
     * @param id
     *            - the id of the item you want to delete
     * @param board
     *            - the view you want to redirect to
     * @return
     * @throws IOException
     */
    @RequestMapping("{board}/delete-item-action")
    public synchronized RedirectView deleteWorkItem(@ModelAttribute("project") KanbanProject project,
                                            @RequestParam("id") int id,
                                            @PathVariable("board") String board) throws IOException {

        // Delete the workitem, save and redirect.
        project.deleteWorkItem(id);
        project.save();
        return new RedirectView("../" + board);
    }

    @RequestMapping("chart")
    public synchronized ModelAndView chart(@ModelAttribute("project") KanbanProject project,
                                           @RequestParam("chartName") String chartName,
                                           @RequestParam("workItemTypeName") String workItemTypeName,
                                           @PathVariable("projectName") String projectName,
                                           @RequestParam(value = "error", required = false) String error,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {

        if (StringUtils.isEmpty(startDate)) {
            startDate = defaultStartDate(endDate);
        }
        if (StringUtils.isEmpty(endDate)) {
            endDate = LocalDate.fromCalendarFields(Calendar.getInstance()).toString("dd/MM/yyyy");
        }

        Map<String, Object> model = initBoard("chart", projectName, error, null);

        model.put("workItemTypeName", workItemTypeName);
        model.put("imageName", chartName + ".png");
        model.put("startDate", startDate);
        model.put("endDate", endDate);
        model.put("kanbanJournal", project.getJournalText());
        model.put("chartName", chartName);

        return new ModelAndView("/chart.jsp", model);
    }

    private String defaultStartDate(String endDate) {
        LocalDate endDateParsed = LocalDate.fromCalendarFields(Calendar.getInstance());
        if (null != endDate) {
            try {
                endDateParsed = LocalDate.fromDateFields(DateFormat.getDateInstance().parse(endDate));
            } catch (ParseException e) {
                // do nothing
            }
        }
        return endDateParsed.minusMonths(DEFAULT_MONTHS_DISPLAY).toString("dd/MM/yyyy");
    }

    // TODO check in this class for redundent model.put("kanban...

    @RequestMapping("cumulative-flow-chart.png")
    public synchronized void cumulativeFlowChartPng(@ModelAttribute("project") KanbanProject project,
                                                    @RequestParam("startDate") String startDate,
                                                    @RequestParam("endDate") String endDate,
                                                    @RequestParam("level") String level,
                                                    @RequestParam(value = "workStream", required = false) String workStream,
                                                    OutputStream outputStream) throws IOException {

        WorkItemType type;

        try {
            type = project.getWorkItemTypes().getByName(level);
        } catch (IllegalArgumentException e) {
            // TODO produce image with text from exception
            return;
        }

        List<WorkItem> workItemList = project.getWorkItemTree().getWorkItemsOfType(type, workStream);

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

        CategoryDataset dataset = builder.createDataset(type.getPhases(), workItemList);
        JFreeChart chart = builder.createChart(dataset);
        int width = dataset.getColumnCount() * 15;
        ChartUtilities.writeChartAsPNG(outputStream, chart, width < 800 ? 800 : width, 600);
    }

    @RequestMapping("cycle-time-chart.png")
    public synchronized void cycleTimeChartPng(@ModelAttribute("project") KanbanProject project,
                                               @RequestParam("startDate") String startDate,
                                               @RequestParam("endDate") String endDate,
                                               @RequestParam(value = "workStream", required = false) String workStream,
                                               @RequestParam("level") String level, OutputStream outputStream)
        throws IOException {

        WorkItemType type = project.getWorkItemTypes().getByName(level);
        CycleTimeChartBuilder builder = new CycleTimeChartBuilder();
        List<WorkItem> workItemList = project.getWorkItemTree().getWorkItemsOfType(type, workStream);

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
    public synchronized ModelAndView editProject(@ModelAttribute("project") KanbanProject project,
                                                 @PathVariable("projectName") String projectName,
                                                 @RequestParam("createNewProject") boolean createNewProject,
                                                 @RequestParam(value = "error", required = false) String error)
        throws IOException {

        Map<String, Object> model = buildModel(projectName, "wall");

        // Get the settings of this current project and pass it to the form.
        model.put("settings", kanbanService
            .getProjectConfiguration(projectName).getKanbanPropertiesFile()
            .getContentAsString());
        model.put("error", error);
        // Create a new project if true
        if (createNewProject) {
            return new ModelAndView("/createProject.jsp", model);
        }
        // else edit the current project
        else {
            return new ModelAndView("/editProject.jsp", model);
        }
    }

    /**
     * Saves the <code>content</code> string and renames the project if
     * <code>newProjectName</code> does
     * not match <code>projectName</code>.
     * 
     * @param project
     *            the project
     * @param projectName
     *            the project name
     * @param newProjectName
     *            the new project name
     * @param content
     *            the content
     * @return the redirect view
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping("edit-project-action")
    public synchronized RedirectView editProjectAction(@ModelAttribute("project") KanbanProject project,
                                                       @PathVariable("projectName") String projectName,
                                                       @RequestParam("newProjectName") String newProjectName,
                                                       @RequestParam("content") String content) throws IOException {

        if (newProjectName != null && !newProjectName.equals(projectName)) {
            // edit project name

            String validProjectNameError = isProjectNameValid(newProjectName);
            if (!StringUtils.isEmpty(validProjectNameError)) {
                return new RedirectView("edit-project?createNewProject=false&error="
                    + URLEncoder.encode(validProjectNameError, "US-ASCII"));
            }
            kanbanService.renameProject(projectName, newProjectName);
        }
        // edit project
        kanbanService.editProject(newProjectName, content);
        return openProject(projectName, "wall", newProjectName, null, null);
    }

    static String isProjectNameValid(String newProjectName) {

        if (StringUtils.containsAny(newProjectName, PROJECT_NAME_INVALID_CHARS)) {
            return String.format("Project name contains incorrect characters at least one of (%s)",
                PROJECT_NAME_INVALID_CHARS);
        }
        if (StringUtils.isEmpty(newProjectName.trim())) {
            return "Project name should not be empty";
        }
        if (newProjectName.length() > MAX_PROJECT_NAME_LENGTH) {
            return String.format("Project name is too long, maximum allowed length is %d charactes, but is %d",
                MAX_PROJECT_NAME_LENGTH, newProjectName.length());
        }

        return null;
    }

    @RequestMapping("edit-wiplimit-action")
    public synchronized ModelAndView editWIPLimitAction(@ModelAttribute("project") KanbanProject project,
                                                        @PathVariable("projectName") String projectName,
                                                        @RequestParam("columnName") String columnName,
                                                        @RequestParam("columnType") String columnType,
                                                        @RequestParam("wipLimit") String wipLimit) throws IOException,
        ParseException {

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
    public synchronized RedirectView createProjectAction(@ModelAttribute("project") KanbanProject project,
                                                         @PathVariable("projectName") String projectName,
                                                         @RequestParam("newProjectName") String newProjectName,
                                                         @RequestParam("content") String content) throws IOException {

        kanbanService.createProject(newProjectName, content);
        return openProject(projectName, "wall", newProjectName, null, null);
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
    @RequestMapping("{board}/open-project")
    public synchronized RedirectView openProject(@PathVariable("projectName") String projectName,
                                                 @PathVariable("board") String boardType,
                                                 @RequestParam("newProjectName") String newProjectName,
                                                 @RequestParam(value = "chartName", required = false) String chartName,
                                                 @RequestParam(value = "workItemTypeName", required = false) String workItemTypeName)
        throws IOException {

        String params = "";

        if (!StringUtils.isEmpty(chartName) && !StringUtils.isEmpty(workItemTypeName)) {
            params = "?chartName=" + chartName + "&workItemTypeName=" + workItemTypeName;
        }
        return new RedirectView(
            "/projects/" + newProjectName + "/" + boardType + params, true);
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
    public void burnUpChartPng(@ModelAttribute("project") KanbanProject project,
                               @ModelAttribute("chartGenerator") BurnUpChartGenerator chartGenerator,
                               @RequestParam("startDate") String startDate,
                               @RequestParam("endDate") String endDate,
                               @RequestParam(value = "workStream", required = false) String workStream,
                               OutputStream outputStream) throws IOException {

        WorkItemTree tree = project.getWorkItemTree();
        WorkItemType type = project.getWorkItemTypes().getRoot().getValue();
        List<WorkItem> topLevelWorkItems = tree.getWorkItemsOfType(type, workStream);

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

    @RequestMapping("{board}/add-column-action")
    public synchronized RedirectView addColumn(@ModelAttribute("project") KanbanProject project,
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

    @RequestMapping("{board}/add-waitingcolumn-action")
    public synchronized RedirectView addWaitingColumn(@ModelAttribute("project") KanbanProject project,
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

    @RequestMapping("{board}/delete-column-action")
    public synchronized RedirectView deleteColumn(@ModelAttribute("project") KanbanProject project,
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

        return new RedirectView("/projects/" + projectName + "/" + boardType, true);
    }

    @RequestMapping("{board}/set-work-stream")
    public RedirectView setWorkStream(@ModelAttribute("project") KanbanProject project,
                                      @PathVariable("projectName") String projectName,
                                      @PathVariable("board") String boardType,
                                      @RequestParam("workStream") String selectedWorkStream,
                                      @RequestParam(value = "chartName", required = false) String chartName,
                                      @RequestParam(value = "workItemTypeName", required = false) String workItemTypeName,
                                      @ModelAttribute("workStreams") Map<String, String> workStreams) {

        workStreams.put(projectName, selectedWorkStream);

        String params = "";

        if (!StringUtils.isEmpty(chartName) && !StringUtils.isEmpty(workItemTypeName)) {
            params = "?chartName=" + chartName + "&workItemTypeName=" + workItemTypeName;
        }

        return new RedirectView("/projects/" + projectName + "/" + boardType + params, true);
    }

    public void setKanbanService(KanbanService kanbanService) {
        this.kanbanService = kanbanService;
    }

    private boolean parseBoolean(String excludedStr) {
        if (excludedStr == null) {
            return false;
        }
        if ("on".equals(excludedStr)) {
            return true;
        }
        return Boolean.parseBoolean(excludedStr);
    }

    private int parseInteger(String sizeStr, int defaultValue) {
        try {
            return Integer.parseInt(sizeStr);
        } catch (NumberFormatException nfe) {
        }
        return defaultValue;
    }

    @RequestMapping(value = "comment", method = RequestMethod.POST)
    public ResponseEntity<String> addComment(@ModelAttribute("project") KanbanProject project,
            @RequestParam int id,
            @RequestParam String userName,
            @RequestParam String comment) throws IOException {

        WorkItemComment workItemComment = new WorkItemComment(userName, comment);
        WorkItem workItem = project.getWorkItemById(id);
        workItem.addComment(workItemComment);

        project.save();

        String body = gson.toJson(workItemComment);
        ResponseEntity<String> response = new ResponseEntity<String>(body, HttpStatus.OK);
        return response;
    }

    @RequestMapping("")
    public RedirectView redirectToWall(@PathVariable("projectName") String projectName) {
        return new RedirectView("/projects/" + projectName + "/wall", true);
    }
}

