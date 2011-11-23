package com.metservice.kanban.tests.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsEmptyCollection;
import com.metservice.kanban.jwebunit.EndToEndTest;
import com.metservice.kanban.model.WorkItem;

public class TestUtils {

    public static void createTestProject(File home, String projectName, String sourceResourcePath) throws IOException {
        File projectDirectory = new File(home, projectName);
        
        copyToTestProject(projectDirectory, sourceResourcePath,"kanban.properties");
        copyToTestProject(projectDirectory, sourceResourcePath,"feature.csv");
        copyToTestProject(projectDirectory, sourceResourcePath,"story.csv");
    }

    private static void copyToTestProject(File projectDirectory, String sourceResourcePath,String resourceName) throws IOException {
    	String sourceFileResourcePath = sourceResourcePath + resourceName;
        File destinationFile = new File(projectDirectory, resourceName);

        writeResourceToFile(EndToEndTest.class, sourceFileResourcePath, destinationFile);
    }

    private static void writeResourceToFile(Class<?> context, String resourcePath, File file) throws IOException {
        InputStream in = context.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new FileNotFoundException(resourcePath);
        }

        OutputStream out = null;
        try {
            out = FileUtils.openOutputStream(file);

            IOUtils.copy(in, out);

            out.close();
            in.close();
        } finally {
            // Attempt to close the streams again in case something went wrong. Be careful not to discard any earlier
            // exception.
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }
    
    public static Matcher<Collection<WorkItem>> emptyWorkItemList() {
        return IsEmptyCollection.<WorkItem> empty();
    }
}
