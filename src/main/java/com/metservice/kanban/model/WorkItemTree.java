package com.metservice.kanban.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface WorkItemTree {

    void addWorkItem(WorkItem workItem);

    void addWorkItems(WorkItem... workItem);

    WorkItem getWorkItem(int id);

    WorkItem getParent(WorkItem workItem);

    List<WorkItem> getChildren(int parentId);

    List<WorkItem> getWorkItemList();

    Map<Integer, WorkItem> getWorkItemsById();

    List<WorkItem> getWorkItemsOfType(WorkItemType type, String workStream);

    WorkItem getTopLevelAncestor(WorkItem workItem);

    int getNewId();

    void reorder(WorkItem workItem, List<WorkItem> workItemList); 

    void move(WorkItem workItem, WorkItem target, boolean after); 

    List<WorkItem> getChildrenWithType(int parentId, WorkItemType childType, String workStream);

    void delete(int id);

    Collection<WorkItem> getParentAlternatives(WorkItem workItem);

    void reparent(int id, int newParentId);
}
