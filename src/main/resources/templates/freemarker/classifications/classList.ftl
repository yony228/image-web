<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <title>分类管理</title>
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
    <div class="container" style="border: 1px solid #ccc;font-size: 14px;">
        <div class="queryLable">&nbsp;&nbsp;&nbsp;&nbsp;查询条件</div>
        <input id="sessionUser" type="hidden" value=<#if Session["userInfo"].user.trainer==false>false<#else>true</#if>>
        <form id="pageForm" style="padding-top:20px;padding-left: 10px;" onsubmit="return submitQuery()">
            <div class="row">
                <div class="col-sm-4">
                    分类名称:
                    <input name="alias" type="text" id="alias" class="form-input" placeholder="请输入分类名称" value="${alias!}">
                </div>
                <div class="col-sm-4">
                    分类别名:
                    <input name="classification" type="text" id="classification" class="form-input" placeholder="请输入分类别名" value="${classification!}">
                </div>
                <div class="col-sm-4">
                    分类描述:
                    <input name="des" type="text" id="des" class="form-input" placeholder="请输入分类描述" value="${des!}">
                </div>
            </div>
            <div class="row">
                <div class="col-sm-4">
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;模型:
                    <select class="form-input" id="queryModelId" name="queryModelId">
                        <option value="">请选择</option>
                    </select>
                </div>
                <div>
                    <button type="submit" onclick="submitQuery()" class="btn btn-primary" style="float: right;margin-right: 8%">查询</button>
                    <button type="reset" class="btn" style="float: right;margin-right: 1%">清空</button>
                </div>
            </div>
        </form>
    </div>
    <br>
    <div style="position: absolute;">
        <button type="button" onclick="addClass()" class="btn btn-primary">添加分类</button>
    </div>
    <br><br>
    <div class="form-group" style="font-size: 14px;background-color: white">
        <table id="classTable">  </table>
    </div>
</div>

