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
    						columnName: $("#edit-column-dialog-name-original").val(),
    						newColumnName: $("#edit-column-dialog-name").val(),
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
	$("#edit-column-dialog-name-original").val(name);
	$("#edit-column-dialog-item-type").val(type);
	if (wipLimit > 0) {
		$("#edit-column-dialog-wipLimit").val(wipLimit);
	}
	else {
		$("#edit-column-dialog-wipLimit").val("");
	}
	$("#edit-column-dialog").dialog("open");
} 


//Changes the card color to FIREBRICK!
function blockStory(id, type) {
	//document.forms["form"].action = getBoard() + "/advance-item-action?id=" + id + "&scrollTop=" + getYOffset();
	 	//document.forms["form"].submit();
	var item = document.getElementById("work-item-" + id);
	//if (item.className=='stopped') {
		//item.className = type;
	//}
	//else { item.className = "stopped"; }
	
	//$('#itemId').val(id)
	
	$("#block-dialog-item-id").html(id);
	
	$("#block-dialog").dialog("open");
}

function unblockStory(id, type) {
	var item = document.getElementById("work-item-" + id);
	$.get(getBoard() + "/block-item-action", 
			{ 
				itemId: id,
				userName: $('#userField').val()
			})
			.success(function() { window.location = getBoard(); })
			.error(function() { window.alert("error"); }
	);
	
}

function markUnmarkToPrint(divId, type, isStopped){
   var item = document.getElementById(divId);
   if (item == null) {
	   return;
   }
  if (item.className == 'markedToPrint') {
  	if (isStopped) {
  		item.className = "blocked";
  	}
  	else {
  		item.className = type;
  	}
  } else {
     item.className = "markedToPrint";
   }
}

function edit(id){
 document.forms["form"].action = getBoard() + "/edit-item?id=" + id;
 document.forms["form"].submit();
 
}

function addChild(id){
  document.forms["form"].action = getBoard() + "/add-item?id=" + id;
  document.forms["form"].submit();
}
function move(id, targetId, after){
  document.forms["form"].action = getBoard() + "/move-item-action?id=" + id + "&targetId=" + targetId + "&scrollTop=" + getYOffset() + "&after=" + after;
  document.forms["form"].submit();
}

$(function() {
	$("button.dropdown").button({
        icons: {
            primary: "ui-icon-gear dropdown-gear",
            secondary: "ui-icon-triangle-1-s dropdown-triangle"
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

    
    //Table header stuff
    var header = $("#kanbantable thead");
    $("body").append('<table class="kanban" id="headercopy"><thead></thead></table>');
  $("#headercopy thead").append($("#kanbantable thead th").clone());
     
    var header_pos = header.offset().top+header.height();
    $(window).scroll(function () { 
      if($(window).scrollTop() >= header_pos){
        $("#headercopy").fadeIn();
      }else{
        $("#headercopy").fadeOut();
      }
    });
});