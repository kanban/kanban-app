<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <jsp:include page="include/header-head.jsp" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/edit.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/edit.js"></script>

    <title>Kanban: edit a work item</title>
</head>
<body>
    <jsp:include page="include/header.jsp" />

    <form id="delete" action="delete-item-action" method="post">
        <div>
            <input type="hidden" name="id" value="${workItem.id}" />
        </div>
    </form>

    <form id="edit" action="edit-item-action" method="post">
        <fieldset>
            <legend>Edit ${workItem.type.name}</legend>

            <label class="labelClass" for="id">Id:</label> <input id="id" size="10" type="text" name="id"
                readonly="readonly" value="${workItem.id}" /> <br />

            <jsp:include page="include/edit-add-item-common-fields.jsp" />

            <label class="labelClass" for="parentId">Parent:</label> <select id="parentId" name="parentId">
                <c:forEach var="possibleParent" items="${parentAlternativesList}">
                    <c:choose>
                        <c:when test="${possibleParent.id == workItem.parentId}">
                            <option selected="selected" value="${possibleParent.id}">${possibleParent.truncatedName}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${possibleParent.id}">${possibleParent.truncatedName}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select> <br />

            <c:forEach var="phaseEntry" items="${phasesMap}">
                <label class="labelClass" for="date-${phaseEntry.key}">${phaseEntry.key}:</label>
                <input size="10" type="text" id="date-${phaseEntry.key}" name="date-${phaseEntry.key}"
                    value="${phaseEntry.value}" />
                <br />
            </c:forEach>
        </fieldset>

        <c:if test="${!empty children}">
            <fieldset>
                <legend>Children</legend>
                <ul>
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
                    <td><button id="delete-button" type="button" onclick="deleteThisWorkItem()">Delete</button></td>
                    <td style="text-align: right"><button id="save-button" type="submit">Save</button></td>
                </tr>
            </table>
        </fieldset>
    </form>
</body>
</html>
