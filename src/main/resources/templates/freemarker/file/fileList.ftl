<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <link href="${rc.contextPath}/tagsinputDist/bootstrap-tagsinput.css" rel="stylesheet" type="text/css">
    <script src="${rc.contextPath}/tagsinputDist/bootstrap-tagsinput.js"></script>
    <link href="${rc.contextPath}/viewerDist/viewer.css" rel="stylesheet" type="text/css">
    <script src="${rc.contextPath}/viewerDist/viewer.js"></script>
    <title>图片管理</title>
</head>
<body>
<#include "../common.ftl" />

<#if Session["userInfo"].user.trainer==false>
    <#assign tempText="标签">
<#else>
    <#assign tempText="分类">
</#if>

<div class="container" style="position: relative;">
    <div class="container" style="border: 1px solid #ccc;font-size: 14px;" id="test">
        <div class="queryLable">&nbsp;&nbsp;&nbsp;&nbsp;查询条件</div>
        <input id="sessionUser" type="hidden" value=<#if Session["userInfo"].user.trainer==false>false<#else>true</#if>>
        <form id="pageForm" style="padding-top:20px;padding-left: 10px;" onsubmit="return submitQuery()">
            <div class="row">
                <input name="classId" type="hidden" id="classId" class="form-input" value="${classId!}">
                <div class="col-sm-4">
                    图片ID:
                    <input name="imageId" type="text" id="imageId" class="form-input" placeholder="请输入图片ID" value="${imageId!}">
                </div>
                <div class="col-sm-4">
                    图片${tempText}:
                    <input name="className" type="text" id="className" class="form-input" placeholder="请输入图片${tempText}" value="${className!}">
                </div>
                <#--<#if Session["userInfo"].user.trainer==true>-->
                    <#--<div class="col-sm-4">-->
                        <#--&nbsp;&nbsp;&nbsp;模型:-->
                        <#--<select class="form-input" id="modelId" name="modelId">-->
                            <#--<option value="">请选择</option>-->
                        <#--</select>-->
                    <#--</div>-->
                <#--</#if>-->
                <#if Session["userInfo"].user.trainer==true>
                    <div class="col-sm-4">
                        批次号:
                        <input name="batchNo" type="text" id="batchNo" class="form-input" placeholder="请输入批次号" value="${batchNo!}">
                    </div>
                </#if>
            </div>
            <div class="row">
                <div>
                    <button type="submit" onclick="submitQuery()" class="btn btn-primary" style="float: right;margin-right: 8%">查询</button>
                    <button type="reset" class="btn" style="float: right;margin-right: 1%">清空</button>
                </div>
            </div>
        </form>
    </div>
    <br>
    <div style="position: absolute;">
    <#if Session["userInfo"].user.trainer==false>
        <button type="button" onclick="showUpload()" class="btn btn-primary">图片上传</button>
    </#if>
        <button type="button" onclick="batchDel()" class="btn btn-primary">批量删除</button>
    </div>
    <br><br>
    <div class="form-group" style="font-size: 14px;background-color: white">
        <table id="picTable"> </table>
    </div>
</div>

<!--上传-->
<div id="uploading">正在上传……</div>
<div class="modal fade" id="showUpload" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;
                </button>
                <h2 class="modal-title" id="myModalLabel">图片上传</h2>
            </div>
            <form id="myfileForm" class="form-horizontal" role="form" action="${rc.contextPath}/file/upload/uploadFile"
                  method="post" enctype="multipart/form-data">
                <div class="modal-body" style="height: 80px;">
                    <input name="picManager" type="hidden" value="picManager">
                    <input name="file" type="file" value="本地上传图片" id="fileInput" class="form-control" accept=".jpg"  multiple>
                    <#--accept=".jpg,.png,.bmp"-->
                    <label style="width: 100%;font-size: 12px;">(只允许上传jpg格式文件)</label>
                    <#--,png,bmp-->
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" onclick="checkForm()" class="btn btn-sm" data-dismiss="modal">确定</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>


