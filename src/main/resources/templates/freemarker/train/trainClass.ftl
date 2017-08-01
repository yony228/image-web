<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <link href="${rc.contextPath}/css/jquery.steps.css" rel="stylesheet" type="text/css">
    <script src="${rc.contextPath}/js/jquery.steps.js" type="text/javascript"></script>

    <title>模型训练</title>
</head>
<body>
<#include "../common.ftl" />
<div class="container" style="position: relative;">
    <div id="wizard" class="wizard">
        <h4>请选择分类</h4>
        <div id="firstDiv">
            <div style="font-size: 14px;border-radius: 4px;">
                <div class="form-group">
                    <button type="button" onclick="showClassDiv()" class="btn btn-primary">选择分类</button>
                <#--<button type="button" onclick="addClassDiv()" class="btn btn-primary">添加分类</button>-->
                </div class="form-group">
            </div>
            <br>
            <div class="form-group" style="font-size: 14px;background-color: white">
                <table id="trainClassTable"> </table>
            </div>
        </div>
        <h4>填写训练信息</h4>
        <div id="secondDiv" style="display: none;font-size: 14px;">
            <div class="form-group row">
                <label class="col-lg-2 control-label">图片总数：</label>
                <div class="col-lg-4">
                    <input id="numTrain" name="numTrain" type="text" class="form-control" value="0" readonly style="border: 0px solid #ccc;">
                </div>
            </div>
            <div class="form-group row">
                <label class="col-lg-2 control-label">训练集分片数量：</label>
                <div class="col-lg-4">
                    <input id="numShard" type="text" class="form-control" value="1">
                </div>
            </div>
            <div class="form-group row" style="display: none;">
                <label class="col-lg-2 control-label">验证集数量：</label>
                <div class="col-lg-4">
                    <input id="numValidation" type="text" class="form-control" value="0">
                </div>
            </div>
            <div class="form-group row">
                <label class="col-lg-2 control-label">训练步数：</label>
                <div class="col-lg-4">
                    <input id="trainStepNum" type="text" class="form-control" value="5000">
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="showClass" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h2 class="modal-title" id="myModalLabel">选择分类</h2>
                </div>
                <div class="modal-body" style="font-size: 14px;">
                    模型：
                    <select class="form-input" id="modelId" name="modelId">
                        <option value="">请选择</option>
                    </select>
                    <button type="button" onclick="submitQuery()"  class="btn btn-primary" style="float: right;">查询</button>
                    <br><br>
                    <table id="showClassTable"></table>
                </div>
                <div class="modal-footer">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="button" class="btn btn-sm btn-primary" onclick="addClass()" data-dismiss="modal">确定</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
<script>
    $("#wizard").steps({
        headerTag: "h4",
        bodyTag: "div",
        enableContentCache: false,
        transitionEffect: "slideLeft",
        autoFucus: true,
        onFinished: function (e, currentIndex) {
            submitTrain();
        }
    });

    var notClassId = "";
    var numTrain = 0;

    $(function () {
        $("#trainClass").css("color", "#d0cb16");
//        $("#modelsManager").css("color", "#d0cb16");

        $("#trainClassTable").bootstrapTable({
            pagination:false,
            striped:true,
            formatNoMatches: function () {
                return "请选择要参与训练的分类";
            },
            onDblClickCell:function (fieId,value,row) {
                $("#trainClassTable").bootstrapTable('remove', {
                    field: 'id',
                    values: [row.id]
                });
                notClassId = notClassId.replace(row.id + ",", "");
            },
            onCheck:function (row) {
                numTrain += row.count;
                $("#numTrain").val(numTrain);
            },
            onUncheck:function (row) {
                numTrain -= row.count;
                $("#numTrain").val(numTrain);
            },
            onCheckAll:function () {
                numTrain = 0;
                var allRows = $("#trainClassTable").bootstrapTable('getData');
                $.each(allRows,function (i,item) {
                    numTrain += item.count;
                });
                $("#numTrain").val(numTrain);
            },
            onUncheckAll:function () {
                numTrain = 0;
                $("#numTrain").val(numTrain);
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
                field:'des',
                title:'描述'
            },{
                field:'modelDes',
                title:'所属模型'
            },{
                field:'count',
                title:'图片数量',
                formatter: function (value, row, index) {
                    if(value=='0'){
                        //this.find('checkbox')
                    }
                    return "<a target='_blank' href='${rc.contextPath}/file/file/fileList?className=" + row.alias + "'>" + value + "张</a>";
                }
            }
//          ,{
//                title:'操作',
//                formatter: function (value, row, index) {
//                    return "<a>添加图片</a>";
//                }
//            }
            ]
        });

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
                });
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });



        $("#showClassTable").bootstrapTable({
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
                var tempId = "";
                if(notClassId!=""){
                    tempId = notClassId.substring(0, notClassId.length - 1);
                }
                var param = {
                    pageNumber: params.pageNumber,
                    pageSize: params.pageSize,
                    modelId: $("#modelId").val(),
                    notClassId: tempId
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
            }]
        });
    });


    function showClassDiv() {
        $("#showClass").modal("show");
        $("#showClassTable").bootstrapTable('refreshOptions',{pageNumber:1});
    }

    function submitQuery(){
        $("#showClassTable").bootstrapTable('refreshOptions',{pageNumber:1});
    }

    function addClass(){
        var selectRow = $("#showClassTable").bootstrapTable('getSelections');

        var index = $("#trainClassTable").bootstrapTable('getData').length;
        $.each(selectRow,function (i,item) {
            $("#trainClassTable").bootstrapTable('insertRow',{
                index: index+i,
                row:item
            });
            notClassId += item.id + ",";
            numTrain += item.count;
        });

        $("#numTrain").val(numTrain);
//        $("#trainClassTable").bootstrapTable("uncheckAll");
//        var data = $("#showClassTable").bootstrapTable("getAllSelections");
//        $("#trainClassTable").bootstrapTable("append", data);
    }

    function submitTrain() {
        var selectRow = $("#trainClassTable").bootstrapTable('getSelections');
        if(selectRow.length==0){
            swal("", "请选择要训练的分类！", "warning");
            return;
        }
        if(numTrain==0){
            swal("", "请添加分类图片！", "warning");
            return;
        }
        if($("#numShard").val()==''){
            swal("", "请输入训练集分片数量！", "warning");
            return;
        }
        if($("#numValidation").val()==''){
            swal("", "请输入验证集数量！", "warning");
            return;
        }

        var classId="";
        $.each(selectRow, function (i, item) {
            classId += item.id + ",";
        });

        $.ajax({
            type: 'post',
            url: '${rc.contextPath}/train/submitTrain',
            data: {
                'classId': classId,
                'numTrain': numTrain,
                'numShard': $("#numShard").val(),
                'numValidation': $("#numValidation").val(),
                'trainStepNum': $("#trainStepNum").val()
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if(result.code==100){
                    swal({   title: "",   text: "成功！",   timer: 2000 });
                    setTimeout(location.href = "${rc.contextPath}/train/train/trainList?train_no=" + result.trainNo, 2000);
                }else{
                    swal({   title: "失败",   text: result.msg,   timer: 2000 });
                }
            },
            error: function () {
                swal("", "调用失败，请联系管理员！", "error");
            }
        });
    }
</script>
</html>