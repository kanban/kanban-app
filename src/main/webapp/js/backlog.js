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
	    
	    var parent = element.parent();
	    parent.empty();
	    parent.text(element.val());
	}
	
	function cancelEdit(element) {
		var originalValue = $(element).attr("originalValue");
		element.parent().html(originalValue);
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
    	var newInput = $("<input />")
    		    .attr("data-role", $(this).attr("data-role"))
    			.attr("originalValue", $(this).html())
    			.css("width", "50%").val($(this).text());
    	$(this).html("").append(newInput);
    	newInput.focus();
      
    	//Add the tooltip for name
    	if($(this).attr("data-role") == "name"){
    		$(this).append("<span style=\'color:#aaa\'>Press <b>Enter</b> to save, <b>Esc</b> to cancel</span>");
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
        		"workStreamsSelect": $("#workStreamPicker").val()
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