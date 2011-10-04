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
        	$( "#startDate" ).datepicker();
        	$( "#endDate" ).datepicker();
        }); 
        </script>

        <title>Kanban</title>
    </head>
    
    <body>
        <jsp:include page="header.jsp"/>
    <p>StartDate: <input id="startDate" type="text"></p>
    <p>EndDate: <input id="endDate" type="text"></p>
        <div><img src="${imageName}?level=${workItemTypeName}&startDate=${startDate}&endDate=${endDate}" alt="[chart]"></img></div>
   
    </body>
</html>
