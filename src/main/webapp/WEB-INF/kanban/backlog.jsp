<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="com.metservice.kanban.model.KanbanBacklog"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>
<%@page import="com.metservice.kanban.model.KanbanProjectConfiguration"%>
<%@page import="com.metservice.kanban.model.WorkItemTypeCollection"%>
<%@page import="com.metservice.kanban.model.KanbanBoard"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.KanbanCell"%>
<%@page import="com.metservice.kanban.model.BoardIdentifier"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<jsp:include page="include/header-head.jsp"/>

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
   		  $.ajax({
           type: "POST",
           url: window.location.pathname + "/edit-item-action",
           data: "id=" + element.parents("tr").attr("id") + "&" + element.attr("data-role") + "=" + element.val(),
         });
         
         element.parent().html(element.val()).removeClass("formified").addClass("formify");
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
   		  $(this).html("<input value=\"" + $(this).html().trim() + "\" data-role=\"" + $(this).attr("data-role") + "\" style=\"width: 50%\" />");
   		  
   		  //Add the tooltip for name
   		  if($(this).attr("data-role") == "name"){
   		    $(this).append("<span style=\'color:#aaa\'>Press <b>Enter</b> to save</span>");
   		  }
   		  
   		  //When the user presses enter, save it
   		  $(this).find("input").keypress(function(event) {
   		    
   		    if (event.which == 13){
   		      saveItem($(this));
   		    }
   		    
   		  });
   		});
  $(".advance").click(function(){
    var parent = $(this).parents("tr");
    $.ajax({
       type: "POST",
       url: window.location.pathname + "/advance-item-action",
       data: "id=" + parent.attr("id")
     });
     parent.hide();
  }); 
  
  $("tr#new_story input").keypress(function(event) {
    if (event.which == 13){
      var row = $(this).parents("tr#new_story");
      var name = row.find("input[name=name]").val();
      var size = row.find("input[name=size]").val();
      var importance = row.find("input[name=importance]").val();
      var type = row.find("input[name=type]").val();
      $.ajax({
         type: "GET",
         url: window.location.pathname + "/add-item-action",
         data: "type="+type+"&name="+name+"&size="+size+"&importance="+importance,
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
<%
    KanbanProject project = (KanbanProject) request.getAttribute("project");
%>

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
					<th ></th>
					<th ></th>
					<th ></th>
					<th >${phase}</th>
					<th ></th>
					<th ></th>
					<th ></th>
					<th ></th>
				</tr>
			</thead>
      <tbody>
			<c:forEach var="cell" items="${kanbanBacklog}">
      
				<tr id="${cell.workItem.id}" class="horizontalLine">
					<td class="dragHandle" style="width:35px" ></td>  				
					<td class="editIcon">
					<a href="<%= request.getContextPath() + "/projects/" + request.getAttribute("projectName") %>/backlog/edit-item?id=${cell.workItem.id}">
							<img id="edit-work-item-${cell.workItem.id}-button"
								src="<%=request.getContextPath()%>/images/edit.png" />
					</a>
					</td>
					<c:choose>
						<c:when test="${cell.workItem.excluded}">
							<td class="itemName"  style="font-family: arial;	font-size: 14px; color: #383838; text-align: center; width:25px; text-decoration: line-through">
								${cell.workItem.id}
						    </td>
						</c:when>
						<c:otherwise>
							<td class="itemName"  style="font-family: arial;	font-size: 14px; color: #383838; text-align: center; width:25px">
						        ${cell.workItem.id} 
						    </td>
						</c:otherwise>
					</c:choose>
					<td class="itemName formify" data-role="name">${cell.workItem.name}</td>
					<td class="small color">
					  <div style="background-color:${cell.workItem.colour}; width: 10px; height: 10px; border: 1px solid #aaa; margin: 5px">
					  </div>
					</td>
					<td class="small formify" data-role="size" >
						<c:if test="${cell.workItem.size > 0 }">
	                       ${cell.workItem.size}
						</c:if>
					</td>
					<td class="small formify" data-role="importance">
						<c:if test="${cell.workItem.importance > 0 }">
							${cell.workItem.importance}
						</c:if>
					</td>
					<td class="small advanceIcon" align="center" >
							<c:if test="${! item.inFinalPhase}">
								<img class="advance"
									src="<%=request.getContextPath()%>/images/go-next.png" />
							</c:if>
					</td>
				</tr>
			</c:forEach>
      <tr id="new_story" class="nodrop nodrag">
          <td></td>
          <td>+</td>
          
          <td></td>
          <td>
            <input name="name"  style="width:75%" />
            <span style="color:#aaa">Press <b>Enter</b> to create new story</span>
          </td>
          <td class="small color">
					  <!-- <div style="background-color:${cell.workItem.colour}; width: 10px; height: 10px; border: 1px solid #aaa; margin: 5px">
					  </div> -->
					</td>
					<td class="small" data-role="size" >
            <input name="size" style="width:50%" />
					</td>
					<td class="small" data-role="importance">
            <input name="importance" style="width:50%" />
            
        		<input type="hidden" name="type" value="${type}" />
					</td>
      </tr>
    </tbody>
	</table>
</body>
</html>
