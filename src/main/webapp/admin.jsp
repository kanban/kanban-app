<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="com.metservice.kanban.KanbanService"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>
<%@page import="com.metservice.kanban.model.WorkItemType" %>
<%@page import="com.metservice.kanban.model.WorkItemTypeCollection" %>
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
	<jsp:include page="header.jsp" />
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
    </ul>
    
    <h2>Export</h2>
    
    <c:forEach var="workItemType" items="${workItemTypes}">
        <div id="${workItemType.name}-download-button" class="button csvdownload"  onclick="javascript:download('${currentProjectName}', '${workItemType.name}');"><div class="textOnButton">${workItemType.name}</div></div>
    </c:forEach>
</body>
</html>
