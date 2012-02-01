<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="com.metservice.kanban.model.WorkItem"%>

<c:set var="secondLevel" value="${project.workItemTypes.root.children[0].value}" /> 

<script type="text/javascript">
    $(document).ready(function(){
        $("#graphs").click(function(){
        	var pos = $(this).position();
        	$("#graph_dropdown").css({
        		left: pos.left + "px",
        		top: (pos.top + 35) + "px" 
        	});
            $("#graph_dropdown").fadeToggle();
            $("#graphs").toggleClass("active");
        });
    });
</script>

<style>
#userContainer input {
}

#userContainer input[readonly] {
    cursor: pointer; 
    background-color: #ebebe4;
}
/*
#userContainer {
    margin: 35px 10px 0 0; 
    border: solid 1px grey; 
    border-radius: 4px; 
    padding-left: 10px; 
    padding-right: 10px; 
}
.focus #userContainer {
    border: none; 
}

#currentUser {
    font-weight: bold;
}
.focus #currentUser {
    display: none;
}

#userField {
    border: solid 1px grey; 
    font-family: Arial,Helvetica,sans-serif; 
    font-size: 11px; 
    font-weight: bold;
    display: none;
}
.focus #userField {
    display: inline;
}
*/
</style>
    <c:if test="${!empty error}">
        <div id="error-dialog" title="Error">
            ${error}
        </div>
        <script>
        	$(function() {
        		$("#error-dialog").dialog({
        			modal: true,
        			buttons: {
        				Ok: function() {
        					$(this).dialog("close");
        				}
        			}
        		});        		
        	});
        </script>
    </c:if>

    <div class="header">
        
        <div class="user-home">${service.home.absolutePath}</div>
        <div class="version">VERSION: ${service.version}</div>
        <div id="projectDropdown">
			<form id="header" method="post" action="" style="display: inline;">
                <input type="hidden" name="chartName" value="${chartName}" />
                <input type="hidden" name="workItemTypeName" value="${workItemTypeName}" />

    			<label class="projectPicker" for="projectPicker"><a href="${pageContext.request.contextPath}">Project</a>:</label>

				<select id="projectPicker" onchange="changeProject('projectPicker')">
                        <option selected="selected">${project.name}</option>
						<c:forEach var="projectName" items="${service.filteredProjects}">
                            <c:if test="${projectName != project.name}">
					           <option >${projectName}</option>
                            </c:if>
						</c:forEach>
				</select>
			</form>
            
 			<form action="${pageContext.request.contextPath}/projects/${project.name}/${boardType}/set-work-stream" style="display: inline;">
                <input type="hidden" name="chartName" value="${chartName}" />
                <input type="hidden" name="workItemTypeName" value="${workItemTypeName}" />
			<label for="workStreamPicker">Work stream:</label>
 				<select id="workStreamPicker" name="workStream" onchange="form.submit()"> 
					<option value="">[all streams]</option>
					<c:forEach var="workStream" items="${project.workStreams}">
						<option <c:if test="${workStreams[projectName] == workStream}">selected="selected"</c:if>>${workStream}</option>
					</c:forEach>
				</select>
 			</form>
            
            <span id="userContainer">
                Current User
                <input id="userField" name="userField" type="text" value="Unknown user" readonly="readonly" />
            </span>
        </div>
