<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.KanbanProjectConfiguration"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/header.js" ></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/header.css"/>


<title></title>

<style type="text/css">


legend {     
margin-left: 1em;     
padding: 0;     
color: #000;     
font-weight: bold;    
}

fieldset {     
float: left;     
clear: both;     
width: 900px;     
margin: 0 0 0 0;     
padding-top: 1.5em;     
padding-bottom: 1.5em;     
border: 1px solid #BFBAB0;     
background-color: #F2EFE9;     
font-family: Verdana;
font-size: 12px;
}

input[readonly] {
background: #F2EFE9;
border: 1px solid #BFBAB0;     
}

fieldset.submit {     
float: left;     
clear: both;     
padding-top: 0.3em;
padding-bottom: 0.3em;
background-color: #DDDDDD;
text-align: right;
}

.labelClass {
  float:left;
  width:6%;
  margin-right:0.5em;
  padding-top:0.2em;
  text-align:right;
  font-weight:bold;
  }
  
</style>
</head>
<body>
<jsp:include page="header.jsp" />


	<form action="edit-project-action">
	 <fieldset>
	    <legend>
		  Edit ${currentProjectName} properties
	    </legend>
	
		<label class="labelClass"  style="display:none;" for="newProjectName">Name:</label> 
		<input size=10 type="text" name="newProjectName" style="display:none;" value="${currentProjectName}" />
	
	    <label class="labelClass" for="name">Settings:</label> 
	    <textarea name="content" rows="25" cols="100">${settings}</textarea>
	
	  </fieldset>
	  <fieldset class="submit">   
	    <input type="submit"/>
	  </fieldset>    
	</form>
</body>
</html>