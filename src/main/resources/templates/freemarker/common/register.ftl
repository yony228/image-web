<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <title>注册</title>
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
    <form>
        <div class="container well" style="width: 33%">
            <h3 style="margin-top: 50px">普通用户注册</h3>
            <div class="input-group input-group-md">
                <span class="input-group-addon" id="sizing-addon1"><i class="glyphicon glyphicon-user" aria-hidden="true"></i></span>
                <input type="text" name="username" id="username" class="form-control" placeholder="请输入用户名" onblur='checkName()' aria-describedby="sizing-addon1">
            </div>
            <span id="user" class="error" > </span>
            <div class="input-group input-group-md">
                <span class="input-group-addon" id="sizing-addon1"><i class="glyphicon glyphicon-lock"></i></span>
                <input type="password" name="password" id="password" class="form-control" placeholder="请输入密码" onblur='checkPassword()' aria-describedby="sizing-addon1">
            </div>
            <span id="psword" class="error"> </span>
            <div class="input-group input-group-md">
                <span class="input-group-addon" id="sizing-addon1"><i class="glyphicon glyphicon-lock"></i></span>
                <input type="password" name="password1" id="password1" class="form-control" placeholder="请再次输入密码" onblur='checkaa()' aria-describedby="sizing-addon1">
            </div>
            <span id="psword1" class="error"> </span>
            <button type="button" onclick="register()"  class="btn btn-success btn-block" style="background-color: #337ab7;">注册</button>
            <p class="text-success" style="text-align: right;padding: 20px;">
                <a href="${rc.contextPath}/login">>>已有账号，去登录</a>
            </p>
        </div>
    </form>
</div>

</body>
<script>
    function checkName() {
        var name = eval(document.getElementById('username')).value;
        if (name.length > 20 || name.length < 1){
            $("#user").html("用户名长度在1-20之间！");
            $("#user").show();
            return false;
        }else {
            $("#user").html("");
            $("#user").hide();
        }
        return true;
    }
    function checkPassword(){
        var password = eval(document.getElementById('password')).value;
        if (password.length > 12 || password.length < 6){
            $("#psword").html("密码长度在6-12之间！");
            $("#psword").show();
            return false;
        }else {
            $("#psword").html("");
            $("#psword").hide();
        }
        return true;
    }

    function checkaa(){
        if($("#password1").val()!=$("#password").val()){
            $("#psword1").html("两次输入的密码不一致！");
            $("#psword1").show();
            return false;
        }else {
            $("#psword1").html("");
            $("#psword1").hide();
        }
        return true;
    }

    function register() {
        if(checkName()==false || checkPassword()==false || checkaa()==false){
            return;
        }

        $.ajax({
            type: 'post',
            url: '${rc.contextPath}/access/registerUser',
            data: {
                'username': $("#username").val(),
                'password': $("#password").val()
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if(result.code==100){
                    swal({
                            title: "",
                            text: "注册成功，去登录?",
                            type: "success",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "确认",
                            cancelButtonText: "取消",
                            closeOnConfirm: false
                        }, function (isConfirm) {
                            if (isConfirm) {
                                location.href="${rc.contextPath}/login";
                            }
                        });
                }else{
                    swal({   title: "注册失败",   text: result.msg,   timer: 2000 });
                }
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });
    }
</script>
</html>