<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.KanbanProjectConfiguration"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
<jsp:include page="include/header-head.jsp"/>

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
<jsp:include page="include/header.jsp" />


	<form action="edit-project-action">
	 <fieldset>
	    <legend>
		  Edit ${project.name} properties
	    </legend>
	
		<label class="labelClass" for="newProjectName">Name:</label> 
		<input size=10 type="text" name="newProjectName" value="${project.name}" />
	    <br/>
	    <label class="labelClass" for="content">Settings:</label> 
	    <textarea id ="project-properties" name="content" rows="25" cols="100">${settings}</textarea>
	
	  </fieldset>
	  <fieldset class="submit">
	    <button id="submit-query-button" type="submit">Submit Query</button>   
	  </fieldset>    
	</form>
</body>
</html>