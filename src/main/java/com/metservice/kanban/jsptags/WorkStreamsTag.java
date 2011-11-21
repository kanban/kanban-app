package com.metservice.kanban.jsptags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import com.metservice.kanban.model.KanbanProject;

public class WorkStreamsTag extends TagSupport {

    private static final long serialVersionUID = 3751728956376722504L;

    private String name;
    private KanbanProject project;

    public int doStartTag() throws JspException {

        if (project != null) {

            try {
                pageContext.getOut().write("var " + name + " = [");

                for (String ws : project.getWorkStreams()) {
                    ws = ws.trim();
                    pageContext.getOut().write("'" + ws + "',");
                }
                pageContext.getOut().write("'' ];\n");

                pageContext.getOut().write("" +
                    "       $(function() {\n" +
                    "               $('#workStreams').tagit({\n" +
                    "                       tagSource   : workStreams,\n" +
                    "                       triggerKeys : ['enter','comma'],\n" +
                    "                       initialTags : []\n" +
                    "               });\n" +
                    "        });\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return EVAL_PAGE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KanbanProject getProject() {
        return project;
    }

    public void setProject(KanbanProject project) {
        this.project = project;
    }

}
