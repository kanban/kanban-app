package com.metservice.kanban.model;

import static com.metservice.kanban.model.WorkItem.ROOT_WORK_ITEM_ID;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds Kanban boards and backlog from a collection of WorkItems.
 * @author Janella Espinas, Chris Cooper
 */
public class KanbanBoardBuilder {
	private final KanbanBoardColumnList columns;
	private final WorkItemTree tree;
	private final WorkItemTypeCollection workItemTypes;

	/**
	 * Default constructor for KanbanBoardBuilder
	 * @param columns - columns within the project
	 * @param workItemTypes - a collection of all types represented by WorkItems in the project
	 * @param tree - a tree representation of all WorkItems in the project 
	 */
	public KanbanBoardBuilder(KanbanBoardColumnList columns, WorkItemTypeCollection workItemTypes, WorkItemTree tree) {
		this.columns = columns;
		this.tree = tree;
		this.workItemTypes = workItemTypes;
	}

	/**
	 * Generates the Kanban board starting from the root of the current collection of WorkItems.
	 * @return the generated Kanban board
	 */
	public KanbanBoard build() {
		return combineHomogenousChildren(ROOT_WORK_ITEM_ID, workItemTypes.getRoot());
	}

	/**
	 * Generates the backlog of the Kanban project from the collection of WorkItems. This method creates a list entry
	 * for each WorkItem in the backlog.
	 * @return the generated Kanban backlog
	 */
	public KanbanBacklog buildKanbanBacklog() {
		List<WorkItem> workItems = tree.getChildrenWithType(ROOT_WORK_ITEM_ID, workItemTypes.getRoot().getValue());
		List<KanbanCell> cells = new ArrayList<KanbanCell>();
		List<WorkItem> list = columns.filter(workItems);
		// create cell for each workitem
		for (int i = 0; i < list.size(); i++) {
			WorkItem workItem = list.get(i);
			WorkItem workItemBefore = (i - 1) >= 0 ? list.get(i - 1) : null;
			WorkItem workItemAfter = (i + 1) < list.size() ? list.get(i + 1) : null;
			KanbanCell cell = new KanbanCell(workItem.getType());
			cell.setWorkItem(workItem);
			cell.setWorkItemAbove(workItemBefore);
			cell.setWorkItemBelow(workItemAfter);
			cells.add(cell);
		}
		return new KanbanBacklog(cells);
	}
	/**
	 * Generates a Kanban board from a given root WorkItem.
	 * @param workItemTypeTreeNode - the root WorkItemTreeNode with which to generate the board from
	 * @param workItem - the root WorkItem for this Kanban board
	 * @param workItemBefore - the WorkItem immediately before the given root 
	 * @param workItemAfter - the WorkItem immediately after the given root
	 * @return the generated Kanban board
	 */
	private KanbanBoard build(TreeNode<WorkItemType> workItemTypeTreeNode, WorkItem workItem, WorkItem workItemBefore, WorkItem workItemAfter) {
		KanbanBoard board = new KanbanBoard(columns);
		if (isVisible(workItem)) {
			board.insert(workItem, workItemBefore, workItemAfter);
			// build child boards and merge them with the main Kanban board
			for (TreeNode<WorkItemType> childType : workItemTypeTreeNode.getChildren()) {
				// combine child boards depending on the type of the WorkItem
				KanbanBoard childBoard = combineHomogenousChildren(workItem.getId(), childType);
				board.merge(childBoard);
			}
		}
		return board;
	}

	/**
	 * Retrieves all WorkItem children of the same type and builds a child board.
	 * @param parentId - the id of the parent WorkItem node
	 * @param childType - the type of child WorkItems within the child board
	 * @return the child Kanban board containing WorkItem children of the given childType
	 */
	private KanbanBoard combineHomogenousChildren(int parentId, TreeNode<WorkItemType> childType) {
		List<WorkItem> workItems = tree.getChildrenWithType(parentId, childType.getValue());

		if (childType.hasChildren()) {
			// space rows on the Kanban board for clearer display if there are children in the child board
			return stack(childType, workItems);
		} else {
			// pack rows tightly on the Kanban board if there are no children in the child board
			return pack(workItems);
		}
	}
	/**
	 * Formats the Kanban board to allow for spacing between child WorkItems within a child board.
	 * @param type - the type of child WorkItems within the child board
	 * @param workItems - the list of child WorkItems with the given WorkItemType
	 * @return a formatted and spaced child Kanban board
	 */
	private KanbanBoard stack(TreeNode<WorkItemType> type, List<WorkItem> workItems) {
		KanbanBoard board = new KanbanBoard(columns);

		List<WorkItem> list = columns.filter(workItems);

		for (int i = 0; i < list.size(); i++) {
			WorkItem workItem = list.get(i);
			WorkItem workItemBefore = (i - 1) >= 0 ? list.get(i - 1) : null;
			WorkItem workItemAfter = (i + 1) < list.size() ? list.get(i + 1) : null;
			// build the corresponding child board format it
			KanbanBoard childBoard = build(type, workItem, workItemBefore, workItemAfter);
			board.stack(childBoard);
		}
		return board;
	}

	/**
	 * Formats the Kanban board to leave no gaps between child WorkItems within a child board.
	 * @param workItems - the list of child WorkItems
	 * @return a formatted and compacted child Kanban board
	 */
	private KanbanBoard pack(List<WorkItem> workItems) {
		KanbanBoard board = new KanbanBoard(columns);
		for (KanbanBoardColumn column : columns) {
			List<WorkItem> sublist = new KanbanBoardColumnList(column).filter(workItems);

			for (int i = 0; i < sublist.size(); i++) {
				WorkItem workItem = sublist.get(i);
				WorkItem workItemBefore = (i - 1) >= 0 ? sublist.get(i - 1) : null;
				WorkItem workItemAfter = (i + 1) < sublist.size() ? sublist.get(i + 1) : null;
				// insert the WorkItem into the appropriate position in the list
				board.insert(workItem, workItemBefore, workItemAfter);
			}
		}
		return board;
	}

	/**
	 * Check if WorkItem is visible (i.e., displayed in a column on the Kanban board)
	 * @param workItem - the WorkItem to check
	 * @return true if the item is visible on the Kanban board, false if item is not on the Kanban board
	 */
	private boolean isVisible(WorkItem workItem) {
		return columns.containsPhase(workItem.getCurrentPhase());
	}
}
