<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="org.joda.time.LocalDate"%>
<%@page import="java.util.List"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.utils.DateUtils"%>

<html>
<head>

<style>
.space {
	position: relative;
	width: 500px;
	height: 11px;
}

.card {
	position: relative;
	border: solid 3px #000000;
	width: 580px;
	height: 310px;
	color: #FFFFFF;
	font-family: verdana;
	font-weight: bold;
	font-size: 11px;
	text-align: center;
}

.type {
	border-bottom: solid 1px #585858;
	width: 580px;
	height: 15px;
	position: absolute;
	top: 0px;
	left: 0px;
	font-family: verdana;
	font-size: 12px;
	text-align: center;
	color: #000000;
	font-weight: lighter;
	
}
.qrcode {
	width: 70px;
	height: 70px;
	position: absolute;
	top: 25px;
	left: 10px;
	font-family: verdana;
	font-size: 18px;
	color: #000000;
	text-align: left;
}


.itemName {
	background-color: #FFFFFF;
	width: 280px;
	height: 70px;
	position: absolute;
	top: 25px;
	left: 10px;
	font-family: verdana;
	font-size: 18px;
	color: #000000;
	text-align: left;
}

.id {
	background-color: #FFFFFF;
	width: 100px;
	height: 55px;
	position: absolute;
	border: solid 2px #585858;
	top: 36px;
	right: 10px;
	color: #000000;
	text-align: center;
	vertical-align: middle;
	font-family: verdana;
	font-size: 30px;
}

.size {
	background-color: #FFFFFF;
	width: 100px;
	height: 55px;
	position: absolute;
	border: solid 2px #585858;
	top: 113px;
	right: 10px;
	color: #000000;
	text-align: center;
	vertical-align: middle;
	font-family: verdana;
	font-size: 30px;
}

.importance {
	background-color: #FFFFFF;
	width: 100px;
	height: 55px;
	position: absolute;
	border: solid 2px #585858;
	top: 190px;
	right: 10px;
	color: #000000;
	text-align: center;
	vertical-align: middle;
	font-family: verdana;
	font-size: 30px;
}

.notes {
	float:bottom;	
	background-color: #FFFFFF;
	width: 440px;
	height: 132px;
	top: 113px;
	position: absolute;
	border: solid 2px #585858;
	bottom: 10px;
	left: 10px;
	color: #000000;
	text-align: left;
}

.label {
	width:100%;
	height:18px;
	top:-20px;
	position: relative;
	background-color: #FFFFFF;
	color: #000000;
	text-align: left;
	font-family: verdana;
	font-size: 14px;
}

.field {
	position: relative;
	background-color: #FFFFFF;
	top: -10px;
	color: #000000;
	text-align: inherit;
	font-family: inherit;
	font-size: inherit;
	vertical-align: inherit;
}

.footer {
	background-color: #FFFFFF;
	width: 560px;
	height: 25px;
	top: 263px;
	position: absolute;
	bottom: 10px;
	left: 10px;
	color: #000000;
	text-align: left;
}

.stamp {
	float: left;
	background-color: #FFFFFF;
	width: 85px;
	height: 22px;
	position: relative;
	border: solid 1px #585858;
	bottom: 10px;
	color: #000000;
	text-align: left;
	margin-right: 6px;
	margin-top: 3px;
}

.phase {
	width:85px;
	height:10px;
	position: relative;
	background-color: #FFFFFF;
	color: #000000;
	text-align: left;
	font-family: verdana;
	font-size: 8px;
}

.date {
	text-align: left;
	font-family: verdana;
	font-size: 11px;
}



</style>
</head>
<%
    KanbanProject project = (KanbanProject) request.getAttribute("project");
    String[] ids = (String[]) request.getAttribute("ids");
    for (int i=0 ; i < ids.length ; i++) {
    
      WorkItem item = project.getWorkItemTree().getWorkItem(Integer.parseInt(ids[i]));
%>
		<div class="card">
		  <div class="itemName"><%= item.getName() %></div>
		  <div class="notes">
		    <div class="label">Notes</div>
		    <div class="field"><%= item.getNotes() == null ? "" : item.getNotes() %></div>
		  </div>
		  <div class="id">
		    <div class="label">Id</div>
		    <div class="field"><%= item.getId() %></div>
		  </div>
		  <div class="size">
		    <div class="label">Size</div>
		    <div class="field"><%= item.getSize() %></div>
		  </div>
		  <div class="importance">
		    <div class="label">Importance</div>
		    <div class="field"><%= item.getImportance() %></div>
		  </div>
		  <div style="background-color: <%= item.getType().getBackgroundColour() %>;" class="type"><%= item.getType().getName().toUpperCase() %></div>
		  <div class="footer">
		    <%
		        List<String> phases = item.getType().getPhases();
		    		      for(String phase : phases) {
		    		         LocalDate date = item.getDate(phase);
		    		         String dateString = "";
		    		         if (date != null) {
		    		           dateString = DateUtils.formatConventionalNewZealandDate(date);
		    		         }
		    %>
			    <div class="stamp">
			      <div class="phase"><%= phase %></div>
			      <div class="date"><%= dateString %></div>
			    </div>
		      <%
		      }
		    %>
		  </div>
		</div>
		<div class="space"></div>
      <%
    }
%>
<body>
</body>
</html>