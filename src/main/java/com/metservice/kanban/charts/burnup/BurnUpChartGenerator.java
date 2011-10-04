package com.metservice.kanban.charts.burnup;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.joda.time.LocalDate;
import com.metservice.kanban.model.WorkItem;
import com.metservice.kanban.model.WorkItemType;

public interface BurnUpChartGenerator {

    void generateBurnUpChart(WorkItemType type, List<WorkItem> workItems, LocalDate startDate, LocalDate date, OutputStream outputStream)
        throws IOException;

}
