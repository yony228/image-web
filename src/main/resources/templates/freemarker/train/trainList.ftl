<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <link href="${rc.contextPath}/css/jquery.steps.css" rel="stylesheet" type="text/css">
    <script src="${rc.contextPath}/js/jquery.steps.js" type="text/javascript"></script>

    <title>训练管理</title>
    <style>
    </style>
</head>
<body>
<#include "../common.ftl" />
<div class="container" style="position: relative;">
    <div style="background-color: #ccc;height: 50px;font-size: 14px;border-radius: 4px;">
        <div class="form-group">
            <div style="position: absolute;padding-top:10px;padding-left: 20px;z-index: 1000;">
                <button type="button" onclick="showAddTrain()" class="btn btn-primary submit1">添加训练
                </button>
            </div>
            <form id="pageForm" style="position: relative;padding-top:10px;padding-left: 10px;" onsubmit="return submitQuery()">
                <div style="float: right;margin-left: 100px;">
                    <input name="condition" type="text" id="condition" class="form-input" style="float: right;width: 600px;margin-right: 100px;" placeholder="请输入训练号，模型名称等条件查询"value="${train_no!}">
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
        <table id="trainTable"></table>
    </div>
</div>

<div class="modal fade" id="createModelsDiv" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h2 class="modal-title" id="myModalLabel"></h2>
            </div>
            <div class="modal-body" style="height: 120px;font-size: 12px">
                <input type="hidden" id="trainNo" value="">
                &nbsp;&nbsp;&nbsp;模型名称：<input type="text" class="form-input" id="name" name="classAlias" value="">
                <br><br>
                &nbsp;&nbsp;&nbsp;模型描述：<input type="text" class="form-input" id="des" name="classDes" value="">
                <br>
            </div>
            <div class="modal-footer">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="button" onclick="checkCreateModels()" class="btn btn-sm btn-primary"
                            data-dismiss="modal">确定
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="addTrainDiv" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 95%;">
    <div class="modal-dialog" style="width: 70%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 class="modal-title" id="myModalLabel">添加训练</h3>
            </div>
            <div class="modal-body" style="font-size: 12px;height: 80%">
                <div id="wizard" class="wizard">
                    <h4>请选择分类</h4>
                    <div id="firstDiv">
                        <div style="font-size: 14px;border-radius: 4px;">
                            <div class="form-group">
                                <button type="button" onclick="showClassDiv()" class="btn btn-primary">选择分类</button>
                            </div class="form-group">
                        </div>
                        <div class="form-group" style="font-size: 14px;background-color: white;height: 90%;overflow: auto;">
                            <table id="trainClassTable"> </table>
                        </div>
                    </div>
                    <h4>填写训练信息</h4>
                    <div id="secondDiv" style="display: none;font-size: 14px;">
                        <div class="form-group row">
                            <label class="col-lg-4 control-label">图片总数：</label>
                            <div class="col-lg-4">
                                <input id="numTrain" name="numTrain" type="text" class="form-control" value="0" readonly style="border: 0px solid #ccc;">
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-lg-4 control-label">训练集分片数量：</label>
                            <div class="col-lg-4">
                                <input id="numShard" type="text" class="form-control" value="1">
                            </div>
                        </div>
                        <div class="form-group row" style="display: none;">
                            <label class="col-lg-4 control-label">验证集数量：</label>
                            <div class="col-lg-4">
                                <input id="numValidation" type="text" class="form-control" value="0">
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-lg-4 control-label">训练步数：</label>
                            <div class="col-lg-4">
                                <input id="trainStepNum" type="text" class="form-control" value="5000">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <div class="col-sm-offset-2 col-sm-10">
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="showClass" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">选择分类</h4>
            </div>
            <div class="modal-body" style="font-size: 14px;">
                模型：
                <select class="form-input" id="modelId" name="modelId">
                    <option value="">请选择</option>
                </select>
                <button type="button" onclick="submitClassQuery()"  class="btn btn-primary" style="float: right;">查询</button>
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
    $(function () {
        $("#trainClass").css("color", "#d0cb16");
//        $("#modelsManager").css("color", "#d0cb16");

        $("#trainTable").bootstrapTable({
            url: '${rc.contextPath}/train/queryTrainList',
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
                checkbox: true
            }, {
                field: 'train_no',
                title: '训练批次号'
            }, {
                field: 'alias',
                title: '训练分类',
                formatter: function (value, row, index) {
                    if (value.length > 20) {
                        return '<span title="' + value + '">' + value.substring(0, 20) + '...</span>';
                    }else{
                        return '<span title="' + value + '">' + value +'</span>';
                    }
                }
            }, {
                field: 'train_step_num',
                title: '训练步数'
            }, {
                field: 'status',
                title: '状态',
                formatter: function (value, row, index) {
                    var str = "-";
                    if (value == '-1') {
                        str = "训练失败";
                    } else if (value == '0') {
                        str = "等待训练";
                    } else if (value == '1') {
                        str = "训练中";
                    } else if (value == '2') {
                        str = "训练完成";
                    } else if (value == '3') {
                        str = "已产生模型";
                    }
                    return str;
                }
            }, {
                field: 'create_time',
                title: '开始训练时间'
            }, {
                field: 'modelDes',
                title: '所属模型'
            }, {
                title: '操作',
                formatter: function (value, row, index) {
                    var str = '<a target="_blank" href=${rc.contextPath}/train/train/trainLog?train_no=' + row.train_no + '>查看日志</a>';
                    if (row.status == '2') {
                        str += '&nbsp;&nbsp;&nbsp;<a onclick=showCreateModels("' + row.train_no + '")>生成模型</a>';
                    }
                    return str;
                }
            }]
        });


        //new ----------------------
        $("#trainClass").css("color", "#d0cb16");
