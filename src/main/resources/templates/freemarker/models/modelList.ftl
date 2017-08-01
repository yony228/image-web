<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <link href="${rc.contextPath}/css/common/switch.button.css" rel="stylesheet" type="text/css">
    <title>模型管理</title>
    <style>
        .modal-body label{
            width: 100px;
            font-size: 14px;
        }

        #myfileForm input{
            border: 0px solid #ccc;
        }
    </style>
</head>
<body>
<#include "../common.ftl" />
<div class="container" style="position: relative;">
    <div style="background-color: #ccc;height: 50px;font-size: 14px;border-radius: 4px;">
        <div class="form-group">
            <div style="position: absolute;padding-top:10px;padding-left: 20px;z-index: 1000;">
                <button type="button" onclick="location.href='${rc.contextPath}/train/train/trainList'" class="btn btn-primary submit1">添加模型</button>
            </div>
            <form id="pageForm" style="position: relative;padding-top:10px;padding-left: 10px;" onsubmit="return submitQuery()">
                <div style="float: right;">
                    <input name="condition" type="text" id="condition" class="form-input"
                           style="float: right;width: 600px;margin-right: 100px;" placeholder="请输入模型名称或描述等模糊查询条件" value="${condition!}">
                </div>
                <div style="position: absolute;right: 0;">
                    <button type="button" onclick="submitQuery()"  class="btn btn-primary submit1" style="float: right;margin-right: 20px">查询</button>
                </div>
            </form>
        </div>
    </div>
    <br>
    <div class="form-group" style="font-size: 14px;background-color: white">
        <table id="modelsTable">  </table>
    </div>
</div>

<div class="modal fade" id="showEditModels" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">修改模型信息</h4>
            </div>
            <form style="font-size: 12px;" id="editClass" class="form-horizontal" role="form" method="post">
                <div class="modal-body">
                    <input type="text" class="form-input" id="modelsId" name="modelsId" value="" style="display: none">
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">模型名称：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="name" name="classAlias" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">模型描述：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="des" name="classDes" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">最低阀值：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="showRateBottom" name="showRateBottom" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">最高阀值：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="showRateTop" name="showRateTop" value="">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" onclick="checkEditForm()" class="btn btn-sm btn-primary">确定</button>
                        <button type="button" class="btn btn-sm" data-dismiss="modal">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
