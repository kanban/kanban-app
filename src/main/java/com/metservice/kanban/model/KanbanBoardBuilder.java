package com.metservice.kanban.model;

import static com.metservice.kanban.model.WorkItem.ROOT_WORK_ITEM_ID;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds Kanboards and backlog from the collection of workitems, alos keeps layout tidy.
 * 
 * Chris n Janella
 *
 */

public class KanbanBoardBuilder {

	private final KanbanBoardColumnList columns;
	private final WorkItemTree tree;
	private final WorkItemTypeCollection workItemTypes;

	public KanbanBoardBuilder(KanbanBoardColumnList columns, WorkItemTypeCollection workItemTypes, WorkItemTree tree) {
		this.columns = columns;
		this.tree = tree;
		this.workItemTypes = workItemTypes;
	}

	public KanbanBoard build() {
		return combineHomogenousChildren(ROOT_WORK_ITEM_ID, workItemTypes.getRoot());
	}

	/**
	 * Gets collection of workitems and generates backlog
	 * @return
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
	 * Wall
	 * @param workItemTypeTreeNode
	 * @param workItem
	 * @param workItemBefore
	 * @param workItemAfter
	 * @return
	 */
	private KanbanBoard build(TreeNode<WorkItemType> workItemTypeTreeNode, WorkItem workItem, WorkItem workItemBefore, WorkItem workItemAfter) {
		KanbanBoard board = new KanbanBoard(columns);
		if (isVisible(workItem)) {
			board.insert(workItem, workItemBefore, workItemAfter);
			// Build child boards and add to KanBan board
			for (TreeNode<WorkItemType> childType : workItemTypeTreeNode.getChildren()) {
				KanbanBoard childBoard = combineHomogenousChildren(workItem.getId(), childType);
				board.merge(childBoard);
			}
		}
		return board;
	}

	/**
	 * Gets the workitem children of the same type and build the board
	 * @param parentId
	 * @param childType
	 * @return
	 */
	private KanbanBoard combineHomogenousChildren(int parentId, TreeNode<WorkItemType> childType) {
		List<WorkItem> workItems = tree.getChildrenWithType(parentId, childType.getValue());

		if (childType.hasChildren()) {
			return stack(childType, workItems);
		} else {
			return pack(workItems);
		}
	}
	/**
	 * Leaves nice gap between different sub-workitems childboards
	 * @param type
	 * @param workItems
	 * @return
	 */
	private KanbanBoard stack(TreeNode<WorkItemType> type, List<WorkItem> workItems) {
		KanbanBoard board = new KanbanBoard(columns);

		List<WorkItem> list = columns.filter(workItems);

		for (int i = 0; i < list.size(); i++) {
			WorkItem workItem = list.get(i);
			WorkItem workItemBefore = (i - 1) >= 0 ? list.get(i - 1) : null;
			WorkItem workItemAfter = (i + 1) < list.size() ? list.get(i + 1) : null;
			KanbanBoard childBoard = build(type, workItem, workItemBefore, workItemAfter);
			board.stack(childBoard);
		}
		return board;
	}

	/**
	 * If there are no children, then don't bother leaving a gap, compress it.
	 * @param workItems
	 * @return
	 */
	private KanbanBoard pack(List<WorkItem> workItems) {
		KanbanBoard board = new KanbanBoard(columns);
		for (KanbanBoardColumn column : columns) {
			List<WorkItem> sublist = new KanbanBoardColumnList(column).filter(workItems);

			for (int i = 0; i < sublist.size(); i++) {
				WorkItem workItem = sublist.get(i);
				WorkItem workItemBefore = (i - 1) >= 0 ? sublist.get(i - 1) : null;
				WorkItem workItemAfter = (i + 1) < sublist.size() ? sublist.get(i + 1) : null;
				board.insert(workItem, workItemBefore, workItemAfter);
			}
		}
		return board;
	}

	private boolean isVisible(WorkItem workItem) {
		return columns.containsPhase(workItem.getCurrentPhase());
	}
}