<!--修改标签-->
<div class="modal fade" id="showEditClass" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="myModalLabel">修改图片${tempText}</h2>
            </div>
            <form style="font-size: 12px;" id="editImagesClass" class="form-horizontal" role="form" method="post">
                <div class="modal-body" style="height: 200px;">
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">图片id：</label>
                        <div class="col-lg-6">
                            <input type="text" class="form-control" id="imgId" name="imgId" readonly>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-2 control-label">图片${tempText}：</label>
                        <div class="col-lg-10">
                            <input id="classTags" type="text" value="" data-role="tagsinput" placeholder="添加${tempText}"/>
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
        $("#filePicManager").css("color", "#d0cb16");

        var tempColumns="分类";
        if($("#sessionUser").val()=='false'){
            tempColumns='标签';
        }
        $("#picTable").bootstrapTable({
            url:'${rc.contextPath}/file/getImageList',
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
                    imageId: $("#imageId").val(),
                    className: $("#className").val(),
                    modelId: $("#modelId").val(),
                    batchNo: $("#batchNo").val(),
                    classId: $("#classId").val()
                };
                return param;
            },
            columns:[{
                checkbox:true
            },{
                field:'id',
                title:'ID'
            },{
                field:'url',
                title:'图片',
                formatter: function (value, row, index) {
                    return '<img data-original="${picUrl}' + row.url + '_1000x1000" src="${picUrl}' + row.url + '_100x100" style="height: 100px; width: 200px" onerror=this.src="../../images/invalid.jpeg">';
                }
            },{
                field:'alias',
                title: tempColumns,
                formatter:function (value, row, index) {
                    var str = row.alias;
                    return str + "<input id='class_" + row.id + "' type='hidden' value=" + str + ">";
                }
            },{
                field:'batchNo',
                title:'批次号'
            },{
                field:'uploadTime',
                title:'上传时间',
                formatter: getDateTime
            },{
                title:'操作',
                formatter:function (value, row, index) {
                    var str = '<a onclick=showEditClass("' + row.id + '")>修改</a>'
                            + '&nbsp;&nbsp;&nbsp;<a onclick=delImages("' + row.id + '")>删除</a>';
                    return str;
                }
            }]
        });

        $("#picTable").on('load-success.bs.table',function (data) {
            if($("#picTable").find('td').length!=1){//TODO 为空的时候调用destroy方法会报错
                $("#picTable").viewer('destroy');
                $("#picTable").viewer({url:'data-original'});
            }
        });

        if($("#sessionUser").val()=='false'){
            $("#picTable").bootstrapTable("hideColumn",'batchNo');
        }

        <#--//初始化模型选择下拉框-->
        <#--$.ajax({-->
            <#--type: 'post',-->
            <#--url: "${rc.contextPath}/models/queryModels",-->
            <#--data: {},-->
            <#--dataType: 'json',-->
            <#--async: false,-->
            <#--success: function (result) {-->
                <#--$.each(result.modelList,function (i,item) {-->
                    <#--$("#modelId").append('<option value="' + item.id + '">' + item.name + '</option>');-->
                <#--});-->
            <#--},-->
            <#--error: function () {-->
                <#--swal("", "系统错误，请联系管理员！", "error");-->
            <#--}-->
        <#--});-->
    });

    function getDateTime(value, row, index) {
        var time = new Date(value);
        var y = time.getFullYear();
        var m = time.getMonth() + 1;
        var d = time.getDate();

        return y + "-" + m + "-" + d;
    }

    function submitQuery(){
//        $("#picTable").bootstrapTable('refresh');
        $("#picTable").bootstrapTable('refreshOptions',{pageNumber:1});

        return false;
    }

    function showUpload() {
        $("#showUpload").modal("show");
    }

    function checkForm() {
        var fs = document.querySelector("#fileInput").files;
        if(fs.length==0){
            swal("提示", "请选择要上传的文件！", "warning");
            return;
        }else if(fs.length>10){
            swal("提示", "上传文件个数超过10张，请重新选择！", "warning");
            return;
        }
        var flag = true;
        $.each(fs,function (i,file) {
            if(!/.(bmp|jpg|png|BMP|JPG|PNG)$/.test(file.name)){
                swal("提示", "选择文件格式不正确，请重新选择！", "warning");
                flag = false;
                return;
            }
        });

        if(flag){
            $("#uploading").show().delay(5000);
            $("#myfileForm").submit();
        }
    }

    function delImages(imagesId) {
        swal({
            title: "",
            text: "是否确认删除该图片？",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消"
        }, function (isConfirm) {
            if (isConfirm) {
                $.ajax({
                    type: 'post',
                    url: "${rc.contextPath}/file/picManager/delImg",
                    data: {
                        imgId:imagesId
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        $("#picTable").bootstrapTable('refreshOptions',{pageNumber:1});
                    },
                    error: function () {
                        swal("", "系统错误，请联系管理员！", "error");
                    }
                });
            }
        });
    }

    function batchDel() {
        var imgIds = "";
        var selectRow = $("#picTable").bootstrapTable('getSelections');
        if(selectRow.length==0){
            swal("", "请选择要删除的图片！", "warning");
            return;
        }

        $.each(selectRow,function (i,item) {
            imgIds += item.id + ",";
        });

        swal({
            title: "",
            text: "是否确认删除图片？",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认",
            cancelButtonText: "取消"
        }, function (isConfirm) {
            if (isConfirm) {
                $.ajax({
                    type: 'post',
                    url: "${rc.contextPath}/file/picManager/delImg",
                    data: {
                        imgId: imgIds.substring(0, imgIds.length - 1)
                    },
                    dataType: 'json',
                    async: false,
                    success: function (result) {
                        $("#picTable").bootstrapTable('refreshOptions',{pageNumber:1});
                    },
                    error: function () {
                        swal("", "系统错误，请联系管理员！", "error");
                    }
                });
            }
        });
    }

    function showEditClass(imagesId) {
        $('#classTags').tagsinput('removeAll');

        $("#imgId").val(imagesId);
        var classStr = $("#class_" + imagesId).val();
        var classArray = classStr.split(',');
        for(var i = 0; i < classArray.length; i++) {
            $('#classTags').tagsinput('add', classArray[i]);
        }
        $("#showEditClass").modal("show");
    }

    function checkEditForm() {
        var classText = $("#classTags").tagsinput("items");
        if(classText==''){
            swal("提示", "图片标签不能为空！", "warning");
            return;
        }

        $.ajax({
            type: 'post',
            url: "${rc.contextPath}/file/picManager/editClass",
            data: {
                'imgId': $("#imgId").val(),
                'classText': classText + ","
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if (result.code == 100) {
                    $("#picTable").bootstrapTable('refreshOptions',{pageNumber:1});
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
</script>
</html>