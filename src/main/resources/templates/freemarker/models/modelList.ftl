<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <link href="${rc.contextPath}/css/common/switch.button.css" rel="stylesheet" type="text/css">
    <link href="${rc.contextPath}/css/jquery.steps.css" rel="stylesheet" type="text/css">
    <script src="${rc.contextPath}/js/jquery.steps.js" type="text/javascript"></script>

    <title>模型管理</title>
    <style>
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
                <button type="button" onclick="showAddTrain()" class="btn btn-primary submit1">添加模型</button>
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
            <form style="font-size: 12px;" id="editModel" class="form-horizontal" role="form" method="post">
                <div class="modal-body">
                    <input type="text" class="form-input" id="modelsId" name="modelsId" value="" style="display: none">
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">模型名称：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="name" name="modelName" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">模型描述：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="des" name="modelDes" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">最低阈值：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="showRateBottom" name="showRateBottom" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">最高阈值：</label>
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

<div class="modal fade" id="showModelDetail" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">查看详情</h4>
            </div>
            <form style="font-size: 12px;" id="editModel" class="form-horizontal" role="form" method="post">
                <div class="modal-body">
                    <input type="text" class="form-input" id="modelsId" name="modelsId" value="" style="display: none">
                    <div class="form-group row">
                        <label class="col-lg-4 control-label">模型名称：</label>
                        <label class="col-lg-4 control-label" style="text-align: left;" id="showModelName"></label>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-4 control-label">模型描述：</label>
                        <label class="col-lg-4 control-label" style="text-align: left;" id="showModelDes"></label>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-4 control-label">训练号：</label>
                        <label class="col-lg-4 control-label"  style="text-align: left;" id="showTrainNo"></label>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-4 control-label">训练集分片数量：</label>
                        <label class="col-lg-4 control-label" style="text-align: left;" id="showNumShard"></label>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-4 control-label">训练图片数量：</label>
                        <label class="col-lg-4 control-label" style="text-align: left;" id="showNumTrain"></label>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-4 control-label">训练步数：</label>
                        <label class="col-lg-4 control-label" style="text-align: left;" id="showTrainStepNum"></label>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-4 control-label">训练开始时间：</label>
                        <label class="col-lg-4 control-label" style="text-align: left;" id="showTrainTime"></label>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-4 control-label">训练状态：</label>
                        <label class="col-lg-4 control-label" style="text-align: left;" id="showTrainStatus"></label>
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="showModelClass" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">查看分类</h4>
            </div>
            <div class="modal-body" style="font-size: 14px;">
                <input id="queryClassModelId" type="hidden">
                <table id="showModelClassTable"></table>
            </div>
            <div class="modal-footer">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>
</div>