<%--         <div id="add-top-level-item-button" class="button" onclick="javascript:addTopLevel(<%= WorkItem.ROOT_WORK_ITEM_ID%>);" > --%>
<%--         	<div class ="textOnButton"><span style="font-weight:bold;font-size:120%;line-height:100%">+</span> Add ${project.workItemTypes.root.value}</div> --%>
<!--         </div> -->
   		<a id="add-top-level-item-button" href="${pageContext.request.contextPath}/projects/${project.name}/backlog/add-item?id=<%= WorkItem.ROOT_WORK_ITEM_ID%>" class="button">
      			<span style="font-weight:bold;font-size:120%;line-height:100%">+</span> Add ${project.workItemTypes.root.value}
   		</a>
       	
       	<a id="backlog-button" href="${pageContext.request.contextPath}/projects/${project.name}/backlog" class="button">Backlog</a>
        <a id="wall" href="${pageContext.request.contextPath}/projects/${project.name}/wall" class="button">Wall</a>
        <a id="journal" href="${pageContext.request.contextPath}/projects/${project.name}/journal" class="button">Journal</a>
        <a id="complete" href="${pageContext.request.contextPath}/projects/${project.name}/completed" class="button">Complete</a>

        <c:if test="${boardType == 'wall' || boardType == 'backlog' }">
            <div id="print" class="button" onclick="javascript:printCards();" ><div class ="textOnButton">Print</div></div>
		</c:if>
			
		<div id="graphs" class="button">
            <div class ="textOnButton">Charts</div>
        </div>
        
<!--             <button id="charts_button">Charts2</button> -->

          <div id="graph_dropdown">
          
          	<a id="cumulative-flow-chart-1-button" href="${pageContext.request.contextPath}/projects/${project.name}/chart?chartName=cumulative-flow-chart&workItemTypeName=${project.workItemTypes.root.value.name}" class="button">
              <img src="${pageContext.request.contextPath}/images/cumulative-flow-chart.png" /> ${project.workItemTypes.root.value.name}
	        </a>
			      
            <c:if test="${secondLevel != null}" >
            	<a id="cumulative-flow-chart-2-button" href="${pageContext.request.contextPath}/projects/${project.name}/chart?chartName=cumulative-flow-chart&workItemTypeName=${secondLevel.name}" class="button">
                	<img src="${pageContext.request.contextPath}/images/cumulative-flow-chart.png" /> ${secondLevel.name}
                </a>
            </c:if>
           
           <a id="cycle-time-chart-1-button" href="${pageContext.request.contextPath}/projects/${project.name}/chart?chartName=cycle-time-chart&workItemTypeName=${project.workItemTypes.root.value.name}" class="button">
                <img src="${pageContext.request.contextPath}/images/cycle-time-chart.png" /> ${project.workItemTypes.root.value.name}
           </a>
            
            <c:if test="${secondLevel != null}" >
            	<a id="cycle-time-chart-2-button" href="${pageContext.request.contextPath}/projects/${project.name}/chart?chartName=cycle-time-chart&workItemTypeName=${secondLevel.name}" class="button">
              		<img src="${pageContext.request.contextPath}/images/cycle-time-chart.png" /> ${secondLevel.name}
            	</a>
            </c:if>
            
            <a id="burn-up-chart-button" href="${pageContext.request.contextPath}/projects/${project.name}/chart?chartName=burn-up-chart&workItemTypeName=${project.workItemTypes.root.value.name}" class="button">
                <img src="${pageContext.request.contextPath}/images/burn-up-chart.png" /> Burn-Up 
            </a>
            
            <a id="estimates-chart-button" href="${pageContext.request.contextPath}/projects/${project.name}/chart?chartName=estimates-burn-up-chart&workItemTypeName=${project.workItemTypes.root.value.name}" class="button">
                <img src="${pageContext.request.contextPath}/images/burn-up-chart.png" /> Estimates Burn-Up 
            </a>
            
          </div>
			
        <a id="pet" href="${pageContext.request.contextPath}/projects/${project.name}/estimates" class="button">Estimates</a>
        <a id="admin" href="${pageContext.request.contextPath}/projects/${project.name}/admin" class="button">Admin</a>
    </div>
    
<script type="text/javascript">
(function() {
    var currentUser = $.cookie('kanban.current.user');
    if (currentUser == null) {
        currentUser = "Unknown user";
    }
    $('#userField').val(currentUser);
    
    $('#userField').click(function() {
    	$('#userField').removeAttr('readonly');
        $('#userField').focus();
        return false;
    });
    $('#userField').keypress(function(event) {
        if (event.which == 13) {
            $.cookie('kanban.current.user', $(this).val(), { expires: 100, path: "/" });
            $("#userField").attr('readonly', 'readonly');
        }
    });
})();
</script>
    
