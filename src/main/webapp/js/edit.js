$(document).ready(function() {
	$(".presetColor").click(function() {
		var col = rgbToHex($(this).css("background-color"));
		$('#colorSelector').ColorPickerSetColor(col);
		$('#colorSelector div').css("background-color", "#" + col);
	});
});


$(function() {
	$('#workStreams2').tagit({triggerKeys:['enter','comma']});
});

function deleteThisWorkItem() {
	var response = confirm("Permanently delete this work item?");
	if (response == true) {
		document.forms["delete"].submit();
	}
}