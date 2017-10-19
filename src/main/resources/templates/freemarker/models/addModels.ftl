<div class="modal fade" id="addTrainDiv" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 95%;">
    <div class="modal-dialog" style="width: 70%;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 class="modal-title" id="myModalLabel">添加模型</h3>
            </div>
            <div class="modal-body" style="font-size: 12px;">
                <div id="wizard" class="wizard">
                    <h4>请选择分类</h4>
                    <div id="firstDiv">
                        <div style="font-size: 14px;border-radius: 4px;">
                            <div class="form-group">
                                <button type="button" onclick="showClassDiv()" class="btn btn-primary">选择分类</button>
                            </div class="form-group">
                        </div>
                        <div class="form-group" style="font-size: 14px;background-color: white;height: 80%;overflow: auto;">
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
                    <h4>填写模型信息</h4>
                    <div id="thirdDiv" style="display: none;font-size: 14px;">
                        <div class="form-group row">
                            <label class="col-lg-4 control-label">模型名称：</label>
                            <div class="col-lg-4">
                                <input id="modelName" name="modelName" type="text" class="form-control" value="">
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-lg-4 control-label">模型描述：</label>
                            <div class="col-lg-4">
                                <input id="modelDes" name="modelDes" type="text" class="form-control" value="">
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
                分类名称：
                <input name="alias" type="text" id="alias" class="form-input" placeholder="请输入分类名称" value="">
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


<script>
    var notClassId = "";
    var numTrain = 0;

    $(function () {
        $("#trainClassTable").bootstrapTable({
            cache:false,
            pagination:false,
            striped:true,
            formatNoMatches: function () {
                return "请选择要参与训练的分类";
            },
//            onDblClickCell:function (fieId,value,row) {
//                $("#trainClassTable").bootstrapTable('remove', {
//                    field: 'id',
//                    values: [row.id]
//                });
//                notClassId = notClassId.replace(row.id + ",", "");
//            },
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
                    return "<a target='_blank' href='${rc.contextPath}/file/file/fileList?classId=" + row.id + "'>" + value + "张</a>";
                }
            },{
                field:'',
                title:'操作',
                formatter: function (value, row, index) {
                    return "<a target='_blank' onclick=delClass('" + row.id + "','"+row.count +"')>删除</a>";
                }
            }]
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
                    alias: $("#alias").val(),
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
                field:'count',
                title:'图片数量',
                formatter: function (value, row, index) {
                    return "<a target='_blank' href='${rc.contextPath}/file/file/fileList?classId=" + row.id + "'>" + value + "张</a>";
                }
            }]
        });
    });


    //-----------------------------

    //初始化添加训练窗体
    function showAddTrain(){
        console.log($("#loginUserName").html());

        //公开的训练帐号,建模型时有上限
        var allModelNum = $("#modelsTable").bootstrapTable('getOptions').totalRows;
        if($("#loginUserName").html()!='admin' && allModelNum>=3){
            swal("", "测试账号最多只能添加三个模型，请删除后再新建！", "warning");
            return;
        }

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

        //todo 回到上一步
        $("#wizard").steps('previous');
        $("#wizard").steps('previous');

        $("#addTrainDiv").modal({
            backdrop:'static',//点击空白处不关闭对话框
            show:true
        });
    }

    $("#wizard").steps({
        headerTag: "h4",
        bodyTag: "div",
        enableContentCache: false,
        transitionEffect: "slideLeft",
        autoFucus: true,
        onStepChanging: function (e, currentIndex, priorIndex) {
            if (currentIndex > priorIndex)
                return true;
            if (priorIndex == 1) {
                var selectRow = $("#trainClassTable").bootstrapTable('getSelections');
                if(selectRow.length==0){
                    swal("", "请选择要训练的分类！", "warning");
                    return false;
                }
                if(numTrain==0){
                    swal("", "请添加分类图片！", "warning");
                    return false;
                }

                return true;
            }else if(priorIndex == 2){
                if($("#numShard").val()==''){
                    swal("", "请输入训练集分片数量！", "warning");
                    return false;
                }
                if($("#numValidation").val()==''){
                    swal("", "请输入验证集数量！", "warning");
                    return false;
                }
                if($("#trainStepNum").val()==''){
                    swal("", "请输入训练步数！", "warning");
                    return false;
                }

                return true;
            }
        },
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

    //选择分类
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

    //删除已选分类
    function delClass(classId,imgCount){
        $("#trainClassTable").bootstrapTable('remove', {
            field: 'id',
            values: [parseInt(classId)]
        });
        notClassId = notClassId.replace(classId + ",", "");

        numTrain -= imgCount;
        $("#numTrain").val(numTrain);
    }

    function submitTrain() {
        if($("#modelName").val()==''){
            swal("", "请输入模型名称！", "warning");
            return;
        }

        var selectRow = $("#trainClassTable").bootstrapTable('getSelections');
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
                'trainStepNum': $("#trainStepNum").val(),
                'modelName': $("#modelName").val(),
                'modelDes': $("#modelDes").val()
            },
            dataType: 'json',
            async: false,
            success: function (result) {
                if(result.code==100){
                    swal({   title: "",   text: "成功！",   timer: 2000 });
                    setTimeout(location.href = "${rc.contextPath}/models/models/modelList", 2000);
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