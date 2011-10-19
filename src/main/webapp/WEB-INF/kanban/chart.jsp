<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<style type="text/css">

body {
font-family: arial;
}

h1,h2,h3 {
font-family: arial;
}

p {
font-family: arial;
}

.journalArea {
margin: 5px 0 40px 10px;
}

</style>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    	<jsp:include page="include/header-head.jsp"/>
        <script type="text/javascript" src="${pageContext.request.contextPath}/header.js"></script>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/header.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jquery-ui-1.8.16.custom.css"/>
        <script>
        $(function() {
        	$("#startDate").datepicker({ dateFormat: 'dd/mm/yy' });
        	$("#endDate").datepicker({ dateFormat: 'dd/mm/yy' });
        	$("#chartName").val($("#chartName").val().substring(0, $("#chartName").val().length - 4));
        	if ($("#chartName").val() == "cycle-time-chart"){
        		$("form#dateForm").hide();
        	}
        });
        </script>

        <title>Kanban</title>
    </head>
    <!-- ${pageContext.request.contextPath}/projects/<%= request.getAttribute("projectName") %>/wall/ -->
    <body>
        <jsp:include page="include/header.jsp"/>
        <form action="chart" id="dateForm">
        	<fieldset>
	            <p>StartDate: <input name="startDate"  id="startDate" type="text"/></p>
	   			<p>EndDate: <input name="endDate" id="endDate" type="text"/></p>
	   			<p>
	   			<input type="hidden" name="chartName" id="chartName" value="${imageName}"/>
	   			<input type="text" name="workItemTypeName" id="workItemTypeName" value="${workItemTypeName}" style="display:none;"/>
				<input type="submit" value="OK" />
				</p>
			</fieldset>
        </form>

        <div><img src="${imageName}?level=${workItemTypeName}&startDate=${startDate}&endDate=${endDate}" alt="[chart]" /></div>
   		<div class=journalArea><h1>Journal</h1>${kanbanJournal}</div>
    </body>
</html>
