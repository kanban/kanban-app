<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ page import="com.metservice.kanban.model.WorkItem"%>
<%@ page import="com.metservice.kanban.model.WorkItemType"%>
<%@ page import="com.metservice.kanban.model.KanbanProjectConfiguration"%>
<%@ page import="com.metservice.kanban.model.KanbanProject"%>
<%@ page import="com.metservice.kanban.utils.DateUtils"%>
<%@ page import="java.util.*" %>
 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/header.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/colorpicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/eye.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/utils.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/layout.js?ver=1.0.2"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/header.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/colorpicker.css" type="text/css" />
<link rel="stylesheet" media="screen" type="text/css" href="${pageContext.request.contextPath}/layout.css" />
<title>Kanban: edit a work item</title>
<style type="text/css">
legend {
	margin-left: 1em;
	padding: 0;
	color: #000;
	font-weight: bold;
}

fieldset {
	float: left;
	clear: both;
	width: 500px;
	margin: 0 0 0 0;
	padding-top: 1.5em;
	padding-bottom: 1.5em;
	border: 1px solid #BFBAB0;
	background-color: #F2EFE9;
	font-family: Verdana;
	font-size: 12px;
}

input[readonly] {
	background: #F2EFE9;
	border: 1px solid #BFBAB0;
}

fieldset.submit {
	margin-top: 0.5em;
	padding-top: 0.3em;
	padding-bottom: 0.3em;
	background-color: #DDDDDD;
}

.labelClass {
	float: left;
	width: 25%;
	margin-right: 0.5em;
	padding-top: 0.2em;
	text-align: right;
	font-weight: bold;
	clear:left;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		
	});
	function deleteThisWorkItem() {
		var response = confirm("Permanently delete this work item?");
		if (response == true) {
			document.forms["delete"].submit();
		}
	}
	
</script>
</head>
<body>
    <jsp:include page="header.jsp"/>

    <form id="delete" action="delete-item-action" method="post">
        <div>
            <input type="hidden" name="id" value="${workItem.id}" />
        </div>
    </form>
    <form id="edit" action="edit-item-action">
        <fieldset>
            <legend>
                Edit ${workItem.type.name}
            </legend>

            <label class="labelClass" for="id">Id:</label>
            <input id="id" size="10" type="text" name="id" readonly="readonly" value="${workItem.id}" /> <br />
            
            <label class="labelClass" for="name">Name:</label>
            <textarea id="name" name="name" rows="2" cols="40">${workItem.name}</textarea>
            
            <br />
            
            <label class="labelClass" for="size">Size:</label>
            <input id="size" size="10" type="text" name="size" value="${workItem.size}" />
            
            <br />
            
            <label class="labelClass" for="importance">Importance:</label>
            <input id="importance" size="10" type="text" name="importance" value="${workItem.importance}" />
            
            <br />
            
            <label class="labelClass" for="notes">Notes:</label>
            <textarea id="notes" name="notes" rows="5" cols="40">${workItem.notes}</textarea>
            
            <br />
            
            <label class="labelClass" for="parentId">Parent:</label>
            <select id="parentId" name="parentId">
	            <c:forEach var="possibleParent" items="${parentAlternativesList}">
					<c:choose>
						<c:when test="${possibleParent.id == workItem.parentId}">
			                <option selected="selected" value="${possibleParent.id}">${possibleParent.truncatedName}</option>
					    </c:when><c:otherwise>
			                <option value="${possibleParent.id}">${possibleParent.truncatedName}</option>
					    </c:otherwise>
					</c:choose>
	            </c:forEach>
            </select> <br />
            <c:forEach var="phaseEntry" items="${phasesMap}">
	            <label class="labelClass" for="date-${phaseEntry.key}">${phaseEntry.key}:</label>
	            <input size="10" type="text" id="date-${phaseEntry.key}"
	                name="date-${phaseEntry.key}" value="${phaseEntry.value}" />
	            <br />
            </c:forEach>
            <label class="labelClass" for="excluded">Excluded from reports:</label>
			<c:choose>
				<c:when test="${workItem.excluded}">
	                <input type="checkbox" name="excluded" checked="checked" />
			    </c:when><c:otherwise>
	                <input type="checkbox" name="excluded" />
			    </c:otherwise>
			</c:choose>
            <br />
            <label class="labelClass" for="color">Color</label>
            <input size="10" type="text" id="colorid"
	                name="color" value="${workItem.colour}" style="display:none" />
			<div class="wrapper">
			<div id="colorSelector"><div style="background-color: #0000ff"></div></div>
		</div>
        </fieldset>
        
        
        
        <c:if test="${!empty children}">
        	<fieldset>
            	<legend>Children</legend>
	        	<ul >
	            	<c:forEach var="child" items="${children}">
	                	<li style="padding: 3px;"><a href="edit-item?id=${child.id}">${child.name}</a></li>
	            	</c:forEach>
	        	</ul>
        	</fieldset>
        </c:if>
        <fieldset class="submit">
            <legend></legend>
            <table width="100%">
                <tr>
                    <td><button id="delete-button" type="button"
                            onclick="deleteThisWorkItem()">Delete</button>
                    </td>
                    <td style="text-align: right"><button
                            id="save-button" type="submit">Save</button>
                    </td>
                </tr>
            </table>
        </fieldset>

    </form>
</body>
</html>
