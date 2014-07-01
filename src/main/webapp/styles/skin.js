/**
 * 版本样式切换
 */
$(document).ready(function(){
	var url = $("#requestContextPath").val();//获取根目录
	var skin = $("#SYS_SKIN").val();
	switch (skin) {
  	case "Deep Blue":
  		$("#top_up").css("background-image","url(" + url + "/images/main_03.gif)");
  		break;
  	case "Light Blue":
  		$("#top_up").css("background-image","url(" + url + "/images/main_top_2.gif)");
  		$("#top_up_right").hide();
  		break;
  	case "Blue Gray":
  		$("#top_up").css("background-image","url(" + url + "/images/main_top_3.gif)");
  		$("#top_up_right").hide();
  		break;
  	default:
  		$("#top_up").css("background-image","url(" + url + "/images/main_03.gif)");
  		break;
	}
});