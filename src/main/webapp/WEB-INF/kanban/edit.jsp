<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="kanban" uri="/WEB-INF/kanban.tld" %>

<html>

<head>
    <jsp:include page="include/header-head.jsp" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/edit.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/edit.js"></script>

    <title>Kanban: edit a work item</title>
    <script type="text/javascript">
        <kanban:workStreams name="workStreams" project="${project}" workItem="${workItem}"/>
    </script>
</head>
<body>
    <jsp:include page="include/header.jsp" />

    <form id="delete" action="delete-item-action" method="get">
        <div>
            <input type="hidden" name="id" value="${workItem.id}" />
            <input type="hidden" name="board" value="${board}" />
        </div>
    </form>

    <form id="edit" action="edit-item-action" method="post">
        <div class="column">
            <input type="hidden" name="board" value="${board}" />
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
<!--                         <td style="text-align: right"><button id="save-button" type="submit">Save and Print</button></td> -->
                    </tr>
                </table>
            </fieldset>
        </div>
    </form>
    
    <div class="column">
        <fieldset>
            <legend>Comments</legend>
            <div class="comments">
                <textarea id="commentsText" placeholder="Add comment"></textarea>
                <div class="add-controls">
                    <button id="addComment">Add comment</button>
                    <button id="cancelComment">X</button>
                </div>
            </div>
            
            <div id="commentSectionContainer">
                <c:forEach var="comment" items="${workItem.commentsInReverseOrder}">
                    <div class="commentSection">
                        <span class="commentSectionAuthor"><c:out value="${comment.addedBy}" /></span>
                        <span class="commentSectionDate"><c:out value="${comment.whenAdded}" /></span>
                        <span class="commentText"><c:out value="${comment.commentText}" /></span>
                    </div>
                </c:forEach>
            </div>
        </fieldset>
    </div>
    
    <script type="text/javascript">
    (function() {
        var commentsField = $('#commentsText');
        
        commentsField.focusin(function() {
            commentsField.parent().addClass('focus');
        });
        $("#cancelComment").click(function() {
            commentsField.val('');
            commentsField.parent().removeClass('focus');
        });
        
        $("#addComment").click(function() {
            $.post("../comment", { id: $('#id').val(), userName: $('#userField').val(), comment: commentsField.val() })
                .success(function(data) {
                    eval("var jsonData = " + data);
                    
                    commentsField.val('');
                    commentsField.parent().removeClass('focus');
                    
                    var a = $('<div class="commentSection"></div>');
                    a.prependTo("#commentSectionContainer");
                    
                    a.append('<span class="commentSectionAuthor">' + jsonData.addedBy + '</span>');
                    a.append('<span class="commentSectionDate">' + jsonData.whenAdded + '</span>');
                    a.append('<span class="commentText">' + jsonData.commentText + '</span>');
                })
                .error(function(data) {
                    console.log("error", data);
                });
        });
    })();
    </script>
    
</body>
</html>
