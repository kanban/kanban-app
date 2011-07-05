package com.metservice.kanban;

import static java.lang.String.format;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import com.metservice.kanban.model.BoardIdentifier;
import com.metservice.kanban.model.Colour;

//TODO This class needs unit tests.

public class KanbanPropertiesFile {

    private File file;
    private Properties properties = new Properties();

    public KanbanPropertiesFile(File file) throws IOException {
        this.file = file;

        FileReader reader = new FileReader(file);
        try {
            properties.load(reader);
            reader.close();
        } finally {
            closeQuietly(reader);
        }
    }

    private static void closeQuietly(Closeable closable) throws IOException {
        try {
            closable.close();
        } finally {
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

    public String[] getPhaseSequence(BoardIdentifier boardType) throws IOException {
        String propertyKey = format("boards.%s", boardType.getName());
        return getCommaSeparatedStrings(propertyKey);
    }

    public String[] getPhases(String workItemType) throws IOException {
        String propertyKey = format("workItemTypes.%s.phases", workItemType);
        return getCommaSeparatedStrings(propertyKey);
    }

    public Colour getWorkItemTypeCardColour(String workItemType) throws IOException {
        String propertyKey = format("workItemTypes.%s.cardColour", workItemType);
        return new Colour(getString(propertyKey));
    }

    public Colour getWorkItemTypeBackgroundColour(String workItemType) throws IOException {
        String propertyKey = format("workItemTypes.%s.backgroundColour", workItemType);
        return new Colour(getString(propertyKey));
    }

    private String[] getCommaSeparatedStrings(String propertyKey) throws IOException {
        String commaSeparatedString = getString(propertyKey);
        return commaSeparatedString.split(",");
    }

    private String getString(String propertyKey) throws IOException {
        String propertyValue = properties.getProperty(propertyKey);
        if (propertyValue == null) {
            throw new IOException("property \"" + propertyKey + "\" missing from " + file);
        }
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
}
