<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="kanban" uri="/WEB-INF/kanban.tld" %>

<html>

<head>
	<jsp:include page="include/header-head.jsp"/>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/edit.css" />
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/edit.js"></script>

	<title>Kanban: add a work item</title>
    
    <script type="text/javascript">
    	<kanban:workStreams name="workStreams" project="${project}" initialWorkStream="${workStreams[projectName]}"/>
	</script>
</head>

<body onload="setFocus('name');">
	<jsp:include page="include/header.jsp" />
	
	<form id="edit" action="add-item-action">
		<fieldset>
			<legend>${legend}</legend>
			
			<jsp:include page="include/edit-add-item-common-fields.jsp"/>
		</fieldset>
        
		<fieldset class="submit" style="text-align: right;">
			<button id="save-and-print-button" onclick="saveAndPrint()">Save and Print</button> <button id="save-button" type="submit">Save</button>
		</fieldset>
        
		<input type="hidden" name="parentId" value="${parentId}" />
		<input type="hidden" name="type" value="${type}" />
	</form>
</body>
</html>