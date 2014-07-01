//创建XMLHTTP对象
var xmlHttp;
var e;
function GetXmlHttpObject() {
	var xmlHttp = null;
	try {
		// Firefox, Opera 8.0+, Safari
		xmlHttp = new XMLHttpRequest();
	} catch (e) {
		// Internet Explorer
		try {
			xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
	}
	return xmlHttp;
}

// 调用远程方法
function callServer(e) {
	try {
		xmlHttp = GetXmlHttpObject();
		if (xmlHttp != null) {
			// 构造查询连接字符串
			var url = "/tms/NotificationServlet";
			// 打开连接
			xmlHttp.open("GET", url, true);
			// 设置回调函数
			xmlHttp.onreadystatechange = updatePage;
			// 发送请求
			xmlHttp.send(null);
		} else {
			alert("XMLHTTP对象创建失败");
		}
	} catch (e) {
		alert("查询错误:" + e);
	}
}
// 回调处理函数
function updatePage() {
	try {
		if (xmlHttp.readyState == 4) {
			bottomBar.hide();
			document.getElementById("notificationBarId").innerHTML = "";
			try {
				var response = xmlHttp.responseText;
				document.getElementById("notificationBarId").innerHTML = response;
				bottomBar.show();
				setTimeout(function() { bottomBar.hide(); }, 1000 * 60);
			} catch (ee) {
				bottomBar.hide();
			}
		}
	} catch (e) {
		alert("回调处理错误:" + e);
	}
}
window.setInterval(callServer, 1000 * 60 * 20);