package com.metservice.kanban;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.metservice.kanban.utils.MessageUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.metservice.kanban.model.DefaultKanbanProject;
import com.metservice.kanban.model.KanbanBoardConfiguration;
import com.metservice.kanban.model.KanbanProject;
import com.metservice.kanban.model.KanbanProjectConfiguration;
import com.metservice.kanban.model.KanbanProjectConfigurationBuilder;
import com.metservice.kanban.model.TreeNode;
import com.metservice.kanban.model.WorkItemTree;
import com.metservice.kanban.model.WorkItemType;
import com.metservice.kanban.model.WorkItemTypeCollection;
import com.metservice.kanban.web.KanbanPersistence;

public class KanbanService {

    //Sets the KANBAN_HOME_PROPERTY name
    public static final String KANBAN_HOME_PROPERTY_NAME = "kanban.home";

    //Filters the files that start with a . i.e '.home'
    private static final NotFileFilter NO_DOT_DIRS_NO_FILES = new NotFileFilter(
        new OrFileFilter(
            new PrefixFileFilter("."),
            FileFileFilter.FILE
        ));

    //Sets the KANBAN_PROPERTIES_FILE name
    private static final String KANBAN_PROPERTIES_FILE_NAME = "kanban.properties";

    private final File home;
    private final String version;

    public KanbanService() {
        this(getKanbanHomeFromSystemProperty());
    }

    /**
     * This method is used as the constructor for {@link KanbanService}.
     * <p>
     * The kanbanHomePath is retrieved. If the path is <code>null</code> a File
     * is created with the path 'user.home.kanban'.
     * <p>
     * If the path is not <code>null</code> then a new {@link File} is created
     * using the pathname defined in the {@code KANBAN_HOME_PROPERTY_NAME}.
     * 
     * @return Returns a {@link File} object.
     */
    private static File getKanbanHomeFromSystemProperty() {
        String kanbanHomePath = System.getProperty(KANBAN_HOME_PROPERTY_NAME);
        if (kanbanHomePath == null) {
            return new File(System.getProperty("user.home"), ".kanban");
        } else {
            return new File(kanbanHomePath);
        }
    }

    /**
     * Version number is retrieved from the version.txt file
     * 
     * @return
     *         A {@link String} which is the version number of the program
     *         <p>
     *         <code>""</code>, if an {@link IOException} is thrown.
     * @exception If
     *                an {@link IOException} is encountered a <code>""</code> is
     *                returned
     */
    private String loadKanbanVersion() {
        Properties versionProperties = new Properties();
        try {
            versionProperties.load(getClass().getResourceAsStream("/version.txt"));
        } catch (IOException e) {
            return "";
        }
        return versionProperties.getProperty("version");
    }

    /**
     * Constructor for {@link KanbanService}.
     * 
     * @param home
     *            - When passed the home File the <code>home</code> and
     *            <code>version</code> fields are set.
     */
    public KanbanService(File home) {
        this.home = home;
        this.version = loadKanbanVersion();
    }

    /**
     * @return {@link String} - returns the <code>home</code> field.
     */
    public File getHome() {
        return home;
    }

    /**
     * @return {@link String} - returns the <code>version</code> details.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns a collection of projects who's names do not start with
     * <code>"."</code>
     * 
     * @return {@link Collection} - returns a <code>Collection(String)</code> of
     *         projects.
     */
    public Collection<String> getProjects() {

        if (home.exists()) {
            List<String> result = asList(home.list(NO_DOT_DIRS_NO_FILES));

            Collections.sort(result, String.CASE_INSENSITIVE_ORDER);

            return result;
        } else {
            return new ArrayList<String>();
        }
    }

    public Collection<String> getFilteredProjects() {
        Collection<String> projects = getProjects();
        Collection<String> filteredProjects = new ArrayList<String>();
        for (String p : projects) {
            if (!isFilteredProject(p)) {
                filteredProjects.add(p);
            }
        }
        return filteredProjects;
    }

    private boolean isFilteredProject(String p) {

        return p.startsWith("_");
    }

    /**
     * Retrieves project information of the specified project then takes the
     * information and creates a {@link KanbanProject} object to be returned.
     * 
     * @param projectName
     *            - {@link String} name of project to be retrieved.
     * @return {@link KanbanProject} a new KanbanProject created from retrieved
     *         information.
     * @throws IOException
     *             if an error is encountered an {@link IOException} is thrown.
     */
    public KanbanProject getKanbanProject(String projectName) throws IOException {
        KanbanProjectConfiguration configuration = getProjectConfiguration(projectName);

        TreeNode<WorkItemType> rootWorkItemType = configuration.getRootWorkItemType();
        KanbanBoardConfiguration phaseSequences = configuration.getPhaseSequences();

        //Creates a new KanbanPersistence object using the configuration file.
        //Then, creates a WorkItemTree using the persistence file.
        KanbanPersistence persistence = new KanbanPersistence(configuration);
        WorkItemTree tree = persistence.read();

        WorkItemTypeCollection workItemTypes = createWorkItemTypeCollection(rootWorkItemType);
        return new DefaultKanbanProject(workItemTypes, phaseSequences, tree, persistence, projectName);
    }

