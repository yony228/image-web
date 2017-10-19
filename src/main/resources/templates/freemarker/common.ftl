<style>
    body{
        font-weight: 500;
        font-size: 1.7em;
        font-family: "Segoe UI", "Lucida Grande", Helvetica, Arial, "Microsoft YaHei", FreeSans, Arimo, "Droid Sans", "wenquanyi micro hei", "Hiragino Sans GB", "Hiragino Sans GB W3", "FontAwesome", sans-serif;
    }
</style>
<!-- 背景图 -->
<div style="position: :absolute;width: 100%;height: 100%;z-index: :-1">
    <img id="backImg" src="../../images/back04.jpg" style="position: fixed;-webkit-filter: blur(6px);opacity: 0.5" width="100%" height="100%">
</div>
<nav class="navbar" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#NAV_COLLAPSE" style="background-color: rgba(0, 0, 0, .25);margin-top: 18px;">
                <span class="sr-only">切换导航</span>
                <span class="icon-bar" style="background-color: #333;"></span>
                <span class="icon-bar" style="background-color: #333;"></span>
                <span class="icon-bar" style="background-color: #333;"></span>
            </button>
        <#if Session["userInfo"]??>
            <ul class="nav navbar-nav" style="width: 120px;cursor: pointer;" title="进入首页">
                <li><img src="../../images/logoko.png" style="width: 100px;" onclick="location.href='${rc.contextPath}/fileSearch/init'"></li>
            </ul>
        </#if>
        </div>
        <div class="collapse navbar-collapse" id="NAV_COLLAPSE">
        <#if Session["userInfo"]??>
            <ul class="nav navbar-nav">
                <li><a id="fileSearch" href="${rc.contextPath}/fileSearch/init" style="color:#333;">图片搜索</a></li>
                <#if Session["userInfo"].user.trainer==false>
                    <li><a id="fileUpload" href="${rc.contextPath}/file/upload/init" style="color:#333;">图片上传</a></li>
                </#if>
                <li><a id="filePicManager" href="${rc.contextPath}/file/file/fileList" style="color:#333;">图片管理</a></li>
                <#if Session["userInfo"].user.trainer>
                    <li><a id="classManager" href="${rc.contextPath}/classification/classifications/classList" style="color:#333;">分类管理</a></li>
                <#else>
                    <li><a id="classManager" href="${rc.contextPath}/tag/tags/tagList" style="color:#333;">标签管理</a></li>
                </#if>
                <#--<#if Session["userInfo"].user.trainer>-->
                    <#--<li class="dropdown">-->
                        <#--<a id="trainClass" class="dropdown-toggle" data-toggle="dropdown" style="color:#333">模型训练<b class="caret"></b></a>-->
                        <#--<ul class="dropdown-menu">-->
                            <#--<li><a href="${rc.contextPath}/train/train/trainClass" style="color:#333;">模型训练</a></li>-->
                            <#--<li><a href="${rc.contextPath}/train/train/trainList" style="color:#333;">训练管理</a></li>-->
                        <#--</ul>-->
                    <#--</li>-->
                <#--</#if>-->
                <#--<#if Session["userInfo"].user.trainer>-->
                    <#--<li><a id="trainClass" href="${rc.contextPath}/train/train/trainList" style="color:#333;">训练管理</a></li>-->
                <#--</#if>-->
                <#if Session["userInfo"].user.trainer>
                    <li><a id="modelsManager" href="${rc.contextPath}/models/models/modelList" style="color:#333;">模型管理</a></li>
                    <#if Session["userInfo"].user.name=='admin'><li><a id="userManager" href="${rc.contextPath}/user/user/userList" style="color:#333;">用户管理</a></#if></li>
                </#if>
            </ul>
        </#if>
            <ul class="nav navbar-nav navbar-right">
            <#if Session["userInfo"]??>
                <li><a href="#" onclick="showUpdatePasswordModel()"><span id="loginUserName" class="glyphicon glyphicon-user">${Session["userInfo"].user.name!}</span></a></li>
                <li><a href="${rc.contextPath}/common/access/logout" id="logout"><span class="glyphicon glyphicon-log-out">登出</span></a></li>
            <#else>
            <#--<li><a href="#"><span class="glyphicon glyphicon-user"></span> 注册</a></li>-->
                <li><a href="${rc.contextPath}/login"><span class="glyphicon glyphicon-log-in"></span> 登录</a></li>
            </#if>
            </ul>
        </div>
    </div>
