<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="kanban" uri="/WEB-INF/kanban.tld" %>


<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.joda.time.LocalDate"%>
<%@page import="com.metservice.kanban.utils.WorkingDayUtils"%>
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.KanbanCell"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.KanbanBoard"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumnList"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumn"%>
<%@page import="com.metservice.kanban.model.HtmlColour"%>
<%@page import="com.metservice.kanban.model.WorkItemTypeCollection"%>
<%@page import="com.metservice.kanban.model.BoardIdentifier"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <jsp:include page="include/header-head.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/project.css" />

<title>Kanban: wall</title>
<%
    String scrollTopParam = (String) request.getAttribute("scrollTop");
    int scrollTo = 0;
    if (scrollTopParam != null)
        scrollTo = Integer.parseInt(scrollTopParam);
%>
<script type="text/javascript">
	$(function() {
		$("button.dropdown").button({
            icons: {
                primary: "ui-icon-gear dropdown-gear",
                secondary: "ui-icon-triangle-1-s dropdown-triangle"
            },
            text: false
        }).click(function(){
          $("div.dropdown-menu-wrapper").fadeOut(200);
          $(this).siblings("div.dropdown-menu-wrapper:hidden").fadeIn(200);
          return false;
        });
        $(":not(button.dropdown)").click(function(){
            $("div.dropdown-menu-wrapper").fadeOut(200);
        });

        
	    //Table header stuff
	    var header = $("#kanbantable thead");
	    $("body").append('<table class="kanban" id="headercopy"><thead></thead></table>');
      $("#headercopy thead").append($("#kanbantable thead th").clone());
	     
	    var header_pos = header.offset().top+header.height();
	    $(window).scroll(function () { 
	      if($(window).scrollTop() >= header_pos){
	        $("#headercopy").fadeIn();
	      }else{
	        $("#headercopy").fadeOut();
	      }
	    });
	});
	</script>
	
<script type="text/javascript">
//<![CDATA[
  
			//Changes the card color to FIREBRICK!
            function blockStory(id, type) {
            	//document.forms["form"].action = getBoard() + "/advance-item-action?id=" + id + "&scrollTop=" + getYOffset();
   			 	//document.forms["form"].submit();
            	var item = document.getElementById("work-item-" + id);
            	//if (item.className=='stopped') {
            		//item.className = type;
            	//}
            	//else { item.className = "stopped"; }
            	
            	//$('#itemId').val(id)
            	$("#block-dialog-item-id").html(id);
            	
           		$("#block-dialog").dialog("open");
            }
            
			function markUnmarkToPrint(divId, type, isStopped){
			   var item = document.getElementById(divId);
			   if (item == null) {
				   return;
			   }
			  if (item.className == 'markedToPrint') {
			  	if (isStopped) {
			  		item.className = "blocked";
			  	}
			  	else {
			  		item.className = type;
			  	}
			  } else {
			     item.className = "markedToPrint";
			   }
			}

			function edit(id){
			 document.forms["form"].action = getBoard() + "/edit-item?id=" + id;
			 document.forms["form"].submit();
			 
			}
			
        	function addChild(id){
              document.forms["form"].action = getBoard() + "/add-item?id=" + id;
              document.forms["form"].submit();
        	}
            function move(id, targetId, after){
              document.forms["form"].action = getBoard() + "/move-item-action?id=" + id + "&targetId=" + targetId + "&scrollTop=" + getYOffset() + "&after=" + after;
              document.forms["form"].submit();
            }

//]]> 
		</script>
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

<style type="text/css">
table {
  width: 100%;
  color: #f00;
	margin: 10px 0px 0px 0px;
	border-collapse: collapse;
}

table#headercopy{
  position: fixed;
  top: -10px;
  z-index: 1000;
  opacity: 0.7;
  display: none;
  width:99%;
}

table#headercopy .feature-header{
  background-color: #fff;
}

.age-container {
    float: left;
    width: 100%;
    height: 4px;
}

.age-item {
    height:3px;
    width:3px;
    margin-bottom: 1px;
    margin-right: 1px;
    float: left;
    background-color: black;
}

.itemName {
	width: 80%;
	height: 20px;
	position: absolute;
	top: 6px;
	left: 2px;
	font-family: arial;
	font-size: 12px;
	color: #383838;
	text-align: left;
}

.upIcon:hover,.advanceIcon:hover,.downIcon:hover,.editIcon:hover,.addIcon:hover,.stopIcon:hover
	{
	-moz-opacity: 0.5;
	opacity: 0.5;
}

.icons{
  width: 10%;
  position:absolute;
  right: 0px;
  top: 10px;
}

.upIcon, .downIcon, .advanceItem{
  width: 16px;
  height: 16px;
}

.upIcon, .downIcon {
	position: relative;
	right: 10px;
}



.addIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: absolute;
	width: 16px;
	height: 16px;
	left: 20px;
	top: 50px;
}



.markedToPrint {
	background: #EEEEEE;
}


.horizontalLine {
	border-top: 2px black solid;
}

