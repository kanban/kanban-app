package com.metservice.kanban;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import com.metservice.kanban.model.DefaultKanbanProject;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.KanbanProjectConfiguration;
import com.metservice.kanban.model.KanbanProjectConfigurationBuilder;
import com.metservice.kanban.model.KanbanBoardConfiguration;
import com.metservice.kanban.model.TreeNode;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.model.WorkItemTypeCollection;
import com.metservice.kanban.web.KanbanPersistence;

public class KanbanService {

    public static final String KANBAN_HOME_PROPERTY_NAME = "kanban.home";

    private static final NotFileFilter NO_DOT_FILES = new NotFileFilter(new PrefixFileFilter("."));
    private static final String KANBAN_PROPERTIES_FILE_NAME = "kanban.properties";

    private final File home;
    private final String version;

    public KanbanService() {
        this(getKanbanHomeFromSystemProperty());
    }
    
    private static File getKanbanHomeFromSystemProperty() {
        String kanbanHomePath = System.getProperty(KANBAN_HOME_PROPERTY_NAME);
        if (kanbanHomePath == null) {
            return new File(System.getProperty("user.home"), ".kanban");
        } else {
            return new File(kanbanHomePath);
        }
    }
    
    private String loadKanbanVersion() {
        Properties version = new Properties();
        try {
            version.load(getClass().getResourceAsStream("/version.txt"));
        } catch (IOException e) {
            return "";
        }
        return version.getProperty("version");
    }

    public KanbanService(File home) {
        this.home = home;
        this.version = loadKanbanVersion();
    }
    
    public File getHome() {
        return home;
    }
    
    public String getVersion() {
        return version;
    }

    public Collection<String> getProjects() {
        String[] list = home.list(NO_DOT_FILES);
        return asList(list);
    }

    public KanbanProject getKanbanProject(String projectName) throws IOException {
        KanbanProjectConfiguration configuration = getProjectConfiguration(projectName);

        TreeNode<WorkItemType> rootWorkItemType = configuration.getRootWorkItemType();
        KanbanBoardConfiguration phaseSequences = configuration.getPhaseSequences();

        KanbanPersistence persistence = new KanbanPersistence(configuration);
        WorkItemTree tree = persistence.read();

        WorkItemTypeCollection workItemTypes = createWorkItemTypeCollection(rootWorkItemType);
        return new DefaultKanbanProject(workItemTypes, phaseSequences, tree, persistence);
    }

    private WorkItemTypeCollection createWorkItemTypeCollection(TreeNode<WorkItemType> rootWorkItemType) {
        return new WorkItemTypeCollection(rootWorkItemType);
    }

    public KanbanProjectConfiguration getProjectConfiguration(String projectName) throws IOException {
        KanbanProjectConfigurationBuilder configurationBuilder = new KanbanProjectConfigurationBuilder(home,
            projectName);
        return configurationBuilder.buildConfiguration();
    }

    public void createProject(String newProjectName, String settings) throws IOException {
        File newProjectHome = new File(home, newProjectName);

        if (newProjectHome.exists()) {
            throw new IllegalArgumentException("cannot create a project with the same name as an existing project: "
                + newProjectName);
        }

        newProjectHome.mkdir();
        File file = new File(newProjectHome, KANBAN_PROPERTIES_FILE_NAME);
        writeStringToFile(file, settings);
    }
}
