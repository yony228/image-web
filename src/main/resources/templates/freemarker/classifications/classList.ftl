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
                <#--<div class="col-sm-4">-->
                    <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;模型:-->
                    <#--<select class="form-input" id="queryModelId" name="queryModelId">-->
                        <#--<option value="">请选择</option>-->
                    <#--</select>-->
                <#--</div>-->
                <div>
                    <button type="submit" class="btn btn-primary" style="float: right;margin-right: 8%">查询</button>
                    <button type="reset" class="btn" style="float: right;margin-right: 1%">清空</button>
                </div>
            </div>
        </form>
    </div>
    <br>
    <div style="position: absolute;">
        <button type="button" onclick="addClass()" class="btn btn-primary">添加分类</button>
        <#if Session["userInfo"].user.name=='admin'><button type="button" onclick="showTagsDiv()" class="btn btn-primary">复制标签</button></#if>
    </div>
    <br><br>
    <div class="form-group" style="font-size: 14px;background-color: white">
        <table id="classTable">  </table>
    </div>
</div>

<!--添加修改分类-->
<div class="modal fade" id="showEditClass" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="editClassModalLabel"></h2>
            </div>
            <form style="font-size: 12px;" id="editClass" class="form-horizontal" role="form" method="post">
                <div class="modal-body" style="height: auto">
                    <input type="text" class="form-input" id="classificationId" name="classificationId" value="" style="display: none">
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">分类名称：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="classAlias" name="classAlias" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">分类描述：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="classDes" name="classDes" value="">
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

<!--复制普通用户公开的标签-->
<div class="modal fade" id="showPublicTagDiv" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">普通用户标签</h4>
            </div>
            <div class="modal-body" style="font-size: 14px;">
                <form id="tagDivForm" onsubmit="return queryTags()">
                    标签名称：
                    <input name="tagAlias" type="text" id="tagAlias" class="form-input" placeholder="请输入标签名称" value="">
                    <button type="submit"  class="btn btn-primary" style="float: right;">查询</button>
                    <br><br>
                    <table id="showPublicTagTable"></table>
                </form>
            </div>
            <div class="modal-footer">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="button" class="btn btn-sm" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>
</div>

