<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<jsp:include page="include/header-head.jsp"/>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/edit.css" />

	<title>Kanban: edit a work item</title>

<script type="text/javascript">
	$(document).ready(function() {
		$(".presetColor").click(function () {
		  var col = rgbToHex($(this).css("background-color"));
      $('#colorSelector').ColorPickerSetColor(col);
      $('#colorSelector div').css("background-color", "#" + col);
		});
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
    <jsp:include page="include/header.jsp"/>

    <form id="delete" action="delete-item-action" method="post">
        <div>
            <input type="hidden" name="id" value="${workItem.id}" />
        </div>
    </form>
    
    <form id="edit" action="edit-item-action" method="get">
        <fieldset>
            <legend>Edit ${workItem.type.name}</legend>

            <label class="labelClass" for="id">Id:</label>
            <input id="id" size="10" type="text" name="id" readonly="readonly" value="${workItem.id}" /> 
            <br />
            
            <label class="labelClass" for="name">Name:</label>
            <textarea id="name" name="name" rows="2" cols="40">${workItem.name}</textarea>
            <br />
            
            <label class="labelClass" for="size">Size:</label>
            <input id="size" size="10" type="text" name="size" value="${workItem.size}" />
            <br />
            
            <label class="labelClass" for="importance">Importance:</label>
            <input id="importance" size="10" type="text" name="importance" value="${workItem.importance}" />
            <br />
            
            <label class="labelClass" for="workStreams">Work streams:</label>
            <input size="40" type="text" id="workStreams" name="workStreams" value="${workItem.workStreamsAsString}" />
            <br/>
            
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
			    </c:when>
			    <c:otherwise>
	                <input type="checkbox" name="excluded" />
			    </c:otherwise>
			</c:choose>
            <br />
            <label class="labelClass" for="color">Color</label>
            <input size="10" type="text" id="colorid"
	                name="color" value="${workItem.colour}" style="display:none" />
			<div class="wrapper">
			  <div id="colorSelector">
  			  <div style="background-color: ${workItem.colour}">
  			  &nbsp;
  			  </div>
  			</div>
  			<div>
  		    <div class="presetColor" style="background:#D96666;"></div>
  		    <div class="presetColor" style="background:#F2A640;"></div>
  		    <div class="presetColor" style="background:#fbff00;"></div>
  		    <div class="presetColor" style="background:#7EC225;"></div>
  		    <div class="presetColor" style="background:#59BFB3;"></div>
  		    <div class="presetColor" style="background:#668CD9;"></div>
  		    <div class="presetColor" style="background:#B373B3;"></div>
  		  </div>
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
