<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="kanban" uri="/WEB-INF/kanban.tld" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<jsp:include page="include/header-head.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/completed.css?version=${service.version}" />
    
    <title>Kanban: complete</title>

    <style type="text/css">
        .typeColour, .backgroundColour:hover {
            background: ${type.cardColour};
        }
        .backgroundColour {
            background: ${type.backgroundColour};
        }
    </style>
</head>

<body>
    <kanban:htmlColours name="htmlColours" series="${fn:length(type.wallPhases)}" />
    <jsp:include page="include/header.jsp"/>
    <form id="form" method="post" action="">
        <table id="completed-table" class="kanban">
			<thead>
				<tr class="customizedHeader typeColour">
					<th colspan="7">${phase}</th>
				</tr>
			</thead>
                <tr class="subHeader typeColour">
					<th></th>
					<th>Id</th>
					<th>Name</th>
                    <th></th>
					<th>Size</th>
                    <th>Completed date</th>
					<th> 
                        <c:forEach var="phase" items="${type.wallPhases}" varStatus="i">
                            <span class="age-legend" style="background-color:${htmlColours[i.count-1]}">${phase}</span>
                        </c:forEach>
                    </th>
				</tr>
                <c:forEach var="row" items="${board.iterator}">
                    <tr class="row backgroundColour">
                        <c:forEach var="cell" items="${row.iterator}">
    
                            <c:if test="${!cell.emptyCell}">
                                <c:set var="item" value="${cell.workItem}" />
                                
                                <td class="edit, padded">
                                    <div class="editIcon">
                                        <a href="${pageContext.request.contextPath}/projects/${projectName}/completed/edit-item?id=${item.id}">
                                            <img id="edit-work-item-${item.id}-button" src="${pageContext.request.contextPath}/images/edit.png" />
                                        </a>
                                    </div>
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
                                       <td class="itemName formify itemMustHave" data-role="name">${item.name} <kanban:workStreamsInfo workItem="${item}" cssClass="item-work-streams" /></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td class="itemName formify itemNiceToHave" data-role="name">${item.name} <kanban:workStreamsInfo workItem="${item}" cssClass="item-work-streams" /></td>
                                    </c:otherwise>
                                </c:choose>
                                
                                <td class="small color">
                                  <div style="background-color:${item.colour}; width: 10px; height: 10px; border: 1px solid #aaa; margin: 5px">
                                  </div>
                                </td>
                                <td class="size padded">${item.averageCaseEstimate}
                                </td>
                                <td class="completeDate padded">
                                ${item.lastPhaseDate }</td>
                                <td class="age padded">
                                    <div id="work-item-${item.id}" class="${item.type.name}">
                                        <kanban:phasesLengths workItem="${item}" project="${project}" type="${type}"/>
                                    </div>      
                                </td>
                            </c:if>
                        </c:forEach>
                    </tr>
                </c:forEach>
        </table>
    </form>
</body>
</html>
