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
import java.util.Collections;
import org.apache.commons.collections.CollectionUtils;

//TODO This class needs unit tests.

/**
 * @author Nicholas Malcolm - malcolnich - 300170288
 * 
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
			@RequestParam("parentId") String parentId,
			@RequestParam("type") String type,
			@RequestParam("name") String name,
			@RequestParam("size") Integer size,
			@RequestParam("importance") Integer importance,
			@RequestParam("notes") String notes) throws IOException {
		// Param passed as string, need an int:
		int parentIdAsInteger = Integer.parseInt(parentId);

		WorkItemType typeAsWorkItemType = project.getWorkItemTypes().getByName(
				type);

		// Don't let null values get through
		if (size == null) {
			size = 0;
		}
		if (importance == null) {
			importance = 0;
		}

		// Add it and save it
		project.addWorkItem(parentIdAsInteger, typeAsWorkItemType, name, size,
				importance, notes, currentLocalDate());
		project.save();

		// Redirect
		return new RedirectView("../" + boardType);
	}

	@RequestMapping(value = "print-items", method = RequestMethod.POST)
	public synchronized ModelAndView printItems(
			@ModelAttribute("project") KanbanProject project,
			@PathVariable("projectName") String projectName,
			@PathVariable("board") String boardType,
			@RequestParam("printSelection") String[] ids) throws IOException {

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
	 * @param id
	 * @param parentId
	 * @param name
	 * @param size
	 * @param importance
	 * @param notes
	 * @param excluded
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
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
			HttpServletRequest request) throws IOException, ParseException {

		@SuppressWarnings("unchecked")
		Map<String, String[]> parameters = request.getParameterMap();

		// Get the item which is being edited
		WorkItem workItem = project.getWorkItemTree().getWorkItem(id);

		// Save all the updates
		workItem.setName(name);
		workItem.setSize(size == null ? 0 : size);
		workItem.setImportance(importance == null ? 0 : importance);
		workItem.setNotes(notes);
		workItem.setExcluded(excluded);

		// TODO Figure this out
		for (String phase : workItem.getType().getPhases()) {
			String key = "date-" + phase;
			String[] valueArray = parameters.get(key);
			if (valueArray == null || valueArray[0].trim().isEmpty()) {
				workItem.setDate(phase, null);
			} else {
				workItem.setDate(phase,
						parseConventionalNewZealandDate(valueArray[0]));
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
			@RequestParam("level") String level, OutputStream outputStream)
	throws IOException {

		WorkItemType type = project.getWorkItemTypes().getByName(level);
		List<WorkItem> workItemList = project.getWorkItemTree()
		.getWorkItemsOfType(type);

		CumulativeFlowChartBuilder builder = new CumulativeFlowChartBuilder();

		CategoryDataset dataset = builder.createDataset(type.getPhases(),
				workItemList);
		JFreeChart chart = builder.createChart(dataset);
		ChartUtilities.writeChartAsPNG(outputStream, chart, 800, 600);
	}

	@RequestMapping("cycle-time-chart.png")
	public synchronized void cycleTimeChartPng(
			@ModelAttribute("project") KanbanProject project,
			@PathVariable("board") String boardType,
			@RequestParam("level") String level, OutputStream outputStream)
	throws IOException {

		WorkItemType type = project.getWorkItemTypes().getByName(level);
		CycleTimeChartBuilder builder = new CycleTimeChartBuilder();
		List<WorkItem> workItemList = project.getWorkItemTree()
		.getWorkItemsOfType(type);
		CategoryDataset dataset = builder.createDataset(builder
				.getCompletedWorkItemsInOrderOfCompletion(workItemList));
		JFreeChart chart = builder.createChart(dataset);
		ChartUtilities.writeChartAsPNG(outputStream, chart, 800, 600);
	}

	/**
	 * Provides the variables of the current project to either a new project form,
	 * or to the edit project page, depending on createNewProject's boolean value.
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
		if (createNewProject){
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
			OutputStream outputStream) throws IOException {

		WorkItemTree tree = project.getWorkItemTree();
		WorkItemType type = project.getWorkItemTypes().getRoot().getValue();
		List<WorkItem> topLevelWorkItems = tree.getWorkItemsOfType(type);

		chartGenerator.generateBurnUpChart(type, topLevelWorkItems,
				currentLocalDate(), outputStream);
	}

	@RequestMapping("add-column-action")
	public synchronized RedirectView addColumn(
			@ModelAttribute("project") KanbanProject project,
			@PathVariable("projectName") String projectName,
			@PathVariable("board") String boardType,
			@RequestParam("name") String name) throws IOException {


		String orig =  kanbanService
		.getProjectConfiguration(projectName).getKanbanPropertiesFile()
		.getContentAsString();

		Scanner sc = new Scanner(orig);
		String temp ="";
		String newContent="";
		boolean addedCol = false;

		while(sc.hasNext()){

			temp = sc.nextLine();
			if(temp.contains("phases") && !addedCol){
				addedCol = true;
				String [] phases = temp.split(",");
				String last = phases[phases.length-1];
				last = name+","+last;

				for(int i = 0; i < phases.length-1; i++){
					newContent += phases[i]+",";
				}
				newContent += last+"\n";
			
			}
			else if (temp.contains("boards.wall")){
				temp += ","+name;
				newContent += temp+"\n";
			}
			else{
				newContent += temp+"\n";
			}


		}



		kanbanService.editProject(projectName, newContent );
		return new RedirectView(
				"/projects/" + projectName + "/" + boardType, true);
	} 
}