<!--上传图片-->
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
                    <div class="form-group row">
                        <label class="col-lg-2 control-label" for="uploadClassId">分类标识</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-input" id="uploadClassId" name="classificationId" value="" readonly>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label" for="uploadAlias">分类名称</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-input" id="uploadAlias" readonly>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label" for="uploadDes">分类描述</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-input" id="uploadDes" value="" readonly>
                        </div>
                    </div>
                    <input style="border: 1px solid #ccc;" name="file" type="file" value="本地上传图片" id="fileInput" class="form-control" multiple>
                    <label style="width: 100%">(只允许上传jpg或zip格式文件)</label>
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" onclick="checkForm()" class="btn btn-sm btn-primary" data-dismiss="modal">确定</button>
                        <button type="button" class="btn btn-sm" data-dismiss="modal">取消</button>
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
        <#--//初始化模型选择下拉框-->
        <#--$.ajax({-->
            <#--type: 'post',-->
            <#--url: "${rc.contextPath}/models/queryModels",-->
            <#--data: {},-->
            <#--dataType: 'json',-->
            <#--async: false,-->
            <#--success: function (result) {-->
                <#--$.each(result.modelList,function (i,item) {-->
                    <#--$("#queryModelId").append('<option value="' + item.id + '">' + item.name + '</option>');-->
                <#--});-->

                <#--$("#queryModelId").val(${modelId!});-->
            <#--},-->
            <#--error: function () {-->
                <#--swal("", "系统错误，请联系管理员！", "error");-->
            <#--}-->
        <#--});-->

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
//                    modelId: $("#queryModelId").val()
                };
                return param;
            },
            columns:[{
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
                title:'图片数量',
                formatter: function (value, row, index) {
                    return "<a target='_blank' href='${rc.contextPath}/file/file/fileList?classId=" + row.id + "'>" + value + "张</a>";
                }
            },{
                title:'操作',
                formatter:function (value, row, index) {
                    var str = '<a onclick=updateClass("' + row.id + '","' + row.alias + '","' + row.des + '")>修改</a>';
                    str += '&nbsp;&nbsp;&nbsp;<a onclick=delClass("' + row.id + '")>删除</a>'
                            + '&nbsp;&nbsp;&nbsp;<a onclick=showUpload("' + row.id + '","' + row.classification + '","' + row.alias + '","' + row.des + '")>添加图片</a>';
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
                            message: '请输入分类名称'
                        }
                    }
                }
            }
        });


        //查询公开标签
        $("#showPublicTagTable").bootstrapTable({
            cache:false,
            url:'${rc.contextPath}/tag/queryPublicTagList',
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
                    alias: $("#tagAlias").val()
                };
                return param;
            },
            columns:[{
                field:'id',
                title:'ID'
            },{
                field:'alias',
                title:'标签名称'
            },{
                field:'des',
                title:'描述'
            },{
                field:'count',
                title:'图片数量',
                formatter: function (value, row, index) {
                    return '<a>'+ value + '张</a>';
                }
            },{
                field:'userName',
                title:'所属用户'
            },{
                field:'',
                title:'操作',
                formatter: function (value, row, index) {
                    return '<a onclick=copyTags("' + row.id + '","'+ row.alias +'")>复制</a>';
                }
            }]
        });
    });

    function submitQuery(){
        $("#classTable").bootstrapTable('refreshOptions',{pageNumber:1});
        return false;
    }

    function showUpload(classId,alias, des) {
        $("#uploadClassId").val(classId);
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
        $('#editClass').data('bootstrapValidator').resetForm(true);

        $("#classificationId").val('');
        $("#classAlias").val('');
        $("#classDes").val('');

        $("#editClassModalLabel").html("添加分类");
        $("#showEditClass").modal("show");
    }

    function updateClass(classId, alias, des) {
        $('#editClass').data('bootstrapValidator').resetForm(true);

        $("#classificationId").val(classId);
        $("#classAlias").val(alias);
        $("#classDes").val(des);

        $("#editClassModalLabel").html("修改分类");
        $("#showEditClass").modal("show");
    }

    function checkEditForm() {
        var des = $("#classDes").val();
        var alias = $('#classAlias').val();
        var classificationId = $("#classificationId").val();

        //表单验证
        var data = $('#editClass').data('bootstrapValidator');
        if(data){
            data.validate();
            if(!data.isValid()){
                return;
            }
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
                    'classificationId': classificationId
                },
                dataType: 'json',
                async: false,
                success: function (result) {
                    if(result.code==100){
                        submitEdit(url,classificationId, alias, des);
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
                                submitEdit(url,classificationId, alias, des);
                            }
                        });
                    }
                },
                error: function () {
                    swal("", "系统错误，请联系管理员！", "error");
                }
            });
        }else {
            submitEdit(url,classificationId, alias, des);
        }
    }

    function submitEdit(url,classificationId, alias, des) {
        $.ajax({
            type: 'post',
            url: url,
            data: {
                'classificationId': classificationId,
                'alias': alias,
                'des': des
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if (result.code == 100) {
                    $("#classTable").bootstrapTable('refreshOptions',{pageNumber:1});
                    $("#showEditClass").modal("hide");
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

    function showTagsDiv(){
        $("#showPublicTagDiv").modal("show");
        $("#showPublicTagTable").bootstrapTable('refreshOptions',{pageNumber:1});
    }

    function queryTags(){
        $("#showPublicTagTable").bootstrapTable('refreshOptions',{pageNumber:1});
        return false;
    }

    //复制用户标签为分类
    function copyTags(tagId,alias) {
        swal({
            title: "",
            text: "是否确认复制该标签及其图片到分类？",
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
                    url: "${rc.contextPath}/classification/checkSameClass",
                    data: {
                        'alias':alias
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        if(result.code==100){
                            submitCopyTag(tagId);
                        }else{
                            swal({
                                title: "",
                                text: "该标签与现有分类名重复，是否确认合并？",
                                type: "warning",
                                showCancelButton: true,
                                confirmButtonColor: "#DD6B55",
                                confirmButtonText: "确认",
                                cancelButtonText: "取消",
                                closeOnConfirm: true
                            }, function (isConfirm) {
                                if (isConfirm) {
                                    submitCopyTag(tagId);
                                }
                            });
                        }
                    },
                    error: function () {
                        swal("", "系统错误，请联系管理员！", "error");
                    }
                });
            }
        });
    }

    function submitCopyTag(tagId) {
        $.ajax({
            type: 'post',
            url: "${rc.contextPath}/classification/copyTags",
            data: {
                'tagId':tagId
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                location.href = "${rc.contextPath}/classification/classifications/classList";
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });
    }
</script>
</html>