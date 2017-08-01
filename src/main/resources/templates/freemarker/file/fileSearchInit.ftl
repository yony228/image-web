<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <script type="text/javascript" src="${rc.contextPath}${urls.getForLookupPath('/js/freemarker/file/fileSearchList.js')}"></script>
    <title>文件搜索</title>
</head>
<body id="fater-body">
<#include "../common.ftl" />
<div class="container" style="margin-left: 140px;">
    <form id="searchForm" class="form-horizontal" role="form" action="${rc.contextPath}/fileSearch/pic/searchList"
          method="post" enctype="multipart/form-data" onkeydown="if(event.keyCode==13)return false;">
        <div class="row">
            <div class="col-md-1"
                 style="padding: 0px; transform: translateY(-20px);<#if Session["userInfo"]??>display: none;</#if> ">
                <img src="../../images/logoko.png" style="width: 100px;">
            </div>
            <!--请输入关键字:-->
            <div class="col-md-6">
                <input name="keyName" type="text" id="keyName" class="form-control" value="${keyName!}" placeholder="请选择图片进行搜索">
                <span class="upload"></span>
                <input style="display: none" name="file" type="file" id="file" class="form-control" accept=".jpg" onchange="checkForm()">
            </div>
            <div class="col-md-4" style="display: none">
                <button type="button" onclick="checkForm()" class="btn btn-primary submit1">搜索</button>
            </div>
        </div>
    </form>
</div>
<div id="uploading">正在搜索……</div>
<script>
    $(function () {
        $("#fileSearch").css("color", "#d0cb16");

        //清除样式
        $("#backImg").css("-webkit-filter",'');
        $("#backImg").css("opacity",'');
    });

    function checkForm() {
        if($("#keyName").val()=='' && $("#file").val() == ''){
            //swal("提示!", "请输入文字或上传图片", "warning");
            return;
        }else if ($("#file").val() == '') {
            $("#searchForm").attr("action","${rc.contextPath}/fileSearch/key/searchList");
        }else {
            $("#searchForm").attr("action","${rc.contextPath}/fileSearch/pic/searchList");
        }

        $("#uploading").show().delay(5000);
        $("#searchForm").submit();
    }

    //文件上传伸缩面板
    $(".upload").on("click", upload);
    function upload() {
        $("#file").trigger('click');
    }
</script>
</body>
</html>