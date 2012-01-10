$(function() {
    $("#journal-add-dialog").dialog({
        modal: true,
        autoOpen: false,
        buttons: {
            Ok: function() {
            	if ($("#journal-date").val() == "") {
            		$("#validation-error").html("Entry date cannot be empty");
            		$("#validation-error").show();
            		return;
            	}
            	if ($("#journal-text").val() == "") {
                	$("#validation-error").html("Journal text cannot be empty");
                	$("#validation-error").show();
                	return;
            	}
                $.get("add-journal-entry", 
                        { 
                            userName: $("#userField").val(), 
                            text: $("#journal-text").val(),
                            date: $("#journal-date").val()
                        })
                        .success(function() { 
                        	window.location = getBoard(); 
                        	$(this).dialog("close");
                        })
                        .error(function(event) { 
                        	$("#validation-error").html("Error response from the server");
                        	$("#validation-error").show();
                        });
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
	$("#validation-error").hide();
}