<#include "addModels.ftl" />
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
                    return "<a onclick=showModelClass('" + row.id + "')>" + value + "</a>";
                }
            },{
                field:'show_rate_bottom',
                title:'模型阈值',
                formatter: function (value, row, index) {
                    return row.show_rate_bottom + "~" + row.show_rate_top;
                }
            },{
                field:'status',
                title:'状态',
                formatter: function (value, row, index) {
                     if(row.trainStatus == '2'){//训练完成时，可执行上下线操作
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
                                 '<span class="text off">下线</span>' +
                                 '</label>' +
                                 '</div>';
                     }else{
                         //显示训练状态
                         if(row.trainStatus == '0'){
                            return "等待训练";
                         }else if(row.trainStatus == '1'){
                             return "训练中";
                         }else{
                             return "训练失败";
                         }
                     }

                }
            },{
                title:'操作',
                formatter:function (value, row, index) {
                    var str = '<a onclick=updateModels("' + row.id + '","' + row.name + '","' + row.des + '","' + row.show_rate_top + '","' + row.show_rate_bottom + '")>修改</a>';
                    str += '&nbsp;&nbsp;&nbsp;<a onclick=showModelDetail("' + row.id + '","' + row.name + '","' + row.des + '")>详情</a>';
                    str += '&nbsp;&nbsp;&nbsp;<a target="_blank" href=${rc.contextPath}/train/train/trainLog?train_no=' + row.train_no + '>日志</a>';
                    if(row.status!='2'){
                        str += '&nbsp;&nbsp;&nbsp;<a onclick=delModel("' + row.id + '")>删除</a>';
                    }
                    return str;
                }
            }]
        });

        //查看模型分类
        $("#showModelClassTable").bootstrapTable({
            cache:false,
//            search:true,
            url:'${rc.contextPath}/classification/classList',
            method:'post',
            contentType:"application/x-www-form-urlencoded",
            dataType:"json",
            pagination:true,
            pageNumber:1,
            pageSize:10,
            striped:true,
            sidePagination:"server",
            pageList:[10],
            queryParamsType:'',
            queryParams:function queryParams(params){
                var param = {
                    pageNumber: params.pageNumber,
                    pageSize: params.pageSize,
                    modelId: $("#queryClassModelId").val()
                };
                return param;
            },
            columns:[{
                checkbox:true
            },{
                field:'id',
                title:'ID'
            },{
                field:'alias',
                title:'分类名称'
            },{
                field:'classification',
                title:'别名'
            },{
                field:'des',
                title:'描述'
            },{
                field:'count',
                title:'图片数量'
            }]
        });

        //修改模型表单验证
        $('#editModel').bootstrapValidator({
            message: 'This value is not valid',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            excluded:[':disabled'],
            fields: {
                modelName: {
                    validators: {
                        notEmpty: {
                            message: '请输入模型名称'
                        }
                    }
                },
                showRateTop: {
                    validators: {
                        notEmpty: {
                            message: '请输入最高阈值'
                        },
                        regexp: {
                            regexp: /^[0-9_\.]+$/,
                            message: '只能输入数字'
                        }
                    }
                },
                showRateBottom: {
                    validators: {
                        notEmpty: {
                            message: '请输入最低阈值'
                        },
                        regexp: {
                            regexp: /^[0-9_\.]+$/,
                            message: '只能输入数字'
                        }
                    }
                }
            }
        });
    });

    //上线模型
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

    //查询
    function submitQuery(){
        $("#modelsTable").bootstrapTable('refreshOptions',{pageNumber:1});
        return false;
    }

    //展示模型修改窗体
    function updateModels(id, name, des,showRateTop,showRateBottom) {
        $('#editModel').data('bootstrapValidator').resetForm(true);
//        $('#editModel').bootstrapValidator('resetForm',true);

        $("#modelsId").val(id);
        $("#name").val(name);
        $("#des").val(des);
        $("#showRateTop").val(showRateTop);
        $("#showRateBottom").val(showRateBottom);

        $("#showEditModels").modal("show");
    }

    //修改模型
    function checkEditForm() {
        var name=$("#name").val();
        var des = $("#des").val();
        var modelsId = $("#modelsId").val();
        var showRateTop = $("#showRateTop").val();
        var showRateBottom = $("#showRateBottom").val();

        //表单验证
        var data = $('#editModel').data('bootstrapValidator');
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

    //查看详情
    function showModelDetail(modelId,modelName,modelDes){
        $.ajax({
            type: 'post',
            url: '${rc.contextPath}/train/queryTrainByModelId',
            data: {
                'modelId': modelId
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                $("#showTrainNo").html(result.train_no);
                $("#showNumShard").html(result.num_shard);
                $("#showNumTrain").html(result.num_train);
                $("#showTrainStepNum").html(result.train_step_num);
                $("#showTrainTime").html(getDateTime(result.create_time));
                var str = "训练失败";
                if(result.status == '0'){
                    str = "等待训练";
                }else if(result.status == '1'){
                    str = "训练中";
                }else if(result.status == '2'){
                    str = "训练完成";
                }
                $("#showTrainStatus").html(str);
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });

        $("#showModelName").html(modelName);
        $("#showModelDes").html(modelDes);

        $("#showModelDetail").modal("show");
    }
    function getDateTime(value) {
        var time = new Date(value);
        var y = time.getFullYear();
        var m = time.getMonth() + 1;
        var d = time.getDate();
        var h = time.getHours();
        var mi = time.getMinutes();
        var s = time.getSeconds();

        return y + "-" + m + "-" + d + " " + h + ":" + mi + ":" + s;
    }

    //删除模型
    function delModel(id) {
        swal({
            title: "",
            text: "是否确认删除此模型？",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消"
        }, function (isConfirm) {
            if (isConfirm) {
                var url = "${rc.contextPath}/models/delModel";

                $.ajax({
                    type: 'post',
                    url: url,
                    data: {
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

    function showModelClass(modelId){
        $("#queryClassModelId").val(modelId);

        $("#showModelClass").modal("show");
        $("#showModelClassTable").bootstrapTable('refreshOptions',{pageNumber:1});
    }
</script>
</html>