<div class="modal fade" id="showEditClass" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="myModalLabel"></h2>
            </div>
            <form style="font-size: 12px;" id="editClass" class="form-horizontal" role="form" action="${rc.contextPath}/classification/addClassification" method="post">
                <div class="modal-body" style="height: 220px;">
                    <input type="text" class="form-input" id="classificationId" name="classificationId" value="" style="display: none">
                    <div style="display: none">
                    &nbsp;&nbsp;&nbsp;所属模型：
                    <select class="form-input" id="modelId" name="modelId">
                        <option value="0">请选择</option>
                    </select>
                    </div>
                    <br><br>
                    &nbsp;&nbsp;&nbsp;分类名称：<input type="text" class="form-input" id="classAlias" name="classAlias" value="">
                    <#--<br><br>-->
                    <#--&nbsp;&nbsp;&nbsp;分类别名：<input type="text" class="form-input" id="classClassification" name="classClassification" value="">-->
                    <br><br>
                    &nbsp;&nbsp;&nbsp;分类描述：<input type="text" class="form-input" id="classDes" name="classDes" value="">
                    <br>
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" onclick="checkEditForm()" class="btn btn-sm btn-primary" data-dismiss="modal">确定</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="showUpload" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times;
                </button>
                <h2 class="modal-title" id="myModalLabel"></h2>
            </div>
            <form id="myfileForm" class="form-horizontal" role="form" action="${rc.contextPath}/classification/uploadFile"
                  method="post" enctype="multipart/form-data">
                <div class="modal-body" style="height: 300px;">
                    <label for="uploadClassId">分类标识符</label>
                    <input id="uploadClassId" value="" class="form-input" width="100px;" name="classificationId" readonly >
                    <br><br>
                <#--<label for="uploadClassification">分类名称</label>-->
                <#--<input id="uploadClassification" value="" class="form-input" width="100px;" readonly="readonly">-->
                <#--<br><br>-->
                    <label for="uploadAlias">分类名称</label>
                    <input id="uploadAlias" value="" class="form-input" width="100px;" readonly="readonly">
                    <br><br>
                    <label for="uploadDes">分类描述</label>
                    <input id="uploadDes" value="" class="form-input" width="100px;" readonly="readonly">
                    <br><br>
                    <input style="border: 1px solid #ccc;" name="file" type="file" value="本地上传图片" id="fileInput" class="form-control" multiple>
                    <label style="width: 100%">(只允许上传jpg或zip格式文件)</label>
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" onclick="checkForm()" class="btn btn-sm btn-primary" data-dismiss="modal">确定</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<div id="uploading">正在上传……</div>
</body>
<script>
    $(function () {
        //初始化模型选择下拉框
        $.ajax({
            type: 'post',
            url: "${rc.contextPath}/models/queryModels",
            data: {},
            dataType: 'json',
            async: false,
            success: function (result) {
                $.each(result.modelList,function (i,item) {
                    $("#modelId").append('<option value="' + item.id + '">' + item.des + '</option>');
                    $("#queryModelId").append('<option value="' + item.id + '">' + item.des + '</option>');
                });

                $("#queryModelId").val(${modelId!});
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });

        $("#classManager").css("color", "#d0cb16");

        $("#classTable").bootstrapTable({
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
            pageNumber:1,
            queryParamsType:'',
            queryParams:function queryParams(params){
                var param = {
                    pageNumber: params.pageNumber,
                    pageSize: params.pageSize,
                    alias: $("#alias").val(),
                    classification: $("#classification").val(),
                    des: $("#des").val(),
                    modelId: $("#queryModelId").val()
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
                field:'modelDes',
                title:'所属模型'
            },{
                field:'count',
                title:'图片数量',
                formatter: function (value, row, index) {
                    return "<a target='_blank' href='${rc.contextPath}/file/file/fileList?className=" + row.alias + "'>" + value + "张</a>";
                }
            },{
                title:'操作',
                formatter:function (value, row, index) {
                    var str = "";
                    if(row.modelDes=='' || row.modelDes==undefined){
                        str = '<a onclick=updateClass("' + row.id + '","' + row.classification + '","' + row.alias + '","' + row.des + '","' + row.model_id + '")>修改</a>';
                    }
                    str += '&nbsp;&nbsp;&nbsp;<a onclick=delClass("' + row.id + '")>删除</a>'
                            + '&nbsp;&nbsp;&nbsp;<a onclick=showUpload("' + row.id + '","' + row.classification + '","' + row.alias + '","' + row.des + '")>添加图片</a>';
                    return str;
                }
            }]
        });
    });

    function submitQuery(){
        $("#classTable").bootstrapTable('refreshOptions',{pageNumber:1});
        return false;
    }

    function showUpload(classId,classification,alias, des) {
        $("#uploadClassId").val(classId);
//        $("#uploadClassification").val(classification);
        $("#uploadAlias").val(alias);
        $("#uploadDes").val(des);
        $("#showUpload").modal("show");
    }

    function checkForm() {
        var fs = document.querySelector("#fileInput").files;
        if(fs.length==0){
            swal("提示", "请选择要上传的文件！", "warning");
            return;
        }

        $("#uploading").show().delay(5000);
        $("#myfileForm").submit();
    }

    function addClass() {
        $("#classificationId").val('');
//        $("#classClassification").val('');
        $("#classAlias").val('');
        $("#classDes").val('');
        $("#modelId").val('0');
        $("#showEditClass").modal("show");
    }

    function updateClass(classId, classification, alias, des, modelId) {
        $("#classificationId").val(classId);
//        $("#classClassification").val(classification);
        $("#classAlias").val(alias);
        $("#classDes").val(des);
        $("#modelId").val(modelId);
        $("#showEditClass").modal("show");
    }

    function checkEditForm() {
//        var classification=$("#classClassification").val();
        var des = $("#classDes").val();
        var alias = $('#classAlias').val();
        var classificationId = $("#classificationId").val();
        var modelId = $("#modelId").val();

//        if(modelId=='0'){
//            swal("", "请选择所属模型！", "warning");
//            return;
//        }
        if(alias==''){
            swal("提示", "请输入分类名！", "warning");
            return;
        }


        var url = "${rc.contextPath}/classification/addClassification";
        if (classificationId != '') {
            url = "${rc.contextPath}/classification/updateClassification";
            //
            $.ajax({
                type: 'post',
                url: "${rc.contextPath}/classification/checkSameClass",
                data: {
                    'alias':alias,
                    'modelId': modelId,
                    'classificationId': classificationId
                },
                dataType: 'json',
                async: false,
                success: function (result) {
                    if(result.code==100){
                        submitEidt(url,classificationId, alias, des, modelId);
                    }else{
                        swal({
                            title: "",
                            text: "分类名重复，是否确认合并该分类？",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "确认",
                            cancelButtonText: "取消",
                            closeOnConfirm: true
                        }, function (isConfirm) {
                            if (isConfirm) {
                                submitEidt(url,classificationId, alias, des, modelId);
                            }
                        });
                    }
                },
                error: function () {
                    swal("", "系统错误，请联系管理员！", "error");
                }
            });
        }else {
            submitEidt(url,classificationId, alias, des, modelId);
        }
    }
    function submitEidt(url,classificationId, alias, des, modelId) {
        $.ajax({
            type: 'post',
            url: url,
            data: {
                'classificationId': classificationId,
//                'classification':classification,
                'alias': alias,
                'des': des,
                'modelId': modelId
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if (result.code == 100) {
                    $("#classTable").bootstrapTable('refreshOptions',{pageNumber:1});
                } else {
                    swal({title: "失败", text: result.msg, timer: 2000});
                }
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });
    }

    function delClass(classificationId){
        swal({
            title: "",
            text: "是否确认删除该分类？",
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
                    url: "${rc.contextPath}/classification/delClassification",
                    data: {
                        'classificationId':classificationId
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        if(result.code==100){
                            location.href = "${rc.contextPath}/classification/classifications/classList";
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