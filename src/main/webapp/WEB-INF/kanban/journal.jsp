<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="org.joda.time.LocalDate"%>
<%@page import="com.metservice.kanban.utils.WorkingDayUtils"%>
<%@page import="com.metservice.kanban.model.WorkItem"%>
<%@page import="com.metservice.kanban.model.KanbanCell"%>
<%@page import="com.metservice.kanban.model.KanbanBoardRow"%>
<%@page import="com.metservice.kanban.model.KanbanBoard"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumnList"%>
<%@page import="com.metservice.kanban.model.KanbanBoardColumn"%>
<%@page import="com.metservice.kanban.model.HtmlColour"%>
<%@page import="com.metservice.kanban.model.WorkItemTypeCollection"%>
<%@page import="com.metservice.kanban.model.BoardIdentifier"%>
<%@page import="com.metservice.kanban.model.WorkItemType"%>
<%@page import="com.metservice.kanban.model.KanbanProject"%>

<style type="text/css">

body {
font-family: arial;
}

h1,h2,h3 {
font-family: arial;
}

p {
font-family: arial;
}

.textTypingArea {
margin: 10px 0px 40px 10px;
border:1px solid grey;
width:780px;
padding: 10px;
}


</style>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<jsp:include page="include/header-head.jsp"/>
	
	<title>Kanban</title>
<%
    String scrollTopParam = (String) request.getAttribute("scrollTop");
    int scrollTo = 0;
    if (scrollTopParam != null)
        scrollTo = Integer.parseInt(scrollTopParam);
%>
<script type="text/javascript">
	$(function() {
		$("button.dropdown").button({
            icons: {
                primary: "ui-icon-gear",
                secondary: "ui-icon-triangle-1-s"
            },
            text: false
        }).click(function(){
          $("div.dropdown-menu-wrapper").fadeOut(200);
          $(this).siblings("div.dropdown-menu-wrapper:hidden").fadeIn(200);
          return false;
        });
        $(":not(button.dropdown)").click(function(){
            $("div.dropdown-menu-wrapper").fadeOut(200);
        });

        
	});
	</script>
	
		<script type="text/javascript">
	<!--
		
		var editing = false;
		
		if (document.getElementById && document.createElement) {
			var submitButton = document.createElement('BUTTON');
			var submitText = document.createTextNode('Submit');
			submitButton.appendChild(submitText);
			submitButton.onclick = saveEdit;
		}
		
		function catchIt(e) {
			if (editing) return;
			if (!document.getElementById || !document.createElement) return;
			if (!e) var obj = window.event.srcElement;
			else var obj = e.target;
			while (obj.nodeType != 1) {
				obj = obj.parentNode;
			}
			if (obj.tagName == 'TEXTAREA' || obj.tagName == 'A') return;
			while (obj.className != 'textTypingArea' && obj.nodeName != 'HTML') {
				obj = obj.parentNode;
			}
			if (obj.nodeName == 'HTML') return;
			var x = obj.innerHTML;
			var y = document.createElement('TEXTAREA');
			y.appendChild(document.createTextNode(x));
			var z = obj.parentNode;
			z.insertBefore(y,obj);
			z.insertBefore(submitButton,obj);
			z.removeChild(obj);
			y.value = x;
			y.focus();
			y.style.height = 500 + "px";
			y.style.width = 800 + "px";
			y.style.marginTop = 10 + "px";
			y.style.marginLeft = 10 + "px";
			editing = true;
			return false;
		}
		
		function saveEdit() {
			var area = document.getElementsByTagName('TEXTAREA')[0];
			$.ajax({
				type: "POST",
				url: window.location.pathname + "/../edit-journal-action",
				data: "journalText=" + $(area).val(),
			});
			var y = document.createElement('div');
			y.className = "textTypingArea";
			var z = area.parentNode;
			y.innerHTML = area.value;
			z.insertBefore(y,area);
			z.removeChild(area);
			z.removeChild(document.getElementsByTagName('button')[0]);
			editing = false;
			return false;
		}


	document.onclick = catchIt;

	// -->
	</script>

<script type="text/javascript">

$(function() {

    $("#journal-add-dialog").dialog({

        modal: true,
        autoOpen: false,
        buttons: {
            Ok: function() {
                $.get("add-journal-entry", 
                        { 
                            userName: $("#userField").val(), 
                            text: $("#journal-text").val(),
                            date: $("#journal-date").val()
                        }).success(function() { window.location = getBoard(); });
                $(this).dialog("close");
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        }
    }); 
    $("#add-entry-button").button();
    $("#journal-date").datepicker({
    	dateFormat: "yy-mm-dd",
		showOn: "button",
		buttonImage: "../../images/calendar.gif",
		buttonImageOnly: true
	});
    $("#add-entry-button").click(function() {
    	addJournalEntry();
    });
});

function addJournalEntry() {
	$("#journal-text").val("");
	$("#journal-date").val("");
	$("#journal-add-dialog").dialog("open");
}


</script>

</head>
<body onload="javscript:setPosition();">
    <jsp:include page="include/header.jsp"/>

    <div>
        <a href="#" id="add-entry-button">Add entry</a>    
    </div>
    <c:forEach items="${kanbanJournal}" var="item">
        <div class="journal-entry">
            <div class="ui-widget-header" style="position: relative;">
                <span>${item.userName} wrote on ${item.dateStr}</span>
                <a href="remove-journal-entry?id=${item.id}" style="top: 2px; right: 5px; position: absolute;">
                    <span class="ui-icon ui-icon-closethick">close</span>
                </a>
                
            </div>
            <div class="ui-widget-content">${item.text}</div>
        </div>
    </c:forEach>

<%--     <div class=textTypingArea>${kanbanJournal}</div> --%>


<div id="journal-add-dialog" title="Add journal item">

    <p>Entry date</p><input type="text" id="journal-date" />
    <p>Journal text:</p>
    
    <textarea name="journal-text" id="journal-text" rows="5" cols="30"></textarea>
</div>

</body>
</html>
