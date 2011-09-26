<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page import="com.metservice.kanban.model.KanbanProject"%>
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.WorkItemTree"%>
<%@page import="com.metservice.kanban.model.KanbanProjectConfiguration"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/header.js" ></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/header.css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/colorpicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/eye.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/utils.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/layout.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/colorpicker.css" type="text/css" />
<link rel="stylesheet" media="screen" type="text/css" href="${pageContext.request.contextPath}/layout.css" />

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
	width: 500px;
	margin: 0 0 0 0;
	padding-top: 1.5em;
	padding-bottom: 1.5em;
	border: 1px solid #BFBAB0;
	background-color: #F2EFE9;
	font-family: Verdana;
	font-size: 12px;
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
	float: left;
	width: 25%;
	margin-right: 0.5em;
	padding-top: 0.2em;
	text-align: right;
	font-weight: bold;
}


.presetColor{
  width:25px;
  height:25px;
  border:1px solid #000000;
  float:left;
  margin:3px;
}

.wrapper div{
  float:left;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	$(".presetColor").click(function () {
	  var col = rgbToHex($(this).css("background-color"));
    $('#colorSelector').ColorPickerSetColor(col);
    $('#colorSelector div').css("background-color", "#" + col);
	});
});
</script>
</head>
<body onload="setFocus('name-field');">
	<jsp:include page="header.jsp" />
	<form action="add-item-action">
		<fieldset>
			<legend>${legend}</legend>
			<label class="labelClass" for="name">Name:</label>
			<textarea id="name-field" name="name" rows="2" cols="40"></textarea>
			<br /> <label class="labelClass" for="size">Size:</label> <input
				size=10 type="text" name="size" /> <br /> <label class="labelClass"
				for="importance">Importance:</label> <input size=10 type="text"
				name="importance" /> <br /> <label class="labelClass" for="notes">Notes:</label>
			<textarea name="notes" rows="5" cols="40"></textarea>
			<label class="labelClass" for="color">Color</label>
            <input size="10" type="text" id="colorid"
	                name="color" value="#FFFFFF" style="display:none" />
	        <br />
			<div class="wrapper">
				<div id="colorSelector">
					<div style="background-color: #FFFFFF">
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
		<fieldset class="submit">
			<button id="save-button" type="submit">Save</button>
		</fieldset>
		<input type="hidden" name="parentId" value="${parentId}" />
		<input type="hidden" name="type" value="${type}" />
	</form>
</body>
</html>