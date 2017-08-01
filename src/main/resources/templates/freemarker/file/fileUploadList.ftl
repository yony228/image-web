<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
    <#include "../head.ftl" />
    <title>文件上传</title>
    <style>
        img{
            width: 100%;
        }
        .img-box:hover .img-label{
            transform: translateY(-46px);

        }
        .img-tag-div{
            float: left;
            height: 30px;
            background-color: #999;
            color: #333;
            font-size: 22px;
            text-align: left;
            border-radius: 4px;
        }
    </style>
</head>
<#--class="change-gruond"-->
<body>
<#include "../common.ftl" />
<table class="container" style="transform: translateY(0px);">
<#list returnFileList as map>
    <tr style="height: 250px;border-bottom: 1px solid #9d9d9d;">
        <td style="width: 300px;">
            <img src="${picFullUrl}${map['fileUrl']}_250x250" style="width:250px;" onerror="this.src='../../images/invalid.jpeg'">
        </td>
        <td>
            <div style="height: 80px;" id="imgTagDiv_${map['imagesId']}">
            <#list map['keyName'] as keyName>
                <div class="img-tag-div" onclick="delImagesTag(this,'${map['imagesId']}','${keyName}')">&nbsp;${keyName}&nbsp;&nbsp;<span">×</span></div>
                <div style="float: left">&nbsp;&nbsp;</div>
            </#list>
            </div>
            <div style="height: 30px;">
                <input type="hidden" value="${map['imagesId']}">
                <#--style="transform: translateY(10px) translateX(50px)"-->
                <#--rgba(0, 0, 0, .075)-->
                <input type="text" id="class_${map['imagesId']}" class="form-control"
                       style="width: 100px;background-color:white;transform: translateY(20px);"
                       placeholder="添加标签"
                       onfocus="showInput(${map['imagesId']})"
                       onblur="hideInput(${map['imagesId']})"
                       onkeydown="editImagesTag(event,${map['imagesId']})"
                >
                <#--<input type="button" value="添加" class="btn btn-primary submit1" style="transform:translateY(-15px) translateX(110px);"  onclick="editImagesTag(${map['imagesId']})">-->
            </div>
        </td>
    </tr>
</#list>
</table>
</body>
<script>
    $(function(){
        $("#fileUpload").css("color","#d0cb16");
    });

    function showInput(imgId) {
        $("#class_" + imgId).css("background-color","#eee");
    }

    function hideInput(imgId) {
        $("#class_" + imgId).css("background-color","rgba(0, 0, 0, .075)");
    }

    function editImagesTag(event,imgId) {
        if(event.keyCode==13){
            var classText = $("#class_" + imgId).val().trim();
            if(classText==''){
                swal("请输入标签！", "", "warning");
                return
            }

            $.ajax({
                type: 'post',
                url: "${rc.contextPath}/file/fileUpload/editClass",
                data: {
                    'imgId': imgId,
                    'classText': classText,
                    'flag' : 'add'
                },
                dataType: 'json',
                async: false,
                success: function (result) {
                    if(result.code==100){
                        //alert("修改成功!");
                        //$("#class_" + imgId).hide();
                        $("#class_" + imgId).val('');
                        $("#class_" + imgId).css("background-color","rgba(0, 0, 0, .075)");
                        var str = "<div class='img-tag-div' style='background-color: #ccc' onclick=delImagesTag(this,'" + imgId + "','" + classText + "')>&nbsp;" + classText + "&nbsp;&nbsp;<span>×</span></div> <div style='float: left'>&nbsp;&nbsp;</div>";
                        $("#imgTagDiv_" + imgId).append(str);
                    }else{
                        swal("修改失败!", "", "error");
                    }
                },
                error: function () {
                    swal("系统错误，请联系管理员！", "", "error");
                }
            });
        }
    }

    function delImagesTag(obj, imgId, classText) {
        swal({
            title: "",
            text: "是否确认删除图片该标签？",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消"
        }, function (isConfirm) {
            if (isConfirm) {
                $.ajax({
                    type: 'post',
                    url: "${rc.contextPath}/file/fileUpload/editClass",
                    data: {
                        'imgId': imgId,
                        'classText': classText,
                        'flag': 'del'
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        if (result.code == 100) {
                            //alert("修改成功!");
                            obj.remove();
                        } else {
                            swal("修改失败!", "", "error");
                        }
                    },
                    error: function () {
                        swal("系统错误，请联系管理员！", "", "error");
                    }
                });
            }
        });
    }
</script>
</html>