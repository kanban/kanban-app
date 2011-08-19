<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page import="org.joda.time.LocalDate"%>
<%@page import="com.metservice.kanban.utils.WorkingDayUtils"%>
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.KanbanCell"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.KanbanBoard"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumnList"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumn"%>
<%@page import="com.metservice.kanban.model.HtmlColour"%>
<%@page import="com.metservice.kanban.model.WorkItemTypeCollection"%>
<%@page import="com.metservice.kanban.model.BoardIdentifier"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>

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
			
			//Changes the card color to FIREBRICK!
            function stopStory(id,type) {
            	//document.forms["form"].action = getBoard() + "/advance-item-action?id=" + id + "&scrollTop=" + getYOffset();
   			 	//document.forms["form"].submit();
            	//var item = document.getElementById(id);
            	//if (item.className=='stopped') {
            		//item.className = type;
            	//}
            	//else { item.className = "stopped"; }
            	document.forms["form"].action = getBoard() + "/stop-item-action?id=" + id;
   			 	document.forms["form"].submit();
            }
            
			function markUnmarkToPrint(divId, type, itemId){
			   var item = document.getElementById(divId);
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

.upIcon:hover,.advanceIcon:hover,.downIcon:hover,.editIcon:hover,.addIcon:hover,.stopIcon:hover
	{
	-moz-opacity: 0.5;
	opacity: 0.5;
}

.upIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: absolute;
	width: 16px;
	height: 16px;
	left: 130px;
	top: 5px;
}

.advanceIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: absolute;
	width: 16px;
	height: 16px;
	left: 140px;
	top: 26px;
}

.downIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: absolute;
	width: 16px;
	height: 16px;
	left: 130px;
	top: 47px;
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

.stopIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: absolute;
	width: 16px;
	height: 16px;
	left: 110px;
	top: 50px;
}

.addIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: absolute;
	width: 16px;
	height: 16px;
	left: 20px;
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

.markedToPrint {
	border: 1px silver solid;
	background: #EEEEEE;
	height: 60px;
	width: 155px;
	margin: 1px 1px 1px 1px;
	padding: 2px 2px 2px 2px;
    position: relative;
}

.stopped {
	border: 1px red solid;
	background: #800517;
	height: 60px;
	width: 155px;
	margin: 1px 1px 1px 1px;
	padding: 2px 2px 2px 2px;
    position: relative;
}

.horizontalLine {
	border-top: 2px black solid;
}