    /**
     * Method to create a {@link WorkItemTypeCollection} from the WorkItemType
     * {@link TreeNode}
     * 
     * @param rootWorkItemType
     * @return {@link WorkItemTypeCollection} - the newly created object.
     */
    private WorkItemTypeCollection createWorkItemTypeCollection(TreeNode<WorkItemType> rootWorkItemType) {
        return new WorkItemTypeCollection(rootWorkItemType);
    }

    /**
     * Method to retrieve the Project configuration.
     * <p>
     * Creates a {@link KanbanProjectConfigurationBuilder} and then uses this to
     * build the configuration.
     * <p>
     * The configuration is then returned.
     * 
     * @param {@link String} projectName
     * @return {@link KanbanProjectConfiguration} - the project configuration.
     * @throws IOException
     */
    public KanbanProjectConfiguration getProjectConfiguration(String projectName) throws IOException {
        KanbanProjectConfigurationBuilder configurationBuilder = new KanbanProjectConfigurationBuilder(home,
            projectName);
        return configurationBuilder.buildConfiguration();
    }

    /**
     * Creates a new project with the given Name and Settings.
     * It creates a folder and saves the properties file in the folder.
     * <p>
     * If an existing project has the same name as <code>newProjectName</code>
     * then an {@link IllegalArgumentException} is thrown and the project is not
     * created.
     * <p>
     *
     * @param newProjectName  project name
     * @param settings  project settings
     * @throws IOException
     */
    public void createProject(String newProjectName, String settings) throws IOException {
        File newProjectHome = new File(home, newProjectName);

        if (newProjectHome.exists()) {
            throw new IllegalArgumentException("cannot create a project with the same name as an existing project: "
                + newProjectName);
        }

        //Creates the project folder and writes the settings to '[newProjectName].kanban.properties'
        try {
            if ( ! newProjectHome.mkdir() )  {
                throw new IllegalArgumentException("cannot create project " + MessageUtils.decorateSingleQuotes(newProjectName) +". "
                                                + "Please check permissions on folder " + MessageUtils.decorateSingleQuotes( newProjectHome.getParentFile().getAbsolutePath()) );
            }
        }
        catch (SecurityException e) {
            throw new IllegalArgumentException("cannot create project " + MessageUtils.decorateSingleQuotes(newProjectName) +". "
                    + "Please check permissions on folder " + MessageUtils.decorateSingleQuotes( newProjectHome.getParentFile().getAbsolutePath()) );
        }
        File file = new File(newProjectHome, KANBAN_PROPERTIES_FILE_NAME);
        writeStringToFile(file, settings);
    }

    /**
     * Edits a given project with the given name and settings.
     * It overwrites the existing .properties file for that project.
     * If a .properties file for that project does not exist, the project is
     * not edited.
     * 
     * @param projectName
     *            - the project to edit
     * @param settings
     *            - the new settings
     * @throws IOException
     */
    public void editProject(String projectName, String settings) throws IOException {
        File projectHome = new File(home, projectName);

        if (projectHome.exists()) {
            File file = new File(projectHome, KANBAN_PROPERTIES_FILE_NAME);
            writeStringToFile(file, settings);
        } else {
            throw new IllegalArgumentException("cannot edit a project that does not exist: " + projectName);
        }
    }


    /**
     * Rename a project.
     * 
     * @param projectName
     *            the project name
     * @param newProjectName
     *            the new project name
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void renameProject(String projectName, String newProjectName) throws IOException {

        File projectHome = new File(home, projectName);
        File newProjectHome = new File(home, newProjectName);

        if (newProjectHome.exists()) {
            throw new IllegalArgumentException("project name already exists: " + newProjectName);
        }

        projectHome.renameTo(newProjectHome);
    }

    public void setColumnWipLimit(String projectName, WorkItemType workItemtype, String columnName, Integer wipLimit)
        throws IOException {

        getProjectConfiguration(projectName).getKanbanPropertiesFile().setColumnWipLimit(workItemtype, columnName,
            wipLimit);
    }

    public boolean renameColumn(String projectName, WorkItemType workItemType, String columnName, String newColumnName)
        throws IOException
    {
        // check if column exists
        if (!workItemType.containsPhase(columnName)) {
            return false;
        }
        if (workItemType.containsPhase(newColumnName)) {
            return false;
        }
        // rename column in a properties file
        try {
            getProjectConfiguration(projectName).getKanbanPropertiesFile().renameColumn(workItemType, columnName,
                newColumnName);
        } catch (IllegalArgumentException e) {
            return false;
        }
        // rename column in a CSV file

        KanbanProjectConfiguration configuration = getProjectConfiguration(projectName);

        CSVReader csvReader = new CSVReader(new FileReader(configuration.getDataFile(workItemType)));
        List<String[]> items;
        try {
            items = csvReader.readAll();
            for (int i = 0; i < items.get(0).length; i++) {
                if (items.get(0)[i].equals(columnName)) {
                    items.get(0)[i] = newColumnName;
                    break;
                }
            }
        } finally {
            csvReader.close();
        }

        CSVWriter csvWriter = new CSVWriter(new FileWriter(configuration.getDataFile(workItemType)));
        try {
            csvWriter.writeAll(items);
        } finally {
            csvWriter.close();
        }
        return true;
    }

}
