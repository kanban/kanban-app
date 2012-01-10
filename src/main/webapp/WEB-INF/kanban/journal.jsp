<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<jsp:include page="include/header-head.jsp"/>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/journal.js"></script>
	<title>Kanban: journal</title>
</head>

<body>
    <jsp:include page="include/header.jsp"/>
    <div>
        <a href="#" id="add-entry-button">Add entry</a>    
    </div>
    <c:forEach items="${kanbanJournal}" var="item">
        <div class="journal-entry">
            <div class="ui-widget-header" style="position: relative;">
                <span>${item.userName} wrote on ${item.dateStr}</span>
                <a href="remove-journal-entry?id=${item.id}" style="top: 2px; right: 5px; position: absolute;">
                    <span class="ui-icon ui-icon-closethick">close</span>
                </a>
                
            </div>
            <div class="ui-widget-content journal-text">${item.text}</div>
        </div>
    </c:forEach>

    <div id="journal-add-dialog" title="Add journal item">
        <p>Entry date</p>
        <input type="text" id="journal-date" />
        <p>Journal text:</p>
        <textarea name="journal-text" id="journal-text" rows="5" cols="30"></textarea>
        <div id="validation-error" class="error">aa</div>
    </div>
</body>
</html>
