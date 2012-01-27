package com.metservice.kanban;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.SystemUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.EstimatesDao;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.EstimatesProject;

public class EstimatesDaoTest {

    EstimatesDao estimatesDao;
    KanbanService service;

    @Before
    public void setUp() {
        estimatesDao = new EstimatesDao();

        service = new KanbanService(new File(SystemUtils.getUserDir(), "/target/test-classes"));
        estimatesDao.setKanbanService(service);
    }

    @Test
    public void afterUpdateProjectEstimatesShouldReturnProperProject() throws IOException {
        String projectname = "test-project";
        
        EstimatesProject project = estimatesDao.loadProject(projectname);
        
        assertNotNull(project);

        project.setBudget(1234);
        project.setEstimatedCostPerPoint(10);
        
        EstimatesProject p2 = estimatesDao.loadProject(projectname);
        
        estimatesDao.storeProjectEstimates(project);
        
        p2 = estimatesDao.loadProject(projectname);
        
        assertEquals(1234, p2.getBudget());
        assertEquals(100, p2.getCostSoFar());
        assertEquals(10, p2.getEstimatedCostPerPoint());
    }

    @Test
    public void fileWithProjectEstimatesDoesNotExistReturnZeros() throws IOException {
        String projectname = "empty-project";

        service = mock(KanbanService.class);
        estimatesDao.setKanbanService(service);

        EstimatesProject project = estimatesDao.loadProject(projectname);

        assertNotNull(project);

        assertEquals(0, project.getBudget());
        assertEquals(0, project.getCostSoFar());
        assertEquals(0, project.getEstimatedCostPerPoint());
    }

    @Test
    public void testGetCostDailyMap() {

        Map<LocalDate, Integer> result;

        result = EstimatesDao.getCostDailyMap("");
        assertEquals(0, result.size());

        result = EstimatesDao.getCostDailyMap(null);
        assertEquals(0, result.size());

        result = EstimatesDao.getCostDailyMap("2012-01-11|5");

        assertEquals(1, result.size());
        assertEquals(5, (int) result.get(LocalDate.parse("2012-01-11")));

        result = EstimatesDao.getCostDailyMap("2012-01-10|10;2012-01-12|20");
        assertEquals(2, result.size());
        assertEquals(10, (int) result.get(LocalDate.parse("2012-01-10")));
        assertEquals(20, (int) result.get(LocalDate.parse("2012-01-12")));

        result = EstimatesDao.getCostDailyMap("2012-01-10|10;2012-01-12|20;");
        assertEquals(2, result.size());
        assertEquals(10, (int) result.get(LocalDate.parse("2012-01-10")));
        assertEquals(20, (int) result.get(LocalDate.parse("2012-01-12")));

    }

    @Test
    public void testGetCostDailyStr() {
        Map<LocalDate, Integer> data = new TreeMap<LocalDate, Integer>();
        String result = EstimatesDao.getCostDailyStr(data);
        assertEquals("", result);

        data.put(LocalDate.parse("2012-01-10"), 5);
        result = EstimatesDao.getCostDailyStr(data);
        assertEquals("2012-01-10|5;", result);

        data.put(LocalDate.parse("2012-01-20"), 7);
        result = EstimatesDao.getCostDailyStr(data);
        assertEquals("2012-01-10|5;2012-01-20|7;", result);
    }
}
