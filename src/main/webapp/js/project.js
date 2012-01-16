$(function() {
	$("#block-dialog").dialog({
		modal: true,
		autoOpen: false,
		buttons: {
			Ok: function() {
				$.get(getBoard() + "/block-item-action", 
						{ 
							itemId: $('#block-dialog-item-id').html(), 
							userName: $('#userField').val(), 
							comment: $("#block-comment").val() 
						}).success(function() { window.location = getBoard(); });
				
				$(this).dialog("close");
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		}
	}); 	
	$("#edit-column-dialog").dialog({
		modal: true,
		autoOpen: false,
		buttons: {
    		Ok: function() {
    			$.get(getProjectUrl() + "/edit-column-action", 
    					{ 
    						columnName: $("#edit-column-dialog-name").val(),
    						itemType: $("#edit-column-dialog-item-type").val(),
    						wipLimit: $("#edit-column-dialog-wipLimit").val()
    					})
    					.success(function() { window.location = getBoard(); })
    					.error(function() { window.alert("error"); });
    			$(this).dialog("close");
    		},
    		Cancel: function() {
    			$(this).dialog("close");
    		}
		}
	});
});


function editColumn(type, name, wipLimit) {
	$("#edit-column-dialog-name").val(name);
	$("#edit-column-dialog-item-type").val(type);
	if (wipLimit > 0) {
		$("#edit-column-dialog-wipLimit").val(wipLimit);
	}
	else {
		$("#edit-column-dialog-wipLimit").val("");
	}
	$("#edit-column-dialog").dialog("open");
} 