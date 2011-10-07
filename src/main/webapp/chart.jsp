<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
  <script type="text/javascript"
  	src="${pageContext.request.contextPath}/jquery-1.6.1.min.js"></script>
  <script type="text/javascript"
  	src="${pageContext.request.contextPath}/jquery-ui-1.8.16.custom.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/header.js"></script>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/header.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jquery-ui-1.8.16.custom.css"/>
        <script>
        $(function() {
        	$("#startDate").datepicker({ dateFormat: 'dd/mm/yy' });
        	$("#endDate").datepicker({ dateFormat: 'dd/mm/yy' });
        	$("#chartName").val($("#chartName").val().substring(0, $("#chartName").val().length - 4));
        });
        </script>

        <title>Kanban</title>
    </head>
    <!-- ${pageContext.request.contextPath}/projects/<%= request.getAttribute("projectName") %>/wall/ -->
    <body>
        <jsp:include page="header.jsp"/>
        <form action="chart" >
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
   
    </body>
</html>
