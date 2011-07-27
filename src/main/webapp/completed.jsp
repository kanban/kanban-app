<%@page import="com.metservice.kanban.utils.WorkingDayUtils"%>
<%@page import="org.joda.time.LocalDate"%>
<%@page import="org.apache.commons.collections.ListUtils"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.awt.Color"%>
<%@page import="java.awt.Paint"%>
<%@page import="com.metservice.kanban.charts.KanbanDrawingSupplier"%>
<%@page import="java.util.Map"%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page import="com.metservice.kanban.model.KanbanBoardColumnList"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumn"%>
<%@page import="java.util.List"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>
<%@page import="com.metservice.kanban.model.Colour"%>
<%@page import="com.metservice.kanban.model.KanbanProjectConfiguration"%>
<%@page import="com.metservice.kanban.model.WorkItemTypeCollection"%>
<%@page import="com.metservice.kanban.model.KanbanBoard"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.KanbanCell"%>
<%@page import="com.metservice.kanban.model.BoardIdentifier"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
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

			function markUnmarkToPrint(id, type){
			   var item = document.getElementById(id)
			   if (item.className == 'markedToPrint') {
			   	 item.className = type;
			   } else {
			     item.className = "markedToPrint";
			   }
			}
			function advance(id){
			 document.forms["form"].action = getBoard() + "/advance-item-action?id=" + id + "&scrollTop=" + getYOffset();
			 document.forms["form"].submit();
			}

			function edit(id){
			 document.forms["form"].action = getBoard() + "/edit-item?id=" + id;
			 document.forms["form"].submit();
			 
			}
			
        	function addChild(id){
              document.forms["form"].action = getBoard() + "/add-item?id=" + id;
              document.forms["form"].submit();
        	}
            function move(id, targetId, after){
              document.forms["form"].action = getBoard() + "/move-item-action?id=" + id + "&targetId=" + targetId + "&scrollTop=" + getYOffset() + "&after=" + after;
              document.forms["form"].submit();
            }

//]]> 
		</script>

<%
    KanbanProject project = (KanbanProject) request.getAttribute("project");
    WorkItemType rootType = project.getWorkItemTypes().getRoot().getValue();
%>

<style type="text/css">
.kanban {
	margin: 10px 0px 0px 0px;
	background: whitesmoke;
	border-collapse: collapse;
}

.age-container {
    float: left;
}

.age-item {
    height:3px;
    width:3px;
    margin-bottom: 1px;
    margin-right: 1px;
    float: left;
    background-color: black;
}

.itemName {
	width: 135px;
	height: 20px;
	position: absolute;
	top: 6px;
	left: 2px;
	font-family: arial;
	font-size: 12px;
	color: #383838;
	text-align: left;
}

.editIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: absolute;
	width: 16px;
	height: 16px;
	left: 0px;
	top: 50px;
}

.size {
	border: 1px #BBBBBB dotted;
	position: absolute;
	width: 16px;
	height: 13px;
	left: 40px;
	top: 50px;
	font-family: arial;
	font-style: italic;
	font-size: 9px;
	text-align: center;
	color: #383838;
}

.importance {
	border: 1px #BBBBBB dotted;
	position: absolute;
	width: 36px;
	height: 13px;
	left: 67px;
	top: 50px;
	font-family: arial;
	font-style: italic;
	font-size: 9px;
	text-align: center;
	color: #383838;
}

.horizontalLine {
	border-top: 2px black solid;
}

<%		
    String boardType = (String) request.getAttribute("boardType");
    BoardIdentifier board = BoardIdentifier.valueOf(boardType.toUpperCase());

    WorkItemTypeCollection workItemTypes = project.getWorkItemTypes();
    for (WorkItemType workItemType : workItemTypes) {
        String name = workItemType.getName();
 %> 
.<%=name%> {
	background: ${type.cardColour};
	height: 60px;
	width: 155px;
	margin: 1px 1px 1px 1px;
	padding: 3px 3px 3px 3px;
    position: relative;
}

.<%=name%>:hover {
	border: 1px black solid;
	background: ${type.cardColour};
	height: 60px;
	width: 155px;
	margin: 1px 1px 1px 1px;
	padding: 2px 2px 2px 2px;
}

.<%=name%>-header {
	background: ${type.cardColour};
	border: 1px #989898 dotted;
	height: 30px;
	width: 164px;
	font-family: verdana;
	font-size: 14px;
	color: black;
}

.<%=name%>-background {
	background: ${type.backgroundColour};
	border: 1px #C8C8C8 dotted;
	width: 155px;
	height: 30px;
}
<%}%>