<%			int cardWidth = 155;
            int ageItemWidth = 4;


            String boardType = (String) request.getAttribute("boardType");
            BoardIdentifier board = BoardIdentifier.valueOf(boardType.toUpperCase());

            WorkItemTypeCollection workItemTypes = project.getWorkItemTypes();
            for (WorkItemType workItemType : workItemTypes) {
                String name =
                    workItemType.getName();
                HtmlColour cardColour = workItemType.getCardColour();
                HtmlColour backgroundColour = workItemType.getBackgroundColour();%> .<%=name%> {
	background: <%=cardColour.toString()%>;
	height: 60px;
	width: <%=cardWidth%>px;
	margin: 1px 1px 1px 1px;
	padding: 3px 3px 3px 3px;
    position: relative;
}

.<%=name%>:hover {
	border: 1px black solid;
	background: <%=cardColour.toString()%>;
	height: 60px;
	width: 155px;
	margin: 1px 1px 1px 1px;
	padding: 2px 2px 2px 2px;
}

.markedToPrint:hover {
	border: 1px black solid;
	background: #CCCCCC;
	height: 60px;
	width: 155px;
	margin: 1px 1px 1px 1px;
	padding: 2px 2px 2px 2px;
}

.stopped:hover {
	border: 1px red solid;
	background: #800517;
	height: 60px;
	width: 155px;
	margin: 1px 1px 1px 1px;
	padding: 2px 2px 2px 2px;
}

.<%=name%>-header {
	background: <%=cardColour.toString()%>;
	border: 1px #989898 dotted;
	height: 30px;
	width: 164px;
	font-family: verdana;
	font-size: 14px;
	color: black;
}

.<%=name%>-background {
	background: <%=backgroundColour.toString()%>;
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
                    
                    %><tr class="<%= row.hasItemOfType(rootType) ? "horizontalLine" : ""%>"><%
                    
                    for (KanbanCell cell : row) {
                        if (!cell.isEmpty()) {
                            WorkItem item = cell.getWorkItem();
                    %>
                    
                    <td class="<%=item.getType().getName()%>-background">
                        <div
                            onclick="javascript:markUnmarkToPrint('work-item-<%=item.getId()%>','<%=item.getType().getName()%>', <%=item.getId()%>)"
                            id="work-item-<%=item.getId()%><%=item.isStopped() %>"
                            class="<%=item.getType().getName()%> <%= item.isStopped() ? "stopped" : "" %>">
                            
                            <div class="age-container">
                                <% 
                                LocalDate phaseStartDate = item.getDate(item.getCurrentPhase());
                                int days = WorkingDayUtils.getWorkingDaysBetween(phaseStartDate, new LocalDate());
                                
                                int itemsPerRow = (int) Math.floor(cardWidth/(double)ageItemWidth);
                                
                                
                                for (int i=0; i<days && i < itemsPerRow; i++) {
                                    if (i == itemsPerRow - 1) {
                                %>
                                        <div class="age-item" style="background-color: red; width: 6px; "></div>
                                <%  
                                    } else { 
                                %>
                                        <div class="age-item"></div>
                                <%    
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
                            <div class="upIcon">
                                <%
                                    WorkItem adjacentWorkItemUp = cell.getWorkItemAbove();
                                                if (adjacentWorkItemUp != null) {
                                %>
                                <img 
                                    onclick="javascript:move(<%=item.getId()%>, <%=adjacentWorkItemUp.getId()%>, false);"
                                    src="<%=request.getContextPath()%>/images/go-up.png" />
                                <%
                                    } 
                                %>
                            </div>
                            <div class="advanceIcon">
                            
                            	<% 
                            		if (!item.isCompleted()) {
                                %>
                                <img 
                                    onclick="javascript:advance(<%=item.getId()%>);"
                                    src="<%=request.getContextPath()%>/images/go-next.png" />
                                <%
                                    }
                                %>
                            </div>
                            <div class="downIcon">

                                <%
                                    WorkItem adjacentWorkItemDown = cell.getWorkItemBelow();
                                                if (adjacentWorkItemDown != null) {
                                %>
                                <img 
                                    onclick="javascript:move(<%=item.getId()%>, <%=adjacentWorkItemDown.getId()%>, true);"
                                    src="<%=request.getContextPath()%>/images/go-down.png" />
                                <%
                                    }
                                %>
                            </div>
                            
                            <div class="editIcon">
                                <img
                                    class="edit"
                                    alt="Edit"
                                    id="edit-work-item-<%=item.getId()%>-button"
                                    onclick="javascript:edit(<%=item.getId()%>);"
                                    src="<%=request.getContextPath()%>/images/edit.png" />
                            </div>
                            <div class="stopIcon">
                                <img
                                    class="stop"
                                    alt="Stop"
                                    id="stop-work-item-<%=item.getId()%>-button"
                                    onclick="javascript:stopStory(<%=item.getId()%>,'<%=item.getType().getName()%>');"
                                    src="<%=request.getContextPath()%>/images/stop.png" />
                            </div>
                            <div class="addIcon">
                                <%
                                if (project.getWorkItemTypes().getTreeNode(item.getType()).hasChildren()) {
                                %>
                                <img
                                    class="add"
                                    alt="Advance"
                                    onclick="javascript:addChild(<%=item.getId()%>);"
                                    src="<%=request.getContextPath()%>/images/list-add.png" />
                                <%
                                    }
                                %>
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
