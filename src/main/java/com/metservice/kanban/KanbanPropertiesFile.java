package com.metservice.kanban;

import static java.lang.String.format;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import com.metservice.kanban.model.BoardIdentifier;
import com.metservice.kanban.model.HtmlColour;
import com.metservice.kanban.model.WorkItemType;

//TODO This class needs unit tests.

/**
 * Reads and parses a project's .properties file and builds the project
 * configuration and layout.
 * 
 * @author Janella Espinas, Chris Cooper
 */
public class KanbanPropertiesFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(KanbanPropertiesFile.class);

    private File file;
    private Properties properties = new Properties();

    public KanbanPropertiesFile(File file) throws IOException {
        this(new FileReader(file));

        this.file = file;
    }

    public KanbanPropertiesFile(Reader reader) throws IOException {
        try {
            properties.load(reader);
        } finally {
            reader.close();
        }
    }

    private void storeProperties() throws IOException {
        if (file != null) {
            FileWriter writer = new FileWriter(file);
            try {
                properties.store(writer, "");
            } finally {
                writer.close();
            }
        }
    }

    public String[] getWorkItemTypes() throws IOException {
        return getCommaSeparatedStrings("workItemTypes");
    }

    public String getParentWorkItemType(String workItemType) throws IOException {
        String propertyKey = format("workItemTypes.%s.parent", workItemType);
        return getString(propertyKey);
    }

    public boolean isChildWorkItemType(String name, String possibleChildName) throws IOException {
        return getParentWorkItemType(possibleChildName).equals(name);
    }

    /**
     * Returns the phases of the project for the wall.
     * 
     * @param boardType
     * @return
     * @throws IOException
     */
    public String[] getPhaseSequence(BoardIdentifier boardType) throws IOException {
        String propertyKey = format("boards.%s", boardType.getName());
        return getCommaSeparatedStrings(propertyKey);
    }

    /**
     * Returns the phases for a particular workItemType (eg, feature, story).
     * 
     * @param workItemType
     * @return
     * @throws IOException
     */
    public String[] getPhases(String workItemType) throws IOException {
        String propertyKey = format("workItemTypes.%s.phases", workItemType);
        return getCommaSeparatedStrings(propertyKey);
    }

    /**
     * Returns the WIP limits for phases
     * 
     * @param boardType
     * @return
     * @throws IOException
     */
    public String[] getPhaseWIPLimit(String workItemType) throws IOException {
        String propertyKey = format("workItemTypes.%s.wipLimit", workItemType);
        try {
            return getCommaSeparatedStrings(propertyKey);
        } catch (Exception e) {
            return new String[0];
        }
    }

    public HtmlColour getWorkItemTypeCardColour(String workItemType) throws IOException {
        String propertyKey = format("workItemTypes.%s.cardColour", workItemType);
        return new HtmlColour(getString(propertyKey));
    }

    public HtmlColour getWorkItemTypeBackgroundColour(String workItemType) throws IOException {
        String propertyKey = format("workItemTypes.%s.backgroundColour", workItemType);
        return new HtmlColour(getString(propertyKey));
    }

    private String[] getCommaSeparatedStrings(String propertyKey) throws IOException {
        String commaSeparatedString = getString(propertyKey);
        return StringUtils.splitPreserveAllTokens(commaSeparatedString, ',');
    }

    /**
     * Gets the value of a given propertyKey from the values in the property
     * map.
     * 
     * @param propertyKey
     * @return
     * @throws IOException
     */
    String getString(String propertyKey) {
        String propertyValue = properties.getProperty(propertyKey);
        //        if (propertyValue == null) {
        //            throw new IOException("property \"" + propertyKey + "\" missing from " + file);
        //        }
        return propertyValue;
    }

    public String getContentAsString() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        StringBuffer sb = new StringBuffer();
        String str;
        while ((str = in.readLine()) != null) {
            sb.append(str + "\n");
        }
        in.close();
        return sb.toString();
    }

    public void setColumnWipLimit(WorkItemType workItemType, String columnName, Integer wipLimit) throws IOException {
        
        LOGGER.info("Setting WIP limit for column {}.{} to {}",
            new Object[] {
                workItemType.toString(),
                columnName,
                wipLimit
            });
        
        String[] phases = getPhases(workItemType.toString());
        String[] wipLimits = getCommaSeparatedStrings("workItemTypes." + workItemType + ".wipLimit");
        if (wipLimits == null) {
            wipLimits = new String[phases.length];
        }
        if (wipLimits.length < phases.length) {
            wipLimits = Arrays.copyOf(wipLimits, phases.length);
        }

        for (int i = 0; i < phases.length; i++) {
            if (phases[i].equals(columnName)) {
                if (wipLimit == null) {
                    wipLimits[i] = "";
                } else {
                    wipLimits[i] = wipLimit.toString();
                }
                break;
            }
        }
        String wipLimitsStr = StringUtils.join(wipLimits, ",");
        properties.put(String.format("workItemTypes.%s.wipLimit", workItemType.getName()), wipLimitsStr);
        
        storeProperties();
    }

    public void renameColumn(WorkItemType workItemType, String columnName, String newColumnName) throws IOException {
        LOGGER.info("Renaming column for WorkItem {} from {} to {} in properties file", new Object[] {
            workItemType,
            columnName,
            newColumnName});
        
        boolean columnFound = false;

        String[] phases = getCommaSeparatedStrings(String.format("workItemTypes.%s.phases", workItemType.getName()));

        for (int i = 0; i < phases.length; i++) {
            if (phases[i].equals(columnName)) {
                phases[i] = newColumnName;
                columnFound = true;
                break;
            }
        }
        Assert.isTrue(columnFound, String.format("Column %s cannot be found in properties file", columnName));

        String newPhases = StringUtils.join(phases, ",");
        properties.put(String.format("workItemTypes.%s.phases", workItemType.getName()), newPhases);
        
        String[] boardPhases = getCommaSeparatedStrings("boards.wall");

        for (int i = 0; i < boardPhases.length; i++) {
                if (boardPhases[i].equals(columnName)) {
                    boardPhases[i] = newColumnName;
                }
        }
        String newboardPhases = StringUtils.join(boardPhases, ",");
        properties.put("boards.wall", newboardPhases);

        storeProperties();
    }
}
