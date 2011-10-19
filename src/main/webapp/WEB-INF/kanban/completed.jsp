<%@page import="com.metservice.kanban.model.WorkItemType"%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.awt.Color"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.commons.collections.ListUtils"%>
<%@page import="com.metservice.kanban.model.HtmlColour"%>
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.KanbanCell"%>
<%@page import="com.metservice.kanban.model.KanbanBoard"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.BoardIdentifier"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumn"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumnList"%>
<%@page import="com.metservice.kanban.charts.KanbanDrawingSupplier"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-1.6.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/header.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/header.css"/>

<title>Kanban</title>
<%
    String scrollTopParam = (String) request.getAttribute("scrollTop");
    int scrollTo = 0;
    if (scrollTopParam != null)
        scrollTo = Integer.parseInt(scrollTopParam);
%>

<script type="text/javascript">
//<![CDATA[
			function setPosition() {
			  window.scrollTo(0,<%=scrollTo%>);
			}

			function edit(id){
			 document.forms["form"].action = getBoard() + "/edit-item?id=" + id;
			 document.forms["form"].submit();
			 
			}
//]]> 
		</script>

<%
    KanbanProject project = (KanbanProject) request.getAttribute("project");
    String boardType = (String) request.getAttribute("boardType");
    WorkItemType type = (WorkItemType) request.getAttribute("type");
    BoardIdentifier board = BoardIdentifier.valueOf(boardType.toUpperCase());
    
    //There doesn't appear to be a straightforward way of always getting
    //the wall columns so this is hardcoded.  We need the wallBoard columns
    //particularly so we can display the time spent in each wall phase.
    BoardIdentifier wallBoard = BoardIdentifier.valueOf("WALL");
    KanbanBoardColumnList wallColumns = project.getColumns(wallBoard);
    List<String> wallPhases = new ArrayList<String>();
    for (KanbanBoardColumn column : wallColumns) {
            wallPhases.add(column.getPhase());
    }

    List<String> itemPhases = type.getPhases();
    List<String> phases = ListUtils.retainAll(itemPhases, wallPhases);
    
    HtmlColour[] htmlColours = KanbanDrawingSupplier.getHtmlColours(phases.size());
%>

<style type="text/css">
.kanban {
	margin: 10px 0px 0px 0px;
	background: whitesmoke;
	border-collapse: collapse;
}

.age-legend {
    height: 10px;
    padding-left: 3px;
    padding-right: 3px;
}

.age-item {
    height: 13px;
    float: left;
    font-size: 11px;
    text-align: center;
}

.age-item:hover {
    font-weight: bold;
}

.editIcon {
	-moz-opacity: 1;
	opacity: 1;
	width: 16px;
	height: 16px;
}

.editIcon:hover	{
	-moz-opacity: 0.5;
	opacity: 0.5;
}

.horizontalLine {
	border-top: 2px black solid;
}

.customizedHeader {
	background: ${type.cardColour};
	border-top:1px #989898 dotted;
    border-left:1px #989898 dotted;
    border-right:1px #989898 dotted;
    width: 164px;
	height:30px;
	font-family:verdana;
	font-size:14px;
	color:black;
}

.subHeader {
	background: ${type.cardColour};
	border-bottom:1px #989898 dotted;
    border-left:1px #989898 dotted;
    border-right:1px #989898 dotted;
	height:10px;
	font-family:verdana;
	font-size:10px;
	color:black;
}

.row:hover {
	border: 1px black solid;
	background: ${type.cardColour};
	margin: 1px 1px 1px 1px;
	padding: 2px 2px 2px 2px;
}

.row {
	background: ${type.backgroundColour};
	border: 1px #C8C8C8 dotted;
	height: 30px;
    font-family: arial;
    font-size: 14px;
	color: #383838;
}

td.padded {
    padding-left: 4px;
    padding-right: 2px;
}
</style>
</head>
<body onload="javscript:setPosition();">
    <jsp:include page="include/header.jsp"/>
    <form id="form" method="post" action="">
        <table id="completed-table" class="kanban">
			<thead>
				<tr class="customizedHeader">
					<th colspan="5">${phase}</th>
				</tr>
			</thead>
                <tr class="subHeader">
					<th></th>
					<th>Id</th>
					<th>Name</th>
					<th>Size</th>
					<th> 
                        <%
                            for (int i = 0; i < phases.size(); i++) {
                        %><span class="age-legend" style="background-color:<%=htmlColours[i]%>"><%=phases.get(i)%></span><% } %>
                    </th>
				</tr>
            <%
                KanbanBoard kanbanBoard = project.getBoard(board);
                for (KanbanBoardRow row : kanbanBoard) {
            %>
                    <tr class="row">
                    <%
                    for (KanbanCell cell : row) {
                        if (!cell.isEmpty()) {
                            WorkItem item = cell.getWorkItem();
                    %>
                    <td class="edit, padded">
                        <div class="editIcon">
                            <img
                                class="edit"
                                alt="Edit"
                                id="edit-work-item-<%=item.getId()%>-button"
                                onclick="javascript:edit(<%=item.getId()%>);"
                                src="<%=request.getContextPath()%>/images/edit.png" />
                        </div>
                    </td>
                    <td class="id, padded">
                        <%
                        String formattedId = "" + item.getId();
                        if (item.isExcluded()) {
                            formattedId = "<span style='text-decoration:line-through';>" + formattedId + "</span>";
                        }
						%>                    
                        <%=formattedId %>
                    </td>
                    <td class="name, padded">
                        <%= item.getName() %>
                    </td>
                    <td class="size, padded">
                        <%=item.getSize()%>
                    </td>
                    <td class="age, padded">
                        <div id="work-item-<%=item.getId()%>" class="<%=item.getType().getName()%>">
                            
                                <% 
                                
                                Map<String, Integer> phaseDurations = item.getPhaseDurations();
                                
                                Iterator<HtmlColour> colorIterator = Arrays.asList(htmlColours).iterator();
                                
                                int pixelsPerDay = 5;
                                
                                for (String phase : phases) {
                                    HtmlColour currentColor = colorIterator.next();
                                    if (phaseDurations.containsKey(phase)) {
                                        int phaseDays = phaseDurations.get(phase);
                                        int phaseWidth = (int)Math.floor(phaseDays * pixelsPerDay);
                                %>
                                    <div class="age-item" style="width:<%=phaseWidth%>px; background-color:<%=currentColor%>;">
                                        <% if (phaseWidth > 10) {%>
                                            <%=phaseDays%>
                                        <% } %>
                                    </div>
                                <%
                                    }
                                }
                                %>
                                
                            
                           </td>
                    <%
                            }                        
                        }
                    %>
                </tr>
                <%
                    }
                %>
        </table>
    </form>
</body>
</html>
