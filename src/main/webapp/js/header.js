function getContext() {
    var pathArray = location.pathname.split('/'); 
    return "/" + pathArray[1];
}

function getRoot() {
    var pathArray = location.pathname.split('/'); 
    return "/" + pathArray[1] + "/" + pathArray[2];
}

function getProjectUrl() {
    var pathArray = location.pathname.split('/'); 
    return "/" + pathArray[1] + "/" + pathArray[2] + "/" + pathArray[3];
}

function getBoard() {
    var board = location.pathname.split('/')[4];
    var pos = board.indexOf(":");
    if (pos > -1) {
        board = board.substring(0,pos);
    }
    return board;
} 

function addTopLevel(id){
    document.forms["header"].action = getProjectUrl() + "/" + getBoard() + "/add-item?id=" + id;
    document.forms["header"].submit();
}

function changeProject(selectId){
	var select = document.getElementById(selectId);
    document.forms["header"].action = getProjectUrl() + "/" + getBoard() + "/open-project?newProjectName=" + select.options[select.selectedIndex].text;
    document.forms["header"].submit();
}


function board(type) {
    document.forms["header"].action = getProjectUrl() + "/" + type;
    document.forms["header"].submit();
}

function admin() {
	document.forms["header"].action = getProjectUrl() + "/admin/";
	document.forms["header"].submit();
}

function chart(chartName, workItemTypeName) {
    document.forms["header"].action = getProjectUrl() + "/" + getBoard() + "/chart?chartName=" + chartName + "&workItemTypeName=" + workItemTypeName + "&startDate=&endDate=";
    document.forms["header"].submit();
}

function changeSettings(createNewProject) {
    document.forms["header"].action = getProjectUrl() + "/" + getBoard() + "/edit-project?createNewProject=" + createNewProject;
    document.forms["header"].submit();
}

function download(currentProjectName, workItemTypeName) {
    document.forms["header"].action = getRoot() + "/download/" + currentProjectName + "?workItemTypeName=" + workItemTypeName;
    document.forms["header"].submit();
}

function getYOffset() {
    var pageY;
    if(typeof(window.pageYOffset)=='number') {
       pageY=window.pageYOffset;
    }
    else {
       pageY=document.documentElement.scrollTop;
    }
    return pageY;
}

function reorder(id, ids) {
	  var query="id=" + id;
	  for(var i=0 ; i < ids.length ; i++) {
		  query = query + "&ids=" + ids[i];
	  }
	  query = query + "&scrollTop=" + getYOffset();
	  document.forms["header"].action = getProjectUrl() + "/" + getBoard() + "/reorder?" + query;
	  document.forms["header"].submit();
}

function printCards(){
    var results = [];
    var query = "";
    var items = document.getElementsByTagName("*");
    for (var i = 0 ; i < items.length ; i++) {
        if (items[i].className == "markedToPrint") {
            // elementIds look like "work-item-3" we just want the "3" at the end
        
            var elementId = items[i].id;
            var workItemId = elementId.substring(elementId.lastIndexOf('-') + 1); 
            if (query == "") {
                query = query + "printSelection=" + workItemId;
            } else { 
                query = query + "&printSelection=" + workItemId;
            }
        }
    }
    if (query != "") {
        document.forms["header"].action = getBoard() + "/print-items?" + query;
        document.forms["header"].submit();
    }
}

function setFocus(id) {
	document.getElementById(id).focus();
}


function addColumn(){
  var name =  prompt("Column Name:");
  document.forms["header"].action = getProjectUrl() + "/" + getBoard() + "/add-column-action?name=" + name;
  document.forms["header"].submit();
}

function addWaitingColumn(){
	  var name =  prompt("Column Name:");
	  document.forms["header"].action = getProjectUrl() + "/" + getBoard() + "/add-waitingcolumn-action?name=" + name;
	  document.forms["header"].submit();
	}

function deleteColumn(){
	var name = prompt("Column Name:")
	document.forms["header"].action = getProjectUrl() + "/" + getBoard() + "/delete-column-action?name=" + name;
	document.forms["header"].submit();
	alert("Done");
}
