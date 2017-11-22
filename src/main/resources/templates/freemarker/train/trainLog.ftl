<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <title>训练日志</title>
    <style>
    </style>
</head>
<body style="background-color: #333;color: white">
<div class="container" style="margin-left: 10px;">
    <div id="log-container">
        <input id="train_no" type="hidden" value="${train_no!}">
        <input id="lastFileSize" type="hidden" value="">
        <div style="font-size: 14px;"></div>
    </div>
</div>
</body>
<script>
    $(document).ready(function () {
        init();
    });

    function init() {
        setInterval(function () {
            $.ajax({
                type: 'post',
                url: '${rc.contextPath}/train/queryTrainLog',
                data: {
                    'train_no': $("#train_no").val(),
                    'lastFileSize':$("#lastFileSize").val()
                },
                dataType: 'json',
                async: false,
                success: function (result) {
                    if(result.code==100){
                        $("#log-container div").append(result.logStr);
                        $("#lastFileSize").val(result.lastFileSize);

                        $(document).scrollTop($(document).height()-$(window).height());
                    }if(result.code==200){
                        $("#log-container div").html(result.msg);
                    }
                },
                error: function () {
                    //swal("", "系统错误，请联系管理员！", "error");
                    console.log("error");
                }
            });
        },2000);
    }
</script>
</html>