<%			int cardWidth = 155;
            int ageItemWidth = 4;


            String boardType = (String) request.getAttribute("boardType");
            BoardIdentifier board = BoardIdentifier.valueOf(boardType.toUpperCase());

            WorkItemTypeCollection workItemTypes = project.getWorkItemTypes();
            for (WorkItemType workItemType : workItemTypes) {
                String name =
                    workItemType.getName();
                HtmlColour cardColour = workItemType.getCardColour();
                HtmlColour backgroundColour = workItemType.getBackgroundColour();%> 
div[data-role="card"]{
  height: 60px;
	width: 95%;
	margin: 1px;
	padding: 3px;
  position: relative;
  -webkit-border-radius: 5px;
    -moz-border-radius: 5px;
         border-radius: 5px;
}

.card:hover{
  border: 1px black solid;
	padding: 2px;

}
                
.<%=name%> {
	background: <%=cardColour.toString()%>;
	
}

.markedToPrint:hover {
	border: 1px black solid;
	background: #CCCCCC;
}

.<%=name%>-header {
	background: <%=cardColour.toString()%>;
	border: 1px #989898 dotted;
	height: 30px;
	width: 164px;
	font-family: verdana;
	font-size: 14px;
	color: black;
}

.<%=name%>-background {
	background: <%=backgroundColour.toString()%>;
	border: 1px #C8C8C8 dotted;
	width: 155px;
	height: 30px;
}
<%}%>

</style>
<script type="text/javascript">
$(function() {
	$("#block-dialog").dialog({
		modal: true,
		autoOpen: false,
		buttons: {
			Ok: function() {
				$.get(getBoard() + "/block-item-action", 
						{ 
							itemId: $('#block-dialog-item-id').html(), 
							userName: $('#userField').val(), 
							comment: $("#block-comment").val() 
						}).success(function() { window.location = getBoard(); });
				
				$(this).dialog("close");
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		}
	}); 	
});
</script>
</head>
<body onload="javscript:setPosition(${scrollTop});">
<div id="block-dialog" title="Block/Unblock work item">
    <p>Please enter a reason for blocking/unblocking work item <span id="block-dialog-item-id"></span>:</p>
    <textarea name="block-comment" id="block-comment" rows="5" cols="30"></textarea>
</div>
    <jsp:include page="include/header.jsp"/>

    <form id="form" method="post" action="">
        <input type="hidden" name="itemId" />
        <table class="kanban" id="kanbantable">
          <thead>
            <tr>
                <%
                    KanbanBoardColumnList columns = project.getColumns(board);
					int column_index = 0;
                    for (KanbanBoardColumn column : columns) {
                                    
                        pageContext.setAttribute("column", column);
                                        
                        pageContext.setAttribute("column_index", column_index);
						column_index++;
						int wipLimit = column.getWIPLimit();
                        String type = column.getWorkItemType().getName();
                %>
                
                <c:choose>
                    <c:when test="${column.WIPLimit > -1 &&  board.itemsInColumn[column.phase] > column.WIPLimit}">
                        <c:set var="columnHeaderClass" value="${column.workItemType.name}-header brokenWIPLimit"></c:set>
                    </c:when>
                    <c:otherwise>
                        <c:set var="columnHeaderClass" value="${column.workItemType.name}-header"></c:set>
                    </c:otherwise>
                </c:choose>
                <th title="WIP Limit: ${column.WIPLimit > 0 ? column.WIPLimit : "None"}" class="${columnHeaderClass}" id="phase_<%= column_index %>">${column.phase}</th>
                <script>
                	$(document).ready(function(){
                		var i = 0; 
                		$('.horizontalLine td:nth-child(<%= column_index %>) .size').each(function(){
                			var temp = parseInt($(this).html());
                			if(!isNaN(temp)){
                				i+=temp;
                			}
                		});
                	});
                </script>
                <%
                    }
                %>
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

                            if (!cell.isEmptyCell()) {
                            WorkItem item = cell.getWorkItem();
                    %>
                    <%--  remove after refactoring --%>
                    <c:set var="item" value="${cell.workItem}" />
                    
                    <td class="${item.type.name}-background">
                      
                        <div
                            onclick="javascript:markUnmarkToPrint('work-item-${item.id}','${item.type.name}', ${item.blocked})"
                            id="work-item-${item.id}" title="Notes: ${fn:escapeXml(item.notesAndBlock)}"
                            class="${item.type.name} ${item.blocked ? "blocked" : ""}" data-role="card">
                            
                            <div class="age-container" style="background-color:${item.colour}">
                                <% 
                                LocalDate phaseStartDate = item.getDate(item.getCurrentPhase());
                                int days = WorkingDayUtils.getWorkingDaysBetween(phaseStartDate, new LocalDate());
                                
                                int itemsPerRow = (int) Math.floor(cardWidth/(double)ageItemWidth);
                                
                                for (int i=0; i<days && i < itemsPerRow; i++) {
                                    if (i == itemsPerRow - 1) {
                                %>
                                        <div class="age-item" style="background-color: red; width: 6px; "></div>
                                <%  
                                    } else { 
                                %>
                                        <div class="age-item"></div>
                                <%    
                                    }
                                }
                                %>
                            </div>
                                
                            <div class="itemName">
                                <span class="${item.excluded ? "itemExcluded" : "" }">${item.id}</span>: <span class="work-item-name">${item.name}</span>
                                <kanban:workStreamsInfo workItem="${item}" cssClass="item-work-streams" />
                            </div>
                            
                            <div class="icons">
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
                                    if (project.getWorkItemTypes().getTreeNode(item.getType()).hasChildren()) {
                                %>
                                    <a class="add" href="javascript:addChild(${item.id});">
                                        <img class="add" alt="Advance" src="${pageContext.request.contextPath}/images/list-add.png" />
                                        Add
                                    </a>
                                <%
                                        }
                                 %>
                                <a href="javascript:blockStory(${item.id},'${item.type.name}');" class="last">
                                  <img class="stop" id="block-work-item-${item.id}-button" src="${pageContext.request.contextPath}/images/stop.png" />
                                  <c:choose>
                                    <c:when test="${item.blocked}">Unblock</c:when>
                                    <c:otherwise>Blocked</c:otherwise>
                                  </c:choose>
                                </a>
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
                    <%
                        } else {
                    %><td
                        class="${cell.workItemType.name}-background"></td>
                    <%
                        }
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
