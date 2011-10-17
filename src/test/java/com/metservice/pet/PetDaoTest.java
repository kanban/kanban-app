package com.metservice.pet;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Test;
import com.metservice.kanban.KanbanService;

public class PetDaoTest {

    PetDao petDao;
    KanbanService service;

    @Before
    public void setUp() {
        petDao = new PetDao();
        service = new KanbanService(new File(SystemUtils.getUserDir(), "/target/test-classes"));
        petDao.setKanbanService(service);
    }

    @Test
    public void afterUpdateProjectEstimatesShouldReturnProperProject() throws IOException {
        String projectname = "test-project";
        
        Project project = petDao.loadProject(projectname);
        
        assertNotNull(project);

        project.setBudget(1234);
        project.setCostSoFar(100);
        project.setEstimatedCostPerPoint(10);
        
        Project p2 = petDao.loadProject(projectname);
        
        //        assertNotSame(1234, p2.getBudget());
        //        assertNotSame(100, p2.getCostSoFar());
        //        assertNotSame(10, p2.getEstimatedCostPerPoint());

        petDao.storeProjectEstimates(project);
        
        p2 = petDao.loadProject(projectname);
        
        assertEquals(1234, p2.getBudget());
        assertEquals(100, p2.getCostSoFar());
        assertEquals(10, p2.getEstimatedCostPerPoint());
    }

    @Test
    public void fileWithProjectEstimatesDoesNotExistReturnZeros() throws IOException {
        String projectname = "empty-project";

        Project project = petDao.loadProject(projectname);

        assertNotNull(project);

        assertEquals(0, project.getBudget());
        assertEquals(0, project.getCostSoFar());
        assertEquals(0, project.getEstimatedCostPerPoint());
    }
}
