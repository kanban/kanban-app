package com.metservice.kanban.jsptags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import com.metservice.kanban.model.WorkItem;

public class WorkStreamsInfoTag extends TagSupport {

    private static final long serialVersionUID = 3751728956376722504L;

    private WorkItem workItem;
    private String cssClass;

    public int doStartTag() throws JspException {

        try {
            for (String s : workItem.getWorkStreams()) {
                pageContext.getOut().write(String.format("<span class='%s'>[%s]</span> ", cssClass, s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EVAL_PAGE;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

}