</nav>

<!--个人中心，修改密码-->
<div class="modal fade" id="updatePassword" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 class="modal-title" id="myModalLabel">个人信息</h3>
            </div>
            <form id="updatePwdForm" class="form-horizontal">
                <div class="modal-body" style="font-size: 14px">
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">用户名:</label>
                        <label class="col-lg-2 control-label"><#if Session["userInfo"]??>${Session["userInfo"].user.name!}</#if></label>
                    </div>
                    <div id="pwdDiv" class="form-group row">
                        <label class="col-lg-2 control-label">密码:</label>
                        <label class="col-lg-2 control-label"><a onclick="showUpdatePwdDiv()">修改</a></label>
                    </div>
                    <div id="updatePwdDiv" style="display: none;">
                        <div class="form-group row">
                            <label class="col-lg-2 control-label">原密码:</label>
                            <div class="col-lg-6">
                                <input type="password" class="form-control" id="oldPwd" name="oldPwd">
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-lg-2 control-label">新密码:</label>
                            <div class="col-lg-6">
                                <input type="password" class="form-control" id="newPwd" name="newPwd">
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-lg-2 control-label">确认密码:</label>
                            <div class="col-lg-6">
                                <input type="password" class="form-control" id="newPwd1" name="newPwd1">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" onclick="submitUpdatePwd()" class="btn btn-sm btn-primary">确定</button>
                        <button type="button" onclick="" class="btn btn-sm" data-dismiss="modal">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    $('#updatePwdForm').bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        excluded:[':disabled'],
        fields: {
            oldPwd: {
                validators: {
                    notEmpty: {
                        message: '请输入原密码'
                    }
                }
            },
            newPwd: {
                validators: {
                    notEmpty: {
                        message: '请输入新密码'
                    },
                    stringLength: {
                        min: 6,
                        max: 12,
                        message: '密码长度在6-12之间'
                    }
                }
            },
            newPwd1: {
                validators: {
                    notEmpty: {
                        message: '请确认密码'
                    },
                    identical:{
                        field:'newPwd',
                        message:'两次输入的密码不一致！'
                    }
                }
            }
        }
    });

    function showUpdatePasswordModel() {
        $('#updatePwdForm').data('bootstrapValidator').resetForm(true);
        $("#oldPwd").val('');
        $("#newPwd").val('');
        $("#newPwd1").val('');

        $("#updatePwdDiv").hide();
        $("#pwdDiv").show();
        $("#updatePassword").modal("show");
    }

    function showUpdatePwdDiv() {
        $("#updatePwdDiv").show();
        $("#pwdDiv").hide();
    }

    function submitUpdatePwd() {
        //如果updatePwdDiv是隐藏状态，直接关闭窗口
        if($("#updatePwdDiv").is(":hidden")==true){
            $("#updatePassword").modal("hide");
            return;
        }

        //表单验证
        var data = $('#updatePwdForm').data('bootstrapValidator');
        if(data){
            data.validate();
            if(!data.isValid()){
                return;
            }
        }

        //提交
        $.ajax({
            type: 'post',
            url: "${rc.contextPath}/user/updatePwd",
            data: {
                oldPwd : $("#oldPwd").val(),
                newPwd : $("#newPwd").val()
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if(result.code==100){
                    swal("", "修改成功！", "success");
                    $("#updatePassword").modal("hide");
                }else{
                    swal("", result.msg, "error");
                }
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });
    }
</script>