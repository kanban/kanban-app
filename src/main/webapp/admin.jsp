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
%>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/header.css" />
</head>
<body>
	<jsp:include page="header.jsp" />
    <h1>ADMIN PAGE</h1>
    <%= request.getAttribute("username") %>
</body>
</html>
