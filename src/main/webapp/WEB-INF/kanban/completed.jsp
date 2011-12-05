<%@page import="com.metservice.kanban.model.WorkItemType"%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="kanban" uri="/WEB-INF/kanban.tld" %>

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
	<jsp:include page="include/header-head.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/boards.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/completed.css" />
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/completed.js"></script>

<title>Kanban</title>
<%
// is this used?
    String scrollTopParam = (String) request.getAttribute("scrollTop");
    int scrollTo = 0;
    if (scrollTopParam != null) {
        scrollTo = Integer.parseInt(scrollTopParam);
    }
%>

<style type="text/css">


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
</style>

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
    WorkItemType type = (WorkItemType) request.getAttribute("type");
    
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

</head>
<body onload="javscript:setPosition();">
    <jsp:include page="include/header.jsp"/>
    <form id="form" method="post" action="">
        <table id="completed-table" class="kanban">
			<thead>
				<tr class="customizedHeader">
					<th colspan="6">${phase}</th>
				</tr>
			</thead>
                <tr class="subHeader">
					<th></th>
					<th>Id</th>
					<th>Name</th>
                    <th></th>
					<th>Size</th>
					<th> 
                        <%
                            for (int i = 0; i < phases.size(); i++) {
                        %><span class="age-legend" style="background-color:<%=htmlColours[i]%>"><%=phases.get(i)%></span><% } %>
                    </th>
				</tr>
                <c:forEach var="row" items="${board.iterator}">
                    <tr class="row">
                        <c:forEach var="cell" items="${row.iterator}">
    
                            <c:if test="${!cell.emptyCell}">
                                <c:set var="item" value="${cell.workItem}" />
                                
                                <td class="edit, padded">
                                    <div class="editIcon">
                                        <img class="edit" alt="Edit" id="edit-work-item-${item.id}-button" onclick="javascript:edit(${item.id});"
                                            src="${pageContext.request.contextPath}/images/edit.png" />
                                    </div>
                                </td>
            					<c:choose>
            						<c:when test="${cell.workItem.excluded}">
            							<td class="itemName itemNumber itemExcluded">
            								${cell.workItem.id}
            						    </td>
            						</c:when>
            						<c:otherwise>
            							<td class="itemName itemNumber itemIncluded">
            						        ${cell.workItem.id} 
            						    </td>
            						</c:otherwise>
            					</c:choose>
                                <c:choose>
                                    <c:when test="${cell.workItem.mustHave}">
                                       <td class="itemName formify itemMustHave" data-role="name">${cell.workItem.name}</td>
                                    </c:when>
                                    <c:otherwise>
                                        <td class="itemName formify itemNiceToHave" data-role="name">${cell.workItem.name}</td>
                                    </c:otherwise>
                                </c:choose>
                                
                                <td class="small color">
                                  <div style="background-color:${item.colour}; width: 10px; height: 10px; border: 1px solid #aaa; margin: 5px">
                                  </div>
                                </td>
                                <td class="size, padded">${item.averageCaseEstimate}
                                </td>
                                <td class="age, padded">
                                    <div id="work-item-${item.id}" class="${item.type.name}">
                                        <kanban:phasesLengths workItem="${item}" project="${project}" type="${type}"/>
                                      </div>      
                                </td>
                            </c:if>
                        </c:forEach>
                    </tr>
                </c:forEach>
        </table>
    </form>
</body>
</html>
