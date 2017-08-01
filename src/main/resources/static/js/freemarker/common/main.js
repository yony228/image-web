$(document).ready(function () {

    $("#login").click(function () {
        parent.refreshMainFrame('freemarker/common/access/login/init', null, true);
    });

    $(".submit1").on("click", function () {

        $("form[name='my-form']").attr("action", "admin/login.do");
        $("form[name=my-form]").submit();
        var username = $("#username").val();
        var password = $("#password").val();
        $.ajax({
            type: "POST",
            url: "admin/login",
            data: {username: username, password: password},
            dataType: "json",
            success: function (data) {
                if (data.success === "true") {
                    var message = "登录成功";
                    console.log(message);
                    alert("登录登录成功！");
                    $("#login-warning").text(message);
                    window.location.href = "../../../outframe.html";
                } else {
                    var warningMessage = "请填入正确的用户名和密码并与组名相匹配!";
                    console.log(warningMessage);
                    alert("登录失败");
                    $("#login-warning").text(warningMessage);
                    window.location.href = "../../../error.html";
                }
            }
        });
    });

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
                        window.location.href = "../../../welcome.html";
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