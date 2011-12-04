<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<jsp:include page="include/header-head.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/backlog.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/boards.css" />

<script>
$(document).ready(function(){
    $("#backlog-table").tableDnD({
        onDragClass: "dragClass",
        onDrop: function(table, row) {
            var rows = table.tBodies[0].rows;
            var ids = [];
            for (var i=0; i<rows.length-1; i++) {
                if (rows[i].id != "") {
                    ids.push(rows[i].id);
                }
            }
            reorder(row.id, ids);
        },
        dragHandle: "dragHandle"
    });

	$("#backlog-table tr:not(tr.nodrag)").hover(function() {
	    $(this.cells[0]).addClass('showDragHandle');
	    $(this).addClass('showDragHandle');
	}, function() {
	    $(this.cells[0]).removeClass('showDragHandle');
	});

	function saveItem(element){
	    var workItemIdToChange = element.parents("tr").attr("id");
	    var elementToChange = element.attr("data-role");
	    var postData = {
	        newValue: element.val()
	    };
	    $.ajax({
	        type: "POST",
	        url: window.location.pathname + "/edit-item/" + workItemIdToChange + "/" + elementToChange,
	        data: postData,
	        error: function(){
	            alert("Failed to update story.");
	        }
	    });
	    
	    element.parent().html(element.val()).removeClass("formified").addClass("formify");
	}
	
	function cancelEdit(element) {
		var originalValue = $(element).attr("originalValue");
		element.parent().html(originalValue).removeClass("formified").addClass("formify");
	}
   		  
	$(".formify").click(function(){
  
	    //Dont add an input to a td with an input in it already!
    	if ($(this).children("input").size() > 0){
    		return false;
    	}
      
    	//Find all other inputs and save them
    	$.each($("tr:not(tr.nodrag) input"), function(index, value){
    		saveItem($(this));
    	});
      
    	//Change the content to an input tag and autopopulate the value
    	var originalValue = $(this).html().trim();
    	var newInput = $("<input />")
    			.attr("data-role", $(this)
    			.attr("data-role"))
    			.attr("originalValue", originalValue)
    			.css("width", "50%").val(originalValue);
    	$(this).html("").append(newInput);
    	newInput.focus();
      
    	//Add the tooltip for name
    	if($(this).attr("data-role") == "name"){
    		$(this).append("<span style=\'color:#aaa\'>Press <b>Enter</b> to save</span>");
    	}
      
    	//When the user presses enter, save it
    	$(this).find("input").keyup(function(event) {
    		switch (event.which) {
    			case 13:
    				saveItem($(this));
    				break;
    			case 27:
    				cancelEdit($(this));
    				break;
    		}
    	});
    });


      
    $("tr#new_story input").keypress(function(event) {
    	if (event.which == 13){
        	var row = $(this).parents("tr#new_story");
              
        	var postData = {
        		"type": row.find("input[name=type]").val(),
        		"name": row.find("input[name=name]").val(),
        		"averageCaseEstimate": "",
        		"importance": row.find("input[name=importance]").val(),
        		"color": "FFFFFF",
        		"notes": "",
        		"workStreamsSelect": "${workStreams[project.name]}"
        	};
              
        	$.ajax({
        		type: "GET",
        		url: window.location.pathname + "/add-item-action",
        		data: postData,
        		success: function(){
        			window.location.reload();
        		},
        		error: function(){
        			alert("Failed to create story");
        		}
        	});
    	}	    
    });  	
});
</script>

<title>Kanban</title>
<style type="text/css">

table{
  width: 100%;
	font-family: arial;
	font-size: 14px;
	color: #383838;
}

td.small{
  width: 20px;
}

.dragClass {
	background: ${type.cardColour};
}

.kanban {
	margin: 10px 0px 0px 0px;
	background: ${type.backgroundColour};
	border-collapse: collapse;
}


.upIcon:hover,.advanceIcon:hover,.downIcon:hover,.editIcon:hover,.addIcon:hover
	{
	-moz-opacity: 0.5;
	opacity: 0.5;
}

.showDragHandle {
	cursor: move;
}

.advanceIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: relative;
	width: 16px;
	height: 16px;
	cursor:default
}

.editIcon {
	-moz-opacity: 1;
	opacity: 1;
	position: relative;
	width: 16px;
	height: 16px;
	cursor:default
}

.horizontalLine {
	border-top: 1px white solid;
	height: 30px;
}

.customizedHeader {
	background: ${type.cardColour};
	border:1px #989898 dotted;
	height:30px;
	width: 164px;
	font-family:verdana;
	font-size:14px;
	color:black;
}
</style>
</head>
<body>
	<jsp:include page="include/header.jsp" />

		<table id="backlog-table" class="kanban">
			<thead>
				<tr class="customizedHeader">
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
      
				<tr id="${cell.workItem.id}" class="horizontalLine">
					<td class="dragHandle" style="width:35px" ></td>  				
					<td class="editIcon">
					<a href="${pageContext.request.contextPath}/projects/${projectName}/backlog/edit-item?id=${cell.workItem.id}">
							<img id="edit-work-item-${cell.workItem.id}-button" src="${pageContext.request.contextPath}/images/edit.png" />
					</a>
					</td>
					<c:choose>
						<c:when test="${cell.workItem.excluded}">
							<td class="itemName itemNumber itemExcluded">
								${cell.workItem.id}
						    </td>
						</c:when>
						<c:otherwise>
							<td class="itemName itemNumber itemIncluded">
						        ${cell.workItem.id} 
						    </td>
						</c:otherwise>
					</c:choose>
                    <c:choose>
                        <c:when test="${cell.workItem.mustHave}">
					       <td id="item-name-${rowNumber.count}" class="itemName formify itemMustHave" data-role="name">${cell.workItem.name}</td>
                        </c:when>
                        <c:otherwise>
                            <td id="item-name-${rowNumber.count}" class="itemName formify itemNiceToHave" data-role="name">${cell.workItem.name}</td>
                        </c:otherwise>
                    </c:choose>
					<td class="small color">
					  <div style="background-color:${cell.workItem.colour}; width: 10px; height: 10px; border: 1px solid #aaa; margin: 5px">
					  </div>
					</td>
					<td class="small formify" data-role="size" >
						<c:if test="${cell.workItem.averageCaseEstimate > 0 }">
	                       ${cell.workItem.averageCaseEstimate}
						</c:if>
					</td>
					<td class="small formify" data-role="importance">
						<c:if test="${cell.workItem.importance > 0 }">
							${cell.workItem.importance}
						</c:if>
					</td>
					<td class="small advanceIcon" align="center" >
						<c:if test="${!item.inFinalPhase}">
                            <a href="backlog/advance-item-action?id=${cell.workItem.id}&phase=${cell.workItem.currentPhase}">
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
					  <!-- <div style="background-color:${cell.workItem.colour}; width: 10px; height: 10px; border: 1px solid #aaa; margin: 5px">
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
