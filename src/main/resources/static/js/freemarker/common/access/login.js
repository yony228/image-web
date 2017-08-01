/**
 * Created by yony on 16-11-10.
 */
$(document).ready(function () {
	/*点击登录*/
	$(".submit1").on("click", userlogin);
	/*enter回车键登录*/
	$("#password").on("keydown", inputenter);
	function inputenter(event) {
		/*兼容firefox、ie*/
		var event = event || window.event;
		if (event.keyCode == "13") {
			userlogin();
		}
	}

	function userlogin() {
		var username = $("#username").val();
		var password = $("#password").val();
		var sourceURL = $("#hidden_sourceURL").val();
		$.ajax({
			type: "POST",
			url: "freemarker/common/access/login/login.do",
			data: {username: username, password: password, sourceURL: sourceURL},
			dataType: "json",
			success: function (data) {
				if (data.success === "true") {
					var message = "登录成功";
					var cookies = document.cookie.split("=")[1];
					var $wd = $(window.parent.document);
					$wd.find("#username").text(cookies);
					$wd.find("#login").hide();
					$wd.find(".usericon,#hostconf,#elasticsearch").show();
					$("#login-warning").text(message);
//                    window.location.href = "outframe.html";
				} else {
					var warningMessage = "请填入正确的用户名和密码并与组名相匹配!";
					console.log(warningMessage);
					$("#login-warning").text(warningMessage);
//                    window.location.href = "error.html";
				}
				parent.refreshMainFrame(sourceURL);
			},
			error: function (v1, v2, v3) {
				alert(v1 + v2 + v3);
				alert("构建失败");
			}
		});


	}

	$("#login-btn").click(function () {
		login();
	});

	function login() {
		var username = $("#username").val();
		var password = $("#password").val();

		if (username == "") {
			$("#login-warning").text("请输入用户名");
			$("#username").attr("data-content", "请输入用户名");
			$("#username").popover('toggle');
		} else if (password == "") {
			$("#login-warning").text("请输入用密码");
			$("#password").attr("data-content", "请输入用密码");
			$("#password").popover('toggle');
		} else {
			$.ajax({
				type: "POST",
				url: "admin/login",
				data: {username: username, password: password},
				dataType: "json",
				success: function (data) {
					if (data.success === "true") {
						var message = "登录成功";
						alert("登录成功了 了了了了！")
						console.log(message);
						$("#login-warning").text(message);
						window.location.href = "../../../../welcome.html";
					} else {
						var warningMessage = "请填入正确的用户名和密码并与组名相匹配!";
						console.log(warningMessage);
						$("#login-warning").text(warningMessage);
					}
				}
			});
		}
	}
});