<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>

<head>
	<jsp:include page="../kanban/include/header-head.jsp"/>
	<title>Kanban App by MetService</title>
</head>

<body class="main welcome">
    <h1>Kanban App</h1>
    
    <table class="pet">
        <tr>
            <th style="text-align:left">Version</th>
            <td>${service.version}</td>            
        </tr>
        <tr>
            <th style="text-align:left">Kanban Home</th>
            <td>${service.home}</td>
        </tr>
    </table>
    
    <h2>Projects</h2>

	<c:choose>	
		<c:when test="${homeExists}">
    		<table>
				<c:forEach var="projectName"  items="${listOfProjects}">        
    				<tr>
    					<td><a href="projects/${projectName}/wall">${projectName}</a></td>
<%--     					<td>Open <a href="projects/${projectName}/pet-project">P.E.T.</a></td> --%>
    				</tr> 
				</c:forEach>
    		</table>
        	<p>${projectsCount} project(s)</p>
		</c:when>
		<c:otherwise>
        	<p><a href="projects/create-home">Click here</a> to create the Kanban Home directory with a sample project.</p>
        </c:otherwise>
    </c:choose>
</body>

</html>
