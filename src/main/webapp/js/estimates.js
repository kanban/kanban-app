$(function() {
	applyDatepicker('input[id^="date_"]');
	var addCosts = 0;
	$("#add_costs").button().click(function() {
		$("#cost_table").append('<tr><td><input type="text" id="date_new_' + addCosts + '" size="10" value=""/></td><td><input type="text" id="cost_new_' +addCosts+'" size="10" value=""/></td></tr>');
		applyDatepicker('input[id="date_new_' + addCosts +'"]');
		addCosts++;
	});
	
	function saveCosts() {
		// get all data to be saved
		var data = [];
		
		$('input[id^="date_"]').each(function(index) {
			var id = $(this).attr("id").match(/date_(.+)/)[1];
			var date_value = $(this).val();
			var cost_value = $("#cost_" + id).val();
			if (date_value != "" && cost_value != "") {
				data.push({
						date : date_value,
						cost : cost_value
				});
			}
		});
		
		$.ajax({
			dataType:    "json",
			contentType: "application/json",
			type:  "POST",
			url:  "estimates-cost-daily-save",
			data:  JSON.stringify(data)
		})
		.success(function() { $("#edit-cost-so-far-dialog").dialog("close"); window.location.reload(); })
		.error(function(e)  { window.alert("error " + e.responseText); });
	}
	
	$("#edit-cost-so-far-dialog").dialog({
        modal: true,
        autoOpen: false,
        buttons: {
            Ok: function() {
            	saveCosts();
            },
            Cancel: function() {
            	$("#edit-cost-so-far-dialog").dialog("close");
            }
        }
	});
	
	$("#edit-cost-so-far").button().click(function() {
		$("#edit-cost-so-far-dialog").dialog("open");
	});
});

function changeProjectProperty(promptName, submitName, oldValue) {
	var value = prompt("Change " + promptName + " to?", oldValue);
	if (value != null && value != "" && value != oldValue) {
		value = parseInt(value);
		
		if (value == NaN) {
			alert("Please enter a number")
		}
		else {
			document.getElementById("projectPropertyNameHiddenField").value = submitName;
			document.getElementById("projectPropertyValueHiddenField").value = value;
			return true;
		}
	}
	return false;
}
