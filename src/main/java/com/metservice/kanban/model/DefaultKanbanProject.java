package com.metservice.kanban.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;
import com.metservice.kanban.charts.ChartUtils;
import com.metservice.kanban.charts.burnup.BurnUpDataModel;
import com.metservice.kanban.charts.burnup.ProjectedDatasetPopulator;
import com.metservice.kanban.web.KanbanPersistence;

//TODO This class needs more unit tests.

/**
 * @author Janella Espinas, Chris Cooper
 */

public class DefaultKanbanProject implements KanbanProject {
    private final WorkItemTypeCollection workItemTypes;
    private final KanbanBoardConfiguration columnsByBoard;
    private final WorkItemTree tree;
    private final KanbanPersistence persistence;
    private final String name;

	/**
	 * Default constructor for DefaultKanbanProject.
	 * @param workItemTypes - the collection of all WorkItemTypes represented in the tree of workItems.
	 * @param phaseSequences - the representation of project phases on a board
	 * @param tree - the collection of all WorkItems in the project
	 * @param persistence - the raw file containing the data in the project
	 */
    public DefaultKanbanProject(WorkItemTypeCollection workItemTypes, KanbanBoardConfiguration phaseSequences,
                                WorkItemTree tree, KanbanPersistence persistence, String name) {
        this.workItemTypes = workItemTypes;
        this.columnsByBoard = phaseSequences;
        this.tree = tree;
        this.persistence = persistence;
        this.name = name;
    }

    /**
     * Advances a WorkItem to the next phase on the Kanban board and logs date of phase start.
     * @param id - the id of the WorkItem
     * @param date - the start date of the next phase
     */
    @Override
    public void advance(int id, LocalDate date) {
        tree.getWorkItem(id).advance(date);
    }

    /**
     * Testing to see if this works
     */
    @Override
    public void stop(int id) {
    	tree.getWorkItem(id).stop();
    }

    /**
     * Adds a new WorkItem to the project. After being added to the project, it is advanced to
     * the first phase on the Kanban board and the date is logged.
	 * @param parentId - the id of the parent WorkItem
	 * @param type - the type of the WorkItem
	 * @param itemName - the name of the WorkItem
	 * @param averageCaseEstimate - the size of the WorkItem
	 * @param worstCaseEstimate - the worstCaseEstimate of the WorkItem
	 * @param importance - the importance of the WorkItem
	 * @param notes - relevant notes regarding the WorkItem
	 * @param backlogDate - the date that the new WorkItem was added to the project and backlog
     */
    @Override
    public int addWorkItem(int parentId, WorkItemType type, String itemName, int averageCaseEstimate, int worstCaseEstimate,
                            int importance, String notes, String color, boolean excluded, String workStreams,
                            LocalDate backlogDate) {

        int newId = tree.getNewId();

        WorkItem workItem = new WorkItem(newId, parentId, type);

        workItem.setName(itemName);
        workItem.setAverageCaseEstimate(averageCaseEstimate);
        workItem.setWorstCaseEstimate(worstCaseEstimate);
        workItem.setImportance(importance);
        workItem.setNotes(notes);
        workItem.setColour(color);
        workItem.setExcluded(excluded);
        workItem.setWorkStreamsAsString(workStreams);

        tree.addWorkItem(workItem);

		// add the WorkItem onto the board and log the date
        advance(newId, backlogDate);
        
        return newId;
    }


    /**
     * Modifies order in which the WorkItems are displayed on the Kanban board.
     * @param id - the id of the WorkItem to be moved
     * @param targetId - the id of the new WorkItem either before or after the WorkItem
     * @param after - whether the WorkItem is to be placed before or after targetId
     */
    @Override
    public void move(int id, int targetId, boolean after) {
        tree.move(tree.getWorkItem(id), tree.getWorkItem(targetId), after);
    }

	/**
	 * Returns all columns on a Kanban board.
	 * @param boardType - the type of board
	 * @return the list of columns on the current Kanban board
	 */
    @Override
    public KanbanBoardColumnList getColumns(BoardIdentifier boardType) {
        return columnsByBoard.get(boardType);
    }

    @Override
    public KanbanBoardColumnList getWallColumns() {
        return getColumns(BoardIdentifier.WALL);
    }

	/**
	 * Returns the tree representation of the WorkItems in the project.
	 * @return the tree representation of all WorkItems on the current Kanban board
	 */
    @Override
    public WorkItemTree getWorkItemTree() {
        return this.tree;
    }

    /**
     * Returns the {@link WorkItem} identified by the {@code id} from this projects WorkItems.
     * @param id - the id of the {@link WorkItem} to return
     * @return the {@link WorkItem} identified by the id.
     */
    public WorkItem getWorkItemById(int id) {
        return getWorkItemTree().getWorkItem(id);
    }

	/**
	 * Removes a WorkItem from the project tree.
	 * @param id - the id of the WorkItem to be deleted
	 */
    @Override
    public void deleteWorkItem(int id) {
        tree.delete(id);
    }

	/**
	 * Removes a WorkItem from the project tree.
	 * @exception IOException if there is an error writing to the data file
	 */
    @Override
    public void save() throws IOException {
        persistence.write(tree);
    }

