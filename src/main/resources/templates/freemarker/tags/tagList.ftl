<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <title>标签管理</title>
    <style>
        .modal-body label {
            /*width: 100px;*/
            font-size: 14px;
        }

        #myfileForm input {
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
                <button type="button" onclick="addTag()" class="btn btn-primary submit1">添加标签</button>
            </div>
            <form id="pageForm" style="position: relative;padding-top:10px;padding-left: 10px;"
                  onsubmit="return submitQuery()">
                <div style="float: right;margin-left: 100px;">
                    <input name="condition" type="text" id="condition" class="form-input"
                           style="float: right;width: 600px;margin-right: 100px;" placeholder="请输入标签名称、描述等模糊查询条件"
                           value="${condition!}">
                </div>
                <div style="position: absolute;right: 0;">
                    <button type="button" onclick="submitQuery()" class="btn btn-primary submit1"
                            style="float: right;margin-right: 20px">查询
                    </button>
                </div>
            </form>
        </div>
    </div>
    <br>
    <div class="form-group" style="font-size: 14px;background-color: white">
        <table id="tagTable"></table>
    </div>
</div>

<div class="modal fade" id="showEditTag" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="editTagsModalLabel"></h2>
            </div>
            <form style="font-size: 12px;" id="editTags" class="form-horizontal" role="form" method="post">
                <div class="modal-body">
                    <input type="text" class="form-input" id="tagId" name="tagId" value="" style="display: none">
                    <div class="form-group row">
                        <label class="col-lg-3 control-label">标签名称：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="tagName" name="tagName" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-3 control-label">标签描述：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="tagDes" name="tagDes" value="">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-3 control-label">是否公开：</label>
                        <div class="col-lg-6">
                            <input type="radio" id="public1" name="isPublic" value="1">是 &nbsp;&nbsp;&nbsp;
                            <input type="radio" id="public2" name="isPublic" value="2">否
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