<script>
    $(function () {
        $("#modelsManager").css("color", "#d0cb16");

        $("#modelsTable").bootstrapTable({
            url:'${rc.contextPath}/models/queryModelList',
            method:'post',
            contentType:"application/x-www-form-urlencoded",
            dataType:"json",
            pagination:true,
            pageNumber:1,
            pageSize:10,
            striped:true,
            sidePagination:"server",
            pageList:[10],
            pageNumber:1,
            queryParamsType:'',
            queryParams:function queryParams(params){
                var param = {
                    pageNumber: params.pageNumber,
                    pageSize: params.pageSize,
                    condition: $("#condition").val()
                };
                return param;
            },
            columns:[{
                checkbox:true
            },{
                field:'id',
                title:'ID'
            },{
                field:'name',
                title:'模型名称'
            },{
                field:'des',
                title:'描述'
            },{
                field:'classCount',
                title:'分类数量',
                formatter: function (value, row, index) {
                    return "<a target='_blank' href='${rc.contextPath}/classification/classifications/classList?modelId=" + row.id + "'>" + value + "</a>";
                }
            },{
                field:'show_rate_bottom',
                title:'模型阀值',
                formatter: function (value, row, index) {
                    return row.show_rate_bottom + "~" + row.show_rate_top;
                }
            },{
                field:'status',
                title:'状态',
                formatter: function (value, row, index) {
//                    var str="-";
//                    if (value == '1') {
//                        str = "暂存";
//                    }else if(value == '2'){
//                        str = "上线";
//                    }
//                    return str;

                    var str="";
                    if(value == '1'){
                        str = "onchange=updateModelStatus('" + row.id + "','2')";
                    }else if(value == '2'){//上线状态，不能修改
                        str = "checked disabled";
                    }
                    return '<div class="toggle-button-wrapper">' +
                            '<input type="checkbox" id="toggle-button' + row.id + '" class="toggle-button" name="switch" '+ str +'>' +
                            '<label for="toggle-button' + row.id + '" class="button-label">' +
                            '<span class="circle"></span>' +
                            '<span class="text on">上线</span>' +
                            '<span class="text off">暂存</span>' +
                            '</label>' +
                            '</div>';
                }
            },{
                title:'操作',
                formatter:function (value, row, index) {
                    var str = '<a onclick=updateModels("' + row.id + '","' + row.name + '","' + row.des + '","' + row.show_rate_top + '","' + row.show_rate_bottom + '")>修改</a>';
//                    if (row.status == '1') {
//                        str += '&nbsp;&nbsp;&nbsp;<a onclick=updateModelStatus("' + row.id + '","2")>上线</a>';
//                    }
//                    else if(row.status == '2'){
//                        str += '&nbsp;&nbsp;&nbsp;<a onclick=updateModelStatus("' + row.id + '","1")>下线</a>';
//                    }
                    return str;
                }
            }]
        });

        //表单验证
        $('#editClass').bootstrapValidator({
            message: 'This value is not valid',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            excluded:[':disabled'],
            fields: {
                classAlias: {
                    validators: {
                        notEmpty: {
                            message: '请输入模型名称'
                        }
                    }
                },
                showRateTop: {
                    validators: {
                        notEmpty: {
                            message: '请输入最大阀值'
                        }
                    }
                },
                showRateBottom: {
                    validators: {
                        notEmpty: {
                            message: '请输入最低阀值称'
                        }
                    }
                }
            }
        });
    });

    function updateModelStatus(id, status) {
        swal({
            title: "",
            text: "是否确认上线此模型？",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消"
        }, function (isConfirm) {
            if (isConfirm) {
                var url = "${rc.contextPath}/models/updateModelStatus";

                $.ajax({
                    type: 'post',
                    url: url,
                    data: {
                        'status': status,
                        'modelsId': id
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        if (result.code == 100) {
                            $("#modelsTable").bootstrapTable('refreshOptions', {pageNumber: 1});
                        } else {
                            alert(result.msg);
                            $("#modelsTable").bootstrapTable('refreshOptions', {pageNumber: 1});
                        }
                    },
                    error: function () {
                        alert("系统错误，请联系管理员！");
                        $("#modelsTable").bootstrapTable('refreshOptions', {pageNumber: 1});
                    }
                });
            }else{
                $("#modelsTable").bootstrapTable('refreshOptions', {pageNumber: 1});
            }
        });
    }

    function submitQuery(){
        $("#modelsTable").bootstrapTable('refreshOptions',{pageNumber:1});
        return false;
    }

    function updateModels(id, name, des,showRateTop,showRateBottom) {
        $('#editClass').data('bootstrapValidator').resetForm(true);
//        $('#editClass').bootstrapValidator('resetForm',true);

        $("#modelsId").val(id);
        $("#name").val(name);
        $("#des").val(des);
        $("#showRateTop").val(showRateTop);
        $("#showRateBottom").val(showRateBottom);

        $("#showEditModels").modal("show");
    }

    function checkEditForm() {
        var name=$("#name").val();
        var des = $("#des").val();
        var modelsId = $("#modelsId").val();
        var showRateTop = $("#showRateTop").val();
        var showRateBottom = $("#showRateBottom").val();

        //表单验证
        var data = $('#editClass').data('bootstrapValidator');
        if(data){
            data.validate();
            if(!data.isValid()){
                return;
            }
        }

        var url  = "${rc.contextPath}/models/updateModels";

        $.ajax({
            type: 'post',
            url: url,
            data: {
                'name':name,
                'des': des,
                'modelsId': modelsId,
                'showRateTop':showRateTop,
                'showRateBottom':showRateBottom
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if(result.code==100){
                    $("#modelsTable").bootstrapTable('refreshOptions',{pageNumber:1});
                    $("#showEditModels").modal("hide");
                }else{
                    swal({   title: "失败",   text: result.msg,   timer: 2000 });
                }
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });
    }
</script>
</html>