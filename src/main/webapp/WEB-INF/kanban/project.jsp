<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="kanban" uri="/WEB-INF/kanban.tld" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page import="com.metservice.kanban.model.KanbanCell"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.KanbanBoard"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <jsp:include page="include/header-head.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/project.css?version=${service.version}" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/project.js?version=${service.version}"></script>

<title>Kanban: wall</title>

<c:if test="${highlight != null}">
    <script type="text/javascript">
    //<![CDATA[
               $(function() {
            	   markUnmarkToPrint('work-item-${highlight}','','');
               });
    //]]> 
    </script>
</c:if>
<%
    KanbanProject project = (KanbanProject) request.getAttribute("project");
    WorkItemType rootType = project.getWorkItemTypes().getRoot().getValue();
%>
    <c:set var="maxAgeItemsPerRow" value="30" />

<style type="text/css">
    <c:forEach var="type" items="${project.workItemTypes}">
    .${type.name}-card-colour {
        background: ${type.cardColour};
    } 
    .${type.name}-background-colour {
        background: ${type.backgroundColour};
    }
    </c:forEach>
</style>

</head>
<body onload="javscript:setPosition(${scrollTop});">
<div id="edit-column-dialog" title="Edit column" style="display: none;">
    Column name: <input id="edit-column-dialog-name" /><br/>
    Column type: <input id="edit-column-dialog-item-type" disabled="disabled" /><br/>
    WIP Limit: <input id="edit-column-dialog-wipLimit" />
    <input type="hidden" id="edit-column-dialog-name-original" />
    <div id="validation-error" class="error"></div>    
</div>
<div id="block-dialog" title="Block work item" style="display: none;">
    <p>Please enter a reason for blocking work item <span id="block-dialog-item-id"></span>:</p>
    <textarea name="block-comment" id="block-comment" rows="5" cols="30"></textarea>