    /**
     * Change parent of specified work item.
     * @param id - the id of the WorkItem to be changed
     * @param newParentId - the id of the new parent WorkItem
     */
    @Override
    public void reparentWorkItem(int id, int newParentId) {
        tree.reparent(id, newParentId);
    }

    /**
     * Returns an instance of the current Kanban board.
     * @param boardType - the type of board
     * @return the Kanban board corresponding to the given boardType
     */
    @Override
    public KanbanBoard getBoard(BoardIdentifier boardType, String workStream) {
        KanbanBoardBuilder kanbanBoardBuilder = new KanbanBoardBuilder(columnsByBoard.get(boardType), workItemTypes,
            tree);
        return kanbanBoardBuilder.build(workStream, null);
    }

    /**
     * Builds backlog screen.
     * @return the Kanban backlog corresponding to the collection of WorkItems
     */
    @Override
    public KanbanBacklog getBacklog(String workStream) {
        // TODO Why a board builder to build the backlog screen?
        KanbanBoardBuilder kanbanBoardBuilder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(getRootWorkItemType(), getRootWorkItemType().getBacklogPhase())),
            workItemTypes,
            tree);
        KanbanBacklog backlog = kanbanBoardBuilder.buildKanbanBacklog(workStream);
        return backlog;
    }

    @Override
    public KanbanBoard getCompleted(String workStream) {
        // get last column
        KanbanBoardBuilder kanbanBoardBuilder = new KanbanBoardBuilder(
            new KanbanBoardColumnList(
                new KanbanBoardColumn(getRootWorkItemType(), getRootWorkItemType().getCompletedPhase())),
            workItemTypes, tree);
        return kanbanBoardBuilder.build(workStream, WorkItem.LAST_PHASE_DATE_COMPARATOR);
    }

	/**
	 * Returns the type of the topmost level (root) WorkItem within the project.
	 * @return the type of the root WorkItem
	 */
    private WorkItemType getRootWorkItemType() {
        return workItemTypes.getRoot().getValue();
    }

    /**
     * Returns a collection of all WorkItem types represented in the project.
     * @return the collection of all types represented by WorkItems in the project
     */
    @Override
    public WorkItemTypeCollection getWorkItemTypes() {
        return workItemTypes;
    }


    /**
     * Reorders a single WorkItem in the tree and rebuilds the tree.
     * @param id - the id of the WorkItem to be moved
     * @param newIdList - the new ids of other WorkItems in the project
     */
    @Override
    public void reorder(Integer id, Integer[] newIdList) {
        List<WorkItem> list = new ArrayList<WorkItem>();
        for(Integer i: newIdList) {
            list.add(tree.getWorkItem(i));
        }

        tree.reorder(tree.getWorkItem(id), list);
    }
/*
    @Override
    public String getJournalText() {
    	String catchString = "";
    	 try {
			catchString = persistence.journalRead();
    	 } catch (IOException e) {
			e.printStackTrace();
    	 }
    	return catchString;
    }

    @Override
    public void writeJournalText(String journalText) {
    	try {
			persistence.journalWrite(journalText);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
*/
    @Override
    public List<KanbanJournalItem> getJournal() {
        try {
            return persistence.journalRead();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getWorkStreams() {
        // TODO: for large projects this can be inefficient - consider caching this set
        Set<String> workStreams = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

        for (WorkItem workItems : tree.getWorkItemList()) {
            for (String ws : workItems.getWorkStreams()) {
                workStreams.add(ws);
            }
        }

        return workStreams;
    }

    @Override
    public void addJournalItem(KanbanJournalItem journalItem) {
        try {
            List<KanbanJournalItem> journal = persistence.journalRead();

            journalItem.setId(nextJournalId(journal));
            journal.add(journalItem);
            persistence.journalWrite(journal);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int nextJournalId(List<KanbanJournalItem> journal) {
        int max = 0;
        for (KanbanJournalItem item : journal) {
            max = Math.max(max, item.getId());
        }
        return max + 1;
    }

    @Override
    public void updateJournalItem(KanbanJournalItem journalItem) {
        throw new NotImplementedException();
    }

    @Override
    public void deleteJournalItem(int itemId) {
        try {
            List<KanbanJournalItem> journal = persistence.journalRead();
            for (KanbanJournalItem item : journal) {
                if (item.getId() == itemId) {
                    journal.remove(item);
                    break;
                }
            }
            persistence.journalWrite(journal);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public LocalDate getStartDate() {
        return ChartUtils.getFirstDate(tree.getWorkItemList());
    }

    @Override
    public LocalDate getProjectedEndDate(LocalDate startDate, LocalDate endDate) {

        BurnUpDataModel model = new BurnUpDataModel(getRootWorkItemType(), tree.getWorkItemList(), startDate, endDate);
        ProjectedDatasetPopulator projectedDatasetPopulator = new ProjectedDatasetPopulator(model);
        return projectedDatasetPopulator.getProjectedEndDate();
    }

    @Override
    public WorkItemType getChildType(WorkItemType parentType) {
        return getWorkItemTypes().getTreeNode(parentType).getChild(0).getValue();
    }

}
