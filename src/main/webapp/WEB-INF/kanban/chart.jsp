<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">


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
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/chart.css"/>

        <script>
        $(function() {
        	$("#startDate").datepicker({ 
        		dateFormat: 'yy-mm-dd',
    			showOn: "button",
    			buttonImage: "../../images/calendar.gif",
    			buttonImageOnly: true

        	});
        	$("#endDate").datepicker({ 
        		dateFormat: 'yy-mm-dd', 
    			showOn: "button",
    			buttonImage: "../../images/calendar.gif",
    			buttonImageOnly: true
        	});
        	$("#chartName").val($("#chartName").val().substring(0, $("#chartName").val().length - 4));
        	if ($("#chartName").val() == "cycle-time-chart"){
        		$("form#dateForm").hide();
        	}
        });
        </script>

        <title>Kanban</title>
    </head>
    <!-- ${pageContext.request.contextPath}/projects/${project.name}/wall/ -->
    <body>
        <jsp:include page="include/header.jsp"/>
        <form action="chart" id="dateForm">
            <table>
	            <tr><td>Start date:</td><td><input name="startDate"  id="startDate" type="text" value="${startDate}"/></td></tr>
	   			<tr><td>End date:</td><td><input name="endDate" id="endDate" type="text"  value="${endDate}"/></td></tr>
                <tr><td colspan="2">
        		    <input type="submit" value="OK" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"/>
           			<input type="hidden" name="chartName" id="chartName" value="${imageName}"/>
           			<input type="text" name="workItemTypeName" id="workItemTypeName" value="${workItemTypeName}" style="display:none;"/>
                </td></tr>
            </table>                
        </form>

        <div><img src="${imageName}?level=${workItemTypeName}&startDate=${startDate}&endDate=${endDate}&workStream=${workStreams[project.name]}" alt="[chart]" /></div>
        
        <c:if test="${imageName == 'burn-up-chart.png'}">
            <table class="burnup-summary">
                <tr><td>Project start date (first backlog entry):</td><td>${projectStartDate}</td></tr>
                <tr><td>Current date:</td><td>${currentDate}</td></tr>
                <tr><td>Projected end date:</td><td>${projectedEndDate}</td></tr>
            </table>
            <div class="small"><b>Note</b>: <i>Projected end date</i> depends on the <i>Start date</i> selected for display.</div>        
        </c:if>
        
   		<div class=journalArea>
            <h1>Journal</h1>
            <c:forEach items="${kanbanJournal}" var="item">
                <div id="journal-entry-${item.id}" class="journal-entry">
                    <div class="ui-widget-header" style="position: relative;">
                        <span id="journal-header-${item.id}">${item.userName} wrote on ${item.dateStr}</span>
                    </div>
                    <div id="journal-text-${item.id}" class="ui-widget-content">${item.text}</div>
                </div>
            </c:forEach>
        
        </div>
    </body>
</html>
