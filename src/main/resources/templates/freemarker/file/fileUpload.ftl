<!DOCTYPE html>
<html lang="zh">
<head>
    <#include "../head.ftl" />
    <title>图片上传</title>
    <style>
    </style>
</head>
<body>
<#include "../common.ftl" />
<div class="container" style="margin-left: 140px;">
    <form id="myfileForm" class="form-horizontal" role="form" action="${rc.contextPath}/file/upload/uploadFile" method="post" enctype="multipart/form-data">
        <div class="row">
            <div class="col-md-6" style="padding: 0px">
                <input name="file" type="file" value="本地上传图片" id="fileUrl" class="form-control" accept=".jpg" multiple>
            </div>
            <div class="col-md-4">
                <button type="button" onclick="checkForm()" class="btn btn-primary submit1">上传</button>
            </div>
        </div>
    </form>
</div>
<div id="uploading" style="text-align: center">正在上传……</div>
</body>
<script>
    $(function(){
        $("#fileUpload").css("color","#d0cb16");

        //清除样式
        $("#backImg").css("-webkit-filter",'');
        $("#backImg").css("opacity",'');
    });

    function checkForm(){
        var fs = document.querySelector("#fileUrl").files;
        if(fs.length==0){
            swal("提示", "请选择要上传的文件！", "warning");
            return;
        }else if(fs.length>10){
            swal("提示", "上传文件个数超过10张，请重新选择！", "warning");
            return;
        }
        var flag = true;
        $.each(fs,function (i,file) {
//          if(!/.(bmp|jpg|png|BMP|JPG|PNG)$/.test(file.name)){
            if(!/.(jpg|JPG)$/.test(file.name)){
                swal("提示", "选择文件格式不正确，请重新选择！", "warning");
                flag = false;
                return;
            }
        });

        if(flag) {
            $("#uploading").show().delay(5000);
            $("#myfileForm").submit();
        }
    }
</script>
</html>