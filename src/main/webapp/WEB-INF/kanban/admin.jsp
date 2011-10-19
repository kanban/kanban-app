<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="com.metservice.kanban.KanbanService"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>
<%@page import="com.metservice.kanban.model.WorkItemType" %>
<%@page import="com.metservice.kanban.model.WorkItemTypeCollection" %>
<%@page import="com.metservice.kanban.model.KanbanBoardColumnList"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumn"%>
<%@page import="com.metservice.kanban.model.BoardIdentifier"%>
<%@page import="java.util.Collection"%>
<html>
<head>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/header.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jquery-1.6.1.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jquery.tablednd_0_5.js"></script>
<%
KanbanProject project = (KanbanProject) request.getAttribute("project");
KanbanService service = new KanbanService();
String currentProjectName = (String) request.getAttribute("projectName");
WorkItemTypeCollection workItemTypes = project.getWorkItemTypes();
request.setAttribute("workItemTypes", workItemTypes);
%>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/header.css" />
</head>
<body>
	<jsp:include page="include/header.jsp" />
    <h1>ADMIN PAGE</h1>
    <!--Use the admin.css file for styling this.  -->
    <ul>
      <li>
   	    <a href="javascript:changeSettings(true);" >
       	  New Project
       	</a>
      </li>
      <li>
        <a href="javascript:changeSettings(false);" >
          Edit Project
        </a>      
      </li>
      <li>
        <a href="javascript:addColumn();" >
          Add Column
        </a>
      </li>
      <li>
    	  <a href="javascript:addWaitingColumn();">
    	    Add Waiting Column
    	  </a>
      </li>
     <li>
      <a href="javascript:deleteColumn();" >
        Delete Column
      </a>
     </li>
    </ul>
    
<!--    
    <select>
    <%
        String boardType = "wall";
        BoardIdentifier board = BoardIdentifier.valueOf(boardType.toUpperCase());
        KanbanBoardColumnList columns = project.getColumns(board);
        int column_index = 0;
        for (KanbanBoardColumn column : columns) {
				  column_index++;
				  int wipLimit = column.getWIPLimit();
          String type = column.getWorkItemType().getName();
          //WIP Limit stuff by Nick Malcolm and Chris Cooper
    %>
      <option value="<%= type +"_"+ column.getPhase() %>">
        <%= type + " - " + column.getPhase() %>
      </option>
    <%    } %>
    </select> <a href="#" onclick="prompt('Enter new WIP Limit');return false;">Set WIP Limit</a>
 -->    
    <h2>Export</h2>
    
    <c:forEach var="workItemType" items="${workItemTypes}">
        <div id="${workItemType.name}-download-button" class="button csvdownload"  onclick="javascript:download('${currentProjectName}', '${workItemType.name}');"><div class="textOnButton">${workItemType.name}</div></div>
    </c:forEach>
</body>
</html>
