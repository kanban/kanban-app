package com.metservice.kanban.utils;


import com.metservice.kanban.model.WorkItem;

import java.util.List;

public class WorkItemUtils {
    public static WorkItem topWorkItem(List<WorkItem> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}
