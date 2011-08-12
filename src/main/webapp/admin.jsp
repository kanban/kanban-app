<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="com.metservice.kanban.KanbanService"%>
<%@page import="java.util.Collection"%>
<html>
<head>
<%
KanbanService service = new KanbanService();
%>
</head>
<body>
    <h1>ADMIN PAGE</h1>
    <%= request.getAttribute("username") %>
</body>
</html>
