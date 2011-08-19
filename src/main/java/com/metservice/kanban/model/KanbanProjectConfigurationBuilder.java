package com.metservice.kanban.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.metservice.kanban.KanbanPropertiesFile;

//TODO This class needs unit tests.
//TODO perhaps rename to factory and replace build methods with create methods

/**
 * 
 * WIP Limit by Nicholas Malcolm and Chris Cooper
 */
public class KanbanProjectConfigurationBuilder {

    private final File projectHome;
    private final KanbanPropertiesFile properties;

    public KanbanProjectConfigurationBuilder(File kanbanHome,String projectName) throws IOException {
        projectHome = new File(kanbanHome, projectName);
        properties = new KanbanPropertiesFile(new File(projectHome, "kanban.properties"));
    }

    public KanbanProjectConfiguration buildConfiguration() throws IOException {
        TreeNode<WorkItemType> rootWorkItemType = getRootWorkItemType();
        WorkItemTypeCollection workItemTypes = new WorkItemTypeCollection(rootWorkItemType);
        KanbanBoardConfiguration boardDefinitions = getBoardDefinitions(workItemTypes);

        return new KanbanProjectConfiguration(projectHome, boardDefinitions, rootWorkItemType, workItemTypes);
    }

    private KanbanBoardConfiguration getBoardDefinitions(WorkItemTypeCollection workItemTypes) throws IOException {
        Map<String, WorkItemType> workItemTypesByPhase = new HashMap<String, WorkItemType>();
        for (WorkItemType type : workItemTypes) {
            String[] phases = properties.getPhases(type.getName());
            for (String phase : phases) {
                workItemTypesByPhase.put(phase, type);
            }
        }
        
        KanbanBoardConfiguration phaseSequences = new KanbanBoardConfiguration();
        for (BoardIdentifier board : BoardIdentifier.values()) {
            List<KanbanBoardColumn> columns = new ArrayList<KanbanBoardColumn>();
            String[] boardPhases = properties.getPhaseSequence(board);
            String[] columnLimits = properties.getPhaseWIPLimit(workItemTypesByPhase.get(boardPhases[0]).getName());
            String phase = "";
            int wipLimit = -1;
            for(int i = 0; i < boardPhases.length;i++){
            	phase = boardPhases[i];
            	try{
            		wipLimit = Integer.parseInt(columnLimits[i]);
            	}catch (Exception e) {
					// No limit was specified, or it was ""
            		wipLimit = -1;
				}

            	columns.add(new KanbanBoardColumn(workItemTypesByPhase.get(phase), phase, wipLimit));
            }

            phaseSequences.add(board, new KanbanBoardColumnList(columns));
        }
        return phaseSequences;
    }

    public TreeNode<WorkItemType> getRootWorkItemType() throws IOException {
        // There should only be one root work item type (something like product, epic, MMF, etc.) The configuration
        // file format does not enforce this so we initially assume there are multiple roots and throw exceptions if
        // we find none or more than one.
        List<TreeNode<WorkItemType>> roots = getChildWorkItemTypeTreeNodes("root");

        if (roots.isEmpty()) {
            throw new IOException("no root work item type");
        } else if (roots.size() > 1) {
            throw new IOException("multiple root work item types: " + roots);
        }
        return roots.get(0);
    }

    private List<TreeNode<WorkItemType>> getChildWorkItemTypeTreeNodes(String name) throws IOException {
        List<TreeNode<WorkItemType>> children = new ArrayList<TreeNode<WorkItemType>>();

        for (String possibleChildName : properties.getWorkItemTypes()) {
            if (properties.isChildWorkItemType(name, possibleChildName)) {
                children.add(createWorkItemTypeTreeNode(possibleChildName));
            }
        }
        return children;
    }

    private TreeNode<WorkItemType> createWorkItemTypeTreeNode(String name) throws IOException {
        WorkItemType workItemType = new WorkItemType(properties.getPhases(name));
        workItemType.setName(name);
            
        workItemType.setCardColour(properties.getWorkItemTypeCardColour(name));
        workItemType.setBackgroundColour(properties.getWorkItemTypeBackgroundColour(name));
        
        List<TreeNode<WorkItemType>> children = getChildWorkItemTypeTreeNodes(name);

        return TreeNode.create(WorkItemType.class, workItemType, children);
    }

    public Iterable<String> getPhases(Iterable<WorkItemType> workItemTypes) throws IOException {
        Collection<String> phases = new ArrayList<String>();

        for (WorkItemType workItemType : workItemTypes) {
            for (String phase : properties.getPhases(workItemType.getName())) {
                phases.add(phase);
            }
        }
        return phases;
    }
}