</div>
    <jsp:include page="include/header.jsp"/>

    <form id="form" method="post" action="">
        <input type="hidden" name="itemId" />
        <table class="kanban" id="kanbantable">
          <thead>
            <tr>
                <c:forEach var="column" items="${project.wallColumns}" varStatus="columnIndex">
                    <c:choose>
                        <c:when test="${column.WIPLimit > -1 &&  board.itemsInColumn[column.phase] > column.WIPLimit}">
                            <c:set var="columnHeaderClass" value="${column.workItemType.name}-card-colour wall-header brokenWIPLimit"></c:set>
                        </c:when>
                        <c:otherwise>
                            <c:set var="columnHeaderClass" value="${column.workItemType.name}-card-colour wall-header"></c:set>
                        </c:otherwise>
                    </c:choose>
                    <th title="WIP Limit: ${column.WIPLimit > 0 ? column.WIPLimit : "None"}" class="${columnHeaderClass}" id="phase_${columnIndex.count-1}">
                        <span>${column.phase}</span> <div style="float: right"><a class="ui-button-icon-primary ui-icon ui-icon-gear" href="javascript:editColumn('${column.workItemType.name}', '${column.phase}', '${column.WIPLimit}')">XX</a></div>
                    </th>
                    <script>
                    	$(document).ready(function(){
                    		var i = 0; 
                    		$('.horizontalLine td:nth-child(${columnIndex.count}) .size').each(function(){
                    			var temp = parseInt($(this).html());
                    			if(!isNaN(temp)){
                    				i+=temp;
                    			}
                    		});
                    	});
                    </script>
                </c:forEach>
            </tr>
          </thead>
          <tbody>
            <%
                KanbanBoard kanbanBoard = (KanbanBoard)request.getAttribute("board");
            
                for (KanbanBoardRow row : kanbanBoard) {
                    
                    
                    %><tr class="<%= row.hasItemOfType(rootType) ? "horizontalLine" : ""%>"><%
                    
                    for (KanbanCell cell : row) {
                        // remove after refactoring
                        pageContext.setAttribute("cell", cell);

                    %>
                    <c:choose>
                        <c:when test="${!cell.emptyCell}">
                            <c:set var="item" value="${cell.workItem}" />
                            
                            <td class="${item.type.name}-background-colour background">
                              
                                <div
                                    onclick="javascript:markUnmarkToPrint('work-item-${item.id}','${item.type.name}-card-colour', ${item.blocked})"
                                    id="work-item-${item.id}" title="Last comment: ${empty item.lastComment ? 'no comments' :  fn:escapeXml(item.lastComment)}"
                                    class="${item.type.name}-card-colour ${item.blocked ? "blocked" : ""}" data-role="card">
                                    
                                    <div class="age-container" style="background-color:${item.colour}">
                                        <c:set var="ageCount" value="${item.workingDaysOnCurrentPhase < maxAgeItemsPerRow ? item.workingDaysOnCurrentPhase : maxAgeItemsPerRow}" />                                        
                                        <c:forEach var="ageItem" begin="1" end="${ageCount}">
                                            <c:choose>
                                                <c:when test="${ageItem < maxAgeItemsPerRow}">  
                                                    <div class="age-item"></div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="age-item" style="background-color: red; width: 6px; "></div>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </div>
                                        
                                    <div class="itemName">
                                        <span class="${item.excluded ? "itemExcluded" : "" }">${item.id}</span>: <span class="work-item-name">${item.name}</span>
                                        <kanban:workStreamsInfo workItem="${item}" cssClass="item-work-streams" />
                                    </div>
                                    
                                    <div class="icons">
                                      <div class="topIcon">
                                        <c:if test="${cell.workItemAbove != null}" >
                                          <img onclick="javascript:move(${item.id}, ${cell.workItemTop.id}, false);"
                                               src="${pageContext.request.contextPath}/images/go-top.png" />
                                          </c:if>
                                      </div>
                                      <div class="upIcon">
                                        <c:if test="${cell.workItemAbove != null}">
                                          <img onclick="javascript:move(${item.id}, ${cell.workItemAbove.id}, false);"
                                               src="${pageContext.request.contextPath}/images/go-up.png" />
                                          </c:if>
                                      </div>
                                      <div class="advanceIcon">
                                        <c:if test="${!item.completed && !item.blocked}">
                                            <a href="javascript:advance(${item.id}, '${item.currentPhase}');">
                                                <img src="${pageContext.request.contextPath}/images/go-next.png" />
                                            </a>
                                        </c:if>
                                      </div>
                                      <div class="downIcon">
                                        <c:if test="${cell.workItemBelow != null}">
                                            <img  onclick="javascript:move(${item.id}, ${cell.workItemBelow.id}, true);"
                                              src="${pageContext.request.contextPath}/images/go-down.png" />
                                        </c:if>
                                      </div>
                                    </div>
                                    <div class="edit-menu" title="Edit">
                                        <a class="editButton" href="wall/edit-item?id=${item.id}">
                                            <img alt="Edit" class="edit" id="edit-work-item-${item.id}-button" src="${pageContext.request.contextPath}/images/edit.png" /> 
                                        </a>
                                    </div>
                                    <button class="dropdown"></button>
                                    <div class="dropdown-menu-wrapper" style="display:none;">
                                      <div class="dropdown-menu">
        <%--                                 <a class="edit" href="wall/edit-item?id=${item.id}"> --%>
        <%--                                   <img class="edit" id="edit-work-item-${item.id}-button" src="${pageContext.request.contextPath}/images/edit.png" />  --%>
        <!--                                   Edit -->
        <!--                                 </a> -->
                                        <%
                                            if (project.getWorkItemTypes().getTreeNode(cell.getWorkItem().getType()).hasChildren()) {
                                        %>
                                            <a class="add" href="javascript:addChild(${item.id});">
                                                <img class="add" alt="Advance" src="${pageContext.request.contextPath}/images/list-add.png" />
                                                Add
                                            </a>
                                        <%
                                                }
                                         %>
                                          <c:choose>
                                            <c:when test="${item.blocked}">
                                                <a href="javascript:unblockStory(${item.id},'${item.type.name}');" class="last">
                                                  <img class="stop" id="block-work-item-${item.id}-button" src="${pageContext.request.contextPath}/images/stop.png" />
                                                    Unblock
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="javascript:blockStory(${item.id},'${item.type.name}');" class="last">
                                                  <img class="stop" id="block-work-item-${item.id}-button" src="${pageContext.request.contextPath}/images/stop.png" />
                                                    Blocked
                                                </a>    
                                            </c:otherwise>
                                          </c:choose>
                                      </div>
                                    </div>
                                    
                                    <c:choose>
                                        <c:when test="${item.averageCaseEstimate > 0}">
                                            <div class="size">
                                                ${item.averageCaseEstimate}
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="size" style="border: 0px"></div>
                                        </c:otherwise>
                                    </c:choose>
        
                                    <c:choose>
                                        <c:when test="${item.importance > 0}">
                                            <div class="importance">
                                                ${item.importance}
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="importance" style="border: 0px"></div>
                                        </c:otherwise>
                                    </c:choose>
                                </div></td>
                        </c:when>
                        <c:otherwise> 
                            <td class="${cell.workItemType.name}-background-colour background"></td>
                        </c:otherwise>
                    </c:choose>
                    <%
                        }
                    %>
                </tr>
                <%
                    }
                %>
          </tbody>
        </table>
    </form>
</body>
</html>