</style>
</head>
<body onload="javscript:setPosition();">
    <jsp:include page="header.jsp"/>
    <form id="form" method="post" action="">
        <table class="kanban">
            <tr>
                <%
                    KanbanBoardColumnList columns = project.getColumns(board);

                    for (KanbanBoardColumn column : columns) {
                        String type = column.getWorkItemType().getName();
                %>
                <th class="<%=type%>-header"><%=column.getPhase()%></th>
                <%
                    }
                %>
            </tr>
            <%
                KanbanBoard kanbanBoard = project.getBoard(board);
                for (KanbanBoardRow row : kanbanBoard) {
            %>
                    <tr class="<%= row.hasItemOfType(rootType) ? "horizontalLine" : ""%>">
                    <%
                    for (KanbanCell cell : row) {
                        if (!cell.isEmpty()) {
                            WorkItem item = cell.getWorkItem();
                    %>

                    <td class="<%=item.getType().getName()%>-background">
                        <div
                            id="work-item-<%=item.getId()%>"
                            class="<%=item.getType().getName()%>">
                            
                            <div class="age-container">
                                <% 
                                
                                Map<String, Integer> phaseDurations = item.getPhaseDurations();
                                List<String> itemPhases = item.getType().getPhases();
                                
                                //There doesn't appear to be a straightforward way of always getting
                                //the wall columns so this is hardcoded.  We need the wallBoard columns
                                //particularly so we can display the time spent in each wall phase.
                                BoardIdentifier wallBoard = BoardIdentifier.valueOf("WALL");
                                KanbanBoardColumnList wallColumns = project.getColumns(wallBoard);
                                List<String> wallPhases = new ArrayList<String>();
                                for (KanbanBoardColumn column : wallColumns) {
                                        wallPhases.add(column.getPhase());
                                }
                                
                                List<String> phases = ListUtils.retainAll(itemPhases, wallPhases);
                                
                                Color[] colors = KanbanDrawingSupplier.getColours(phases.size());
                                Iterator<Color> colorIterator = Arrays.asList(colors).iterator();
                                for (String phase : phases) {
                                    Colour currentColor = new Colour(colorIterator.next());
                                    if (phaseDurations.containsKey(phase)) {
                                        for (int i=0; i < phaseDurations.get(phase); i++) {
                                %>
                                            <div class="age-item" style="background-color:<%=currentColor.toString()%>"></div>
                                <%
                                        }
                                    }
                                }
                                %>
                            </div>
                                
                                <div class="itemName">
								<%
									String formattedId = "" + item.getId();
								    if (item.isExcluded()) {
								        formattedId = "<span style='text-decoration:line-through';>" + formattedId + "</span>";
								    }
								%>                    
                                <%=formattedId %>: <span class="work-item-name"><%= item.getName() %></span>
                            </div>
                            <div class="editIcon">
                                <img
                                    class="edit"
                                    alt="Edit"
                                    id="edit-work-item-<%=item.getId()%>-button"
                                    onclick="javascript:edit(<%=item.getId()%>);"
                                    src="<%=request.getContextPath()%>/images/edit.png" />
                            </div>
                            <%
                                if (item.getSize() > 0) {
                            %>
                            <div class="size">
                                <%=item.getSize()%>
                            </div>
                            <%
                                } else {
                            %>
                            <div class="size" style="border: 0px"></div>
                            <%
                                }
                            %>

                            <%
                                if (item.getImportance() > 0) {
                            %>
                            <div class="importance">
                                <%=item.getImportance()%>
                            </div>
                            <%
                                } else {
                            %>
                            <div class="importance" style="border: 0px"></div>
                            <%
                                }
                            %>
                            

                        </div></td>
                    <%
                        } else {
                    %><td
                        class="<%=cell.getWorkItemType().getName()%>-background"></td>
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