<div class="modal fade" id="showUpload" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="myModalLabel"></h2>
            </div>
            <form id="myfileForm" class="form-horizontal" role="form" action="${rc.contextPath}/tag/uploadFile"
                  method="post" enctype="multipart/form-data">
                <div class="modal-body" style="height: auto">
                    <div class="form-group row">
                        <label class="col-lg-3 control-label">标签标识符：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-input" id="uploadTagId" name="tagId" readonly>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-3 control-label">标签名称：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-input" id="uploadTagName" readonly>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-3 control-label">标签描述：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-input" id="uploadTagDes" readonly>
                        </div>
                    </div>
                    <input style="border: 1px solid #ccc;width: 80%" name="file" type="file" value="本地上传图片"
                           id="fileInput" class="form-control" multiple>
                    <label style="width: 100%">(只允许上传jpg或zip格式文件)</label>
                <#--,png,bmp-->
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" onclick="checkForm()" class="btn btn-sm btn-primary" data-dismiss="modal">
                            确定
                        </button>
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
        $("#classManager").css("color", "#d0cb16");

        $("#tagTable").bootstrapTable({
            url: '${rc.contextPath}/tag/getTagList',
            method: 'post',
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            pagination: true,
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
                checkbox: true
            }, {
                field: 'id',
                title: 'ID'
            }, {
                field: 'alias',
                title: '标签名称'
            }, {
                field: 'des',
                title: '描述'
            }, {
                field: 'is_public',
                title: '是否公开',
                formatter: function (value, row, index) {
                    if (row.is_public == '2') {
                        return "否"
                    }
                    return "是";
                }
            }, {
                field: 'count',
                title: '图片数量',
                formatter: function (value, row, index) {
                    return "<a target='_blank' href='${rc.contextPath}/file/file/fileList?classId=" + row.id + "'>" + value + "张</a>";
                }
            }, {
                title: '操作',
                formatter: function (value, row, index) {
                    var str = '<a onclick=updateTag("' + row.id + '","' + row.alias + '","' + row.des + '","' + row.is_public + '")>修改</a>'
                            + '&nbsp;&nbsp;&nbsp;<a onclick=delTag("' + row.id + '")>删除</a>'
                            + '&nbsp;&nbsp;&nbsp;<a onclick=showUpload("' + row.id + '","' + row.alias + '","' + row.des + '")>添加图片</a>';
                    return str;
                }
            }]
        });

        //表单验证
        $('#editTags').bootstrapValidator({
            message: 'This value is not valid',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            excluded: [':disabled'],
            fields: {
                tagName: {
                    validators: {
                        notEmpty: {
                            message: '请输入标签名称'
                        }
                    }
                }
            }
        });
    });

    function submitQuery() {
        $("#tagTable").bootstrapTable('refreshOptions', {pageNumber: 1});
        return false;
    }

    function showUpload(tagId, alias, des) {
        $("#uploadTagId").val(tagId);
        $("#uploadTagName").val(alias);
        $("#uploadTagDes").val(des);
        $("#showUpload").modal("show");
    }

    function checkForm() {
        var fs = document.querySelector("#fileInput").files;
        if (fs.length == 0) {
            swal("提示", "请选择要上传的文件！", "warning");
            return;
        }

        $("#uploading").show().delay(5000);
        $("#myfileForm").submit();
    }

    function addTag() {
        $('#editTags').data('bootstrapValidator').resetForm(true);

        $("#tagId").val('');
        $("#tagName").val('');
        $("#tagDes").val('');
        $("#public1").click();

        $("#editTagsModalLabel").html("添加标签");
        $("#showEditTag").modal("show");
    }

    function updateTag(tagId, alias, des, isPublic) {
        $('#editTags').data('bootstrapValidator').resetForm(true);

        $("#tagId").val(tagId);
        $("#tagName").val(alias);
        $("#tagDes").val(des);
        $("#public" + isPublic).click();

        $("#editTagsModalLabel").html("修改标签");
        $("#showEditTag").modal("show");
    }

    function checkEditForm() {
        var des = $("#tagDes").val();
        var alias = $("#tagName").val();
        var tagId = $("#tagId").val();
        var isPublic = $("input[name='isPublic']:checked").val();

        //表单验证
        var data = $('#editTags').data('bootstrapValidator');
        if (data) {
            data.validate();
            if (!data.isValid()) {
                return;
            }
        }

        var url = "${rc.contextPath}/tag/addTag";
        if (tagId != '') {
            url = "${rc.contextPath}/tag/updateTag";
            //
            $.ajax({
                type: 'post',
                url: "${rc.contextPath}/tag/checkSameTag",
                data: {
                    'alias': alias,
                    'tagId': tagId
                },
                dataType: 'json',
                async: false,
                success: function (result) {
                    if (result.code == 100) {
                        submitEidt(url, tagId, alias, des, isPublic);
                    } else {
                        swal({
                            title: "",
                            text: "标签名重复，是否确认合并该标签？",
                            type: "warning",
                            showCancelButton: true,
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "确认",
                            cancelButtonText: "取消",
                            closeOnConfirm: true
                        }, function (isConfirm) {
                            if (isConfirm) {
                                submitEidt(url, tagId, alias, des, isPublic);
                            }
                        });
                    }
                },
                error: function () {
                    swal("", "系统错误，请联系管理员！", "error");
                }
            });
        } else {
            submitEidt(url, tagId, alias, des, isPublic);
        }
    }
    function submitEidt(url, tagId, alias, des, isPublic) {
        $.ajax({
            type: 'post',
            url: url,
            data: {
                'tagId': tagId,
                'alias': alias,
                'des': des,
                'isPublic': isPublic
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if (result.code == 100) {
                    $("#tagTable").bootstrapTable('refreshOptions', {pageNumber: 1});
                    $("#showEditTag").modal("hide");
                } else {
                    swal({title: "失败", text: result.msg, timer: 2000});
                }
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });
    }

    function delTag(tagId) {
        swal({
            title: "",
            text: "是否确认删除该标签？",
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
                    url: "${rc.contextPath}/tag/delTag",
                    data: {
                        'tagId': tagId
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        if (result.code == 100) {
                            location.href = "${rc.contextPath}/tag/tags/tagList";
                        } else {
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