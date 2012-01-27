function applyDatepicker(selector) {
	$(selector).datepicker({
    	dateFormat: "yy-mm-dd",
		showOn: "button",
		buttonImage: "../../images/calendar.gif",
		autoSize: true,
		buttonImageOnly: true
	});	
}