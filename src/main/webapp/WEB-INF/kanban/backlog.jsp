<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="kanban" uri="/WEB-INF/kanban.tld" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<jsp:include page="include/header-head.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/backlog.css?version=${service.version}" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/backlog.js?version=${service.version}"></script>

    <title>Kanban: backlog</title>
    
    <style type="text/css">
        .typeColour {
            background: ${type.cardColour};
        }
        .backgroundColour {
            background: ${type.backgroundColour};
        }
        .dragClass {
	       background: ${type.cardColour};
        }
</style>

</head>
<body onload="javscript:setPosition(${scrollTop});">
	    <jsp:include page="include/header.jsp" />

		<table id="backlog-table" class="backlog-table backgroundColour">
		   <thead>
				<tr class="customizedHeader typeColour">
					<th></th>
					<th></th>
					<th></th>
					<th>${phase}</th>
					<th></th>
					<th>Size</th>
					<th>Imp</th>
					<th></th>
				</tr>
		  </thead>
          <tbody>
    			<c:forEach var="cell" items="${kanbanBacklog}" varStatus="rowNumber">
                    <c:set var="item" value="${cell.workItem}" />
    				<tr id="${item.id}" class="horizontalLine">
    					<td class="dragHandle" style="width:35px" ></td>  				
    					<td class="editIcon">
    					<a href="${pageContext.request.contextPath}/projects/${projectName}/backlog/edit-item?id=${item.id}">
    							<img id="edit-work-item-${item.id}-button" src="${pageContext.request.contextPath}/images/edit.png" />
    					</a>
    					</td>
    					<c:choose>
    						<c:when test="${item.excluded}">
    							<td class="itemName itemNumber itemExcluded">
    								${item.id}
    						    </td>
    						</c:when>
    						<c:otherwise>
    							<td class="itemName itemNumber itemIncluded">
    						        ${item.id} 
    						    </td>
    						</c:otherwise>
    					</c:choose>
                        <c:choose>
                            <c:when test="${item.mustHave}">
    					       <td id="item-name-${rowNumber.count}" class="itemName itemMustHave"><div style="float: left; padding-right: 5px;" class="formify" data-role="name">${item.name}</div> <kanban:workStreamsInfo workItem="${item}" cssClass="item-work-streams" /></td>
                            </c:when>
                            <c:otherwise>
                                <td id="item-name-${rowNumber.count}" class="itemName itemNiceToHave"><div style="float: left; padding-right: 5px;" class="formify" data-role="name">${item.name}</div> <kanban:workStreamsInfo workItem="${item}" cssClass="item-work-streams" /></td>
                            </c:otherwise>
                        </c:choose>
    					<td class="small color">
    					  <div style="background-color:${item.colour}; width: 10px; height: 10px; border: 1px solid #aaa; margin: 5px">
    					  </div>
    					</td>
    					<td class="small formify" data-role="size" >
    						<c:if test="${item.averageCaseEstimate > 0 }">
    	                       ${item.averageCaseEstimate}
    						</c:if>
    					</td>
    					<td class="small formify" data-role="importance">
    						<c:if test="${item.importance > 0 }">
    							${item.importance}
    						</c:if>
    					</td>
    					<td class="small advanceIcon" align="center" >
    						<c:if test="${!item.completed}">
                                <a href="javascript:advance(${item.id}, '${item.currentPhase}');">
    							     <img class="advance" src="${pageContext.request.contextPath}/images/go-next.png" />
                                </a>
    						</c:if>
    					</td>
    				</tr>
    			</c:forEach>
          <tr id="new_story" class="nodrop nodrag">
              <td></td>
              <td>+</td>
              
              <td></td>
              <td>
                <input id="quick-editor-name" name="name"  style="width:75%" />
                <span style="color:#aaa">Press <b>Enter</b> to create new top item</span>
              </td>
              <td class="small color">
    					  <!-- <div style="background-color:${item.colour}; width: 10px; height: 10px; border: 1px solid #aaa; margin: 5px">
    					  </div> -->
			  </td>
			 <td class="small" data-role="size" >
                <input id="quick-editor-size" name="size" style="width:50%" />
              </td>
              <td class="small" data-role="importance">
                <input id="quick-editor-importance" name="importance" style="width:50%" />
                <input type="hidden" name="type" value="${type}" />
              </td>
          </tr>
        </tbody>
	</table>
</body>
</html>
