package com.metservice.kanban;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.EstimatesDao;
import com.metservice.kanban.KanbanService;
import com.metservice.kanban.model.EstimatesProject;

public class EstimatesDaoTest {

    EstimatesDao petDao;
    KanbanService service;

    @Before
    public void setUp() {
        petDao = new EstimatesDao();

        service = new KanbanService(new File(SystemUtils.getUserDir(), "/target/test-classes"));
        petDao.setKanbanService(service);
    }

    @Test
    public void afterUpdateProjectEstimatesShouldReturnProperProject() throws IOException {
        String projectname = "test-project";
        
        EstimatesProject project = petDao.loadProject(projectname);
        
        assertNotNull(project);

        project.setBudget(1234);
        project.setCostSoFar(100);
        project.setEstimatedCostPerPoint(10);
        
        EstimatesProject p2 = petDao.loadProject(projectname);
        
        petDao.storeProjectEstimates(project);
        
        p2 = petDao.loadProject(projectname);
        
        assertEquals(1234, p2.getBudget());
        assertEquals(100, p2.getCostSoFar());
        assertEquals(10, p2.getEstimatedCostPerPoint());
    }

    @Test
    public void fileWithProjectEstimatesDoesNotExistReturnZeros() throws IOException {
        String projectname = "empty-project";

        service = mock(KanbanService.class);
        petDao.setKanbanService(service);

        EstimatesProject project = petDao.loadProject(projectname);

        assertNotNull(project);

        assertEquals(0, project.getBudget());
        assertEquals(0, project.getCostSoFar());
        assertEquals(0, project.getEstimatedCostPerPoint());
    }
}
