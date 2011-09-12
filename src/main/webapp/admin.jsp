<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="com.metservice.kanban.KanbanService"%>
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
KanbanService service = new KanbanService();
String currentProjectName = (String) request.getAttribute("projectName");
%>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/header.css" />
</head>
<body>
	<jsp:include page="header.jsp" />
    <h1>ADMIN PAGE</h1>
    <%= request.getAttribute("username") %>
    <!--Use the admin.css file for styling this.  -->
   	<div id="newProjectLink" class="link" onclick="javascript:changeSettings(true);" ><div class ="textOnButton">New Project</div></div>
    <div id="editProjectLink" class="link" onclick="javascript:changeSettings(false);" ><div class ="textOnButton">Edit Project</div></div>
	<div id="add-column-link" class="link" onclick="javascript:addColumn();" ><div class ="textOnButton">Add Column</div></div>
	<div id="add-waitingcolumn-link" class="link" onclick="javascript:addWaitingColumn();" ><div class ="textOnButton">Add Waiting Column</div></div>
    
</body>
</html>
