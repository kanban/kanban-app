<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
	<jsp:include page="include/header-head.jsp"/>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/edit.css" />
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/edit.js"></script>

	<title>Kanban: add a work item</title>
</head>
<body onload="setFocus('name-field');">
	<jsp:include page="include/header.jsp" />
	
	<form action="add-item-action">
		<fieldset>
			<legend>${legend}</legend>
			
			<jsp:include page="include/edit-add-item-common-fields.jsp"/>
		</fieldset>
        
		<fieldset class="submit">
			<button id="save-button" type="submit">Save</button>
		</fieldset>
        
		<input type="hidden" name="parentId" value="${parentId}" />
		<input type="hidden" name="type" value="${type}" />
	</form>
</body>
</html>