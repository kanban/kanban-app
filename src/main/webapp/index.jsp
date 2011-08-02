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
    <h1>Kanban App</h1>
    
    <table>
        <tr>
            <th style="text-align:left">Version</th>
            <td><%= service.getVersion() %> - Victoria Branch</td>            
        </tr>
        <tr>
            <th style="text-align:left">Kanban Home</th>
            <td><%= service.getHome() %></td>
        </tr>
    </table>
    
    <%
    if (service.getHome().exists()) {
        Collection<String> listOfProjects = service.getProjects();
    %>
        
        <h2>Projects</h2>
        
    	<%
    	    for (String projectName : listOfProjects) {
    	%><a href="projects/<%=projectName%>/wall"><%=projectName%></a>
    	<br />
    	<%
    	    }
    	%>
        <p><%= listOfProjects.size() %> project(s)</p>
        
    <%
    } else {
    %>
        <p><a href="projects/create-home">Click here</a> to create the Kanban Home directory with a sample project.</p>
    <%
    }
    %>
</body>
</html>