//        $("#modelsManager").css("color", "#d0cb16");

        $("#trainClassTable").bootstrapTable({
            cache:false,
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
        //end---------------------------------
    });

    function submitQuery() {
        $("#trainTable").bootstrapTable('refreshOptions',{pageNumber:1});
        return false;
    }

    function showCreateModels(trainNo) {
        $("#trainNo").val(trainNo);
        $("#name").val('');
        $("#des").val('');
        $("#createModelsDiv").modal("show");
    }

    function checkCreateModels() {
        var name = $("#name").val();
        var des = $("#des").val();

        if (name == '') {
            swal("", "请输入模型名称！", "warning");
            return;
        }

        var url = "${rc.contextPath}/train/createModels";

        $.ajax({
            type: 'post',
            url: url,
            data: {
                'train_no': $("#trainNo").val(),
                'modelName': name,
                'modelDes': des
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if (result.code == 100) {
                    $("#trainTable").bootstrapTable('refreshOptions',{pageNumber:1});
                } else {
                    swal({title: "失败", text: result.msg, timer: 2000});
                }
            },
            error: function () {
                swal("", "系统错误，请联系管理员！", "error");
            }
        });
    }


    //-----------------------------
    var notClassId = "";
    var numTrain = 0;
    function showAddTrain(){
        // $("#trainClassTable").bootstrapTable('resetView');

        //TODO 清空表格数据
        var trainClassTableData = $("#trainClassTable").bootstrapTable('getData');
        var ids = [];
        $.each(trainClassTableData,function (i,item) {
            ids.push(item.id);
        });
        $("#trainClassTable").bootstrapTable('remove', {
            field: 'id',
            values: ids
        });
        notClassId="";
        numTrain=0;
        $("#numTrain").val(numTrain);

//        $("#wizard").steps('destroy');
//        $("#wizard").steps({
//            headerTag: "h4",
//            bodyTag: "div",
//            enableContentCache: false,
//            transitionEffect: "slideLeft",
//            autoFucus: true,
//            onFinished: function (e, currentIndex) {
//                submitTrain();
//            }
//        });
        $("#wizard").steps('previous');

        $("#addTrainDiv").modal("show");
    }

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
    function showClassDiv() {
        $("#showClass").modal("show");
        $("#showClassTable").bootstrapTable('refreshOptions',{pageNumber:1});
    }

    function submitClassQuery(){
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
    //end--------------------------------
</script>
</html>