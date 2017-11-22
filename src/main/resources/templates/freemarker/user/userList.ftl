<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <title>用户管理</title>
    <style>
    </style>
</head>
<body>
<#include "../common.ftl" />
<div class="container" style="position: relative;">
    <div style="background-color: #ccc;height: 50px;font-size: 14px;border-radius: 4px;">
        <div class="form-group">
            <form id="pageForm" style="position: relative;padding-top:10px;padding-left: 10px;" onsubmit="return submitQuery()">
                <div style="float: right;">
                    <input name="condition" type="text" id="condition" class="form-input" style="float: right;width: 600px;margin-right: 100px;" placeholder="请输入用户等条件查询" value="${condition!}">
                </div>
                <div style="position: absolute;right: 0;">
                    <button type="button" onclick="submitQuery()" class="btn btn-primary submit1" style="float: right;margin-right: 20px">查询
                    </button>
                </div>
            </form>
        </div>
    </div>
    <br>
    <div class="form-group" style="font-size: 14px;background-color: white">
        <table id="userTable"></table>
    </div>
</div>
</body>
<script>
    $(function () {
        $("#userManager").css("color", "#d0cb16");

        $("#userTable").bootstrapTable({
            url: '${rc.contextPath}/user/queryUserList',
            method: 'post',
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            pagination: true,
            pageNumber: 1,
            pageSize: 10,
            striped: true,
            sidePagination: "server",
            pageList: [10],
            pageNumber: 1,
            queryParamsType: '',
            queryParams: function queryParams(params) {
                var param = {
                    pageNumber: params.pageNumber,
                    pageSize: params.pageSize,
                    condition: $("#condition").val()
                };
                return param;
            },
            columns: [{
                field: 'name',
                title: '用户名'
            }, {
                field: 'role_name',
                title: '所属角色'
            }, {
                field: 'desc',
                title: '备注'
            }, {
                field: 'status',
                title: '状态',
                formatter: function (value, row, index) {
                    var str = "-";
                    if (value == '0') {
                        str = "正常";
                    } else if (value == '1') {
                        str = "禁用";
                    }
                    return str;
                }
            }, {
                title: '操作',
                formatter: function (value, row, index) {
                    var str = '<a onclick=resetPwd("' + row.id + '")>重置密码</a>';
                    if (row.status == '0') {
                        str += '&nbsp;&nbsp;&nbsp;<a onclick=updateStatus("' + row.id + '","1")>禁用</a>';
                    }else if (row.status == '1') {
                        str += '&nbsp;&nbsp;&nbsp;<a onclick=updateStatus("' + row.id + '","0")>启用</a>';
                    }
                    return str;
                }
            }]
        });
    });

    function submitQuery() {
        $("#userTable").bootstrapTable('refreshOptions',{pageNumber:1});
        return false;
    }

    function resetPwd(userId) {
        swal({
            title: "",
            text: "是否确认重置密码？",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消",
            closeOnConfirm: false
        }, function (isConfirm) {
            if (isConfirm) {
                $.ajax({
                    type: 'post',
                    url: "${rc.contextPath}/user/resetPwd",
                    data: {
                        'userId':userId
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        if(result.code==100){
                            swal("成功!", "重置密码成功!", "success");
                        }else{
                            swal("失败!", result.msg, "error");
                        }
                    },
                    error: function () {
                        swal("", "系统错误，请联系管理员！", "error");
                    }
                });
            }
        });
    }

    function updateStatus(userId,status){
        swal({
            title: "",
            text: "是否确认？",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消",
            closeOnConfirm: true
        }, function (isConfirm) {
            if (isConfirm) {
                $.ajax({
                    type: 'post',
                    url: "${rc.contextPath}/user/updateStatus",
                    data: {
                        'userId':userId,
                        'status':status
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        if(result.code==100){
                            $("#userTable").bootstrapTable('refreshOptions',{pageNumber:1});
                        }else{
                            swal("失败!", result.msg, "error");
                        }
                    },
                    error: function () {
                        swal("", "系统错误，请联系管理员！", "error");
                    }
                });
            }
        });
    }
</script>
</html>