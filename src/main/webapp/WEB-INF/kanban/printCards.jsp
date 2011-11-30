<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.utils.DateUtils"%>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/printCards.css"/>
    <title>Kanban: printing items</title>
</head>
<body>
<div class="printhide">Goto: <a href="backlog">Backlog</a> :: <a href="wall">Wall</a> :: <a href="completed">Complete</a></div>
    <c:forEach var="item" items="${items}">
        <div class="card">
            <div class="itemName">${item.name}</div>
            <div class="notes">
                <div class="label">Notes</div>
                <div class="field">${item.notes}</div>
            </div>
            <div class="id">
                <div class="label">Id</div>
                <div class="field" style="background-color: ${item.colour};">${item.id}</div>
            </div>
            <div class="averageCaseEstimate">
                <div class="label">Avg. Case</div>
                <div class="field">${item.averageCaseEstimate}</div>
            </div>
            <div class="importance">
                <div class="label">Importance</div>
                <div class="field">${item.importance}</div>
            </div>
            <div style="background-color: ${item.type.backgroundColour};" class="type">${fn:toUpperCase(item.type.name)}</div>
            <div class="footer">
                <c:forEach var="phase" items="${item.type.phases}">
                    <div class="stamp">
                        <div class="phase">${phase}</div>
                        <div class="date">
                            <c:if test="${!empty item.datesByPhase[phase]}">
                                <%=DateUtils.formatConventionalNewZealandDate(((WorkItem) pageContext
                            .getAttribute("item")).getDate((String) pageContext.getAttribute("phase")))%>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
        <div class="space"></div>
    </c:forEach>
</body>
</html>