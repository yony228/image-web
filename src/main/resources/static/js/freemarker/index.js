alertify.defaults.transition = 'slide';
alertify.defaults.glossary.ok = "确定";
alertify.defaults.glossary.cancel = "取消";
alertify.defaults.notifier.delay = 2;
alertify.defaults.glossary.title = "";
alertify.defaults.theme.ok = "btn btn-primary";
alertify.defaults.theme.cancel = "btn btn-danger";
{
	var $location = (window.location + '').split('/');
	var $basePath = $location[3];
}
function showLogo() {
	$("#showLogo").modal("show");
}

function refreshMainFrame(path, parentId, forceFlag) {
	var basePath = '/' + $basePath;
	var absPath = path.split("?")[0];
	if (absPath.substr(0, basePath.length) == basePath) {
		absPath = absPath.substr(basePath.length + 1);
	}


	var parentId = parentId ? parentId : "NAV_COLLAPSE";
	if (forceFlag) {
		$("#frame_main").attr("src", path);
	} else {
		var desElement = $("#" + parentId + " a[onclick*='" + absPath + "']");
		if (desElement != null && desElement.size() == 1) {
			$("li").attr("class", "");
			desElement.parent().attr("class", "active");
			$("#frame_main").attr("src", path);
		}
	}
}

$("#btn_logout").click(function () {
	$.ajax({
		type: "POST",
		url: "freemarker/common/logout.do",
		dataType: "json",
		success: function (data) {
			if (data.success === "true") {
				var message = "退出成功";
				alertify.success(message);
				$("#login-warning").text(message);
				$("#username").text("");
				$("#login").show();
				$(".usericon,#count,#hostconf,#elasticsearch").hide();
				parent.refreshMainFrame("log/history/init");
			} else {
				var warningMessage = "请填入正确的用户名和密码并与组名相匹配!";
				alert("退出失败");
				$("#login-warning").text(warningMessage);
			}
		}

	});
});
/* debug iframe 高度*/
var cookies = document.cookie.split("=")[1];
$(function () {
    // /*判断是否有cookie值显示登录*/
	// if (cookies == undefined || cookies == null) {
	// 	$("#login").show();
	// } else {
	// 	$("#login").hide();
	// 	$(".usericon,#elasticsearch").show();
	// 	$("#username").text(cookies);
	// }
    /*显示登录账户信息*/
	$("#mycount").hover(
		function () {
			$(this).find("ul").show();
		},
		function () {
			$(this).find("ul").hide();
		}
	);

	$("#login").click(function () {
		refreshMainFrame('freemarker/common/access/login/init', null, true);
	});
    /*设置iframe高度*/
	var $height_body = $(window).height();
	var $embed_heigth = $height_body - 50;
	$("#fater_body").css("height", $height_body);
	$(".embed-responsive").css("height", $embed_heigth);
    /*浏览器缩放时高度设置*/
	$(window).resize(function () {
		var $height_body = $(window).height();
		var $embed_heigth = $height_body - 50;
		$("#fater_body").css("height", $height_body);
		$(".embed-responsive").css("height", $embed_heigth);
	})
});