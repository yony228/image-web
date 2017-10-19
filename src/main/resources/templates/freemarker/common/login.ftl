<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <title>登录</title>
    <style>
        .error{float:right;color:red;margin-right:10px;width: 100%;text-align: right;font-size: 14px;}

        .input-group{
            margin:10px 0px;
        }

        h3{
            padding:5px;
            border-bottom: 1px solid #ddd;
        }
    </style>
</head>
<body>
<div style="position: :absolute;width: 100%;height: 100%;z-index: :-1">
    <img src="../../images/back04.jpg" style="position: fixed;" width="100%" height="100%">
</div>
<div class="navbar-collapse" id="NAV_COLLAPSE">
    <ul class="nav navbar-nav" style="width: 120px;cursor: pointer;" title="进入首页">
        <li><img src="../../images/logoko.png" style="width: 100px;" onclick="location.href='${rc.contextPath}/fileSearch/init'"></li>
    </ul>
</div>
<div class="container" style="position: relative;">
    <form onsubmit="return login()">
        <div class="container well" style="width: 33%">
            <h3 style="margin-top: 50px">登录搜图系统</h3>
            <div class="input-group input-group-md">
                <span class="input-group-addon" id="sizing-addon1"><i class="glyphicon glyphicon-user" aria-hidden="true"></i></span>
                <input type="text" name="username" id="username" class="form-control" placeholder="用户名" onblur='checkName()' aria-describedby="sizing-addon1">
            </div>
            <span id="user" class="error" > </span>
            <div class="input-group input-group-md">
                <span class="input-group-addon" id="sizing-addon1"><i class="glyphicon glyphicon-lock"></i></span>
                <input type="password" name="password" id="password" class="form-control" placeholder="密码" onblur='checkPassword()' aria-describedby="sizing-addon1">
            </div>
            <span id="psword" class="error"> </span>
            <button type="submit" onclick="login()"  class="btn btn-success btn-block" style="background-color: #337ab7;">登录</button>
            <p class="text-success" style="text-align: right;padding: 20px;">
                <a onclick="forgetPassword()">>>忘记密码？</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a href="${rc.contextPath}/register">>>还没账号？去注册</a>
            </p>
        </div>
    </form>
</div>

</body>
<script>
    var checkName=function() {
        var name = eval(document.getElementById('username')).value;
        if (name.length > 20 || name.length < 1){
            $("#user").html("用户名长度在1-20之间！");
            $("#user").show();
        }else {
            $("#user").html("");
            $("#user").hide();
        }
    }
    var checkPassword = function(){
        var password = eval(document.getElementById('password')).value;
        if (password.length > 12 || password.length < 6){
            $("#psword").html("密码长度在6-12之间！");
            $("#psword").show();
        }else {
            $("#psword").html("");
            $("#psword").hide();
        }
    }

    function  forgetPassword() {
        swal("", "请联系管理员,重置密码！", "warning");
    }

    function login() {
        if($("#username").val()=='' || $("#password").val()==''){
            return false;
        }

        $.ajax({
            type: 'post',
            url: '${rc.contextPath}/access/login',
            data: {
                'username': $("#username").val(),
                'password': $("#password").val()
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if(result.code==100){
                    location.href = "${rc.contextPath}/fileSearch/init";
                }else{
                    swal({   title: "登录失败",   text: result.msg,   timer: 2000 });
                }
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });
        return false;
    }
</script>
</html>