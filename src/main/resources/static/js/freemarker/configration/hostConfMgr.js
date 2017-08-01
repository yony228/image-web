var $location = (window.location + '').split('/');
var $basePath = $location[3];

var setting = {
	data: {
		simpleData: {
			enable: true
		}
	},
	view: {
		addHoverDom: addHoverDom,
		removeHoverDom: removeHoverDom,
		selectedMulti: false
	},
	callback: {
		onClick: hostChoose,
		beforeRemove: beforeRemove

	},
	edit: {
		enable: true,
		showRemoveBtn: showRemoveBtn,
		showRenameBtn: false,
		removeTitle: "删除"
	}
}
alertify.defaults.notifier.delay=3;


function addHoverDom(treeId, treeNode) {
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0 || !treeNode.isParent)
		return;
	var addStr = "<span class='glyphicon glyphicon-plus' id='addBtn_" + treeNode.tId + "' title='添加' onfocus='this.blur();'></span>";
	sObj.after(addStr);
	var btn = $("#addBtn_" + treeNode.tId);
	if (btn)
		btn.bind("click", function () {
			prepareForAdd();
		});
};

function removeHoverDom(treeId, treeNode) {
	$("#addBtn_" + treeNode.tId).unbind().remove();
};

function beforeRemove(treeId, treeNode) {
	var jsonStr = {ip: treeNode.name};
	var rtFlag = false;
	$.ajax({
		async: false,
		cache: false,
		data: jsonStr,
		type: 'POST',
		dataType: 'json',
		url: "/" + $basePath + "/configuration/hostConf/delConfig.do",
		success: function (data) {
			rtFlag = true;
		},
		error: function (v1, v2, v3) {
			rtFlag = false;
		}
	});
	return rtFlag;
}

function showRemoveBtn(treeId, treeNode) {
	return !treeNode.isParent;
}

var zTree;
var treeNodes;

$(function () {
	$.ajax({
		async: true,
		cache: false,
		type: 'POST',
		dataType: 'json',
		url: "/" + $basePath + "/configuration/hostConf/getHosts.do",
		success: function (data) {
			zTree = $.fn.zTree.init($("#treeHosts"), setting, data);
			/*$("#treeHosts li span.button.remove").on("click",function () {
			 alert("是否删除？");

			 });*/

		},
		error: function (v1, v2, v3) {
		}
	});

});

$("#btn_submit_configuration").on("click", function () {
	var form = new FormData(document.getElementById("form_configuration"));
	$.ajax({
		url: "/" + $basePath + "/configuration/hostConf/createConfig.do",
		data: form,
		processData: false,
		contentType: false,
		type: 'POST',
		// dataType: 'json',
		error: function () {

		},
		success: function (data) {
			treeNodes = data;
		}
	});
});
/*更新配置*/
$("#btn_update_configuration").on("click", function () {
	var ip = $("#input_ip").val();
	var $error= $("span.error").length;
	var $rows= $("#showHost>.row"),myinfo = [], my_test = /[\s\_]/;$currentInput = $rows.last().find("input");
	var forLength= $rows.length;
	var app = $currentInput.eq(0).val(),type = $currentInput.eq(1).val(),	path = $currentInput.eq(2).val();

	if (app==""||type==""||path=="") {
		alertify.error("配置项不能为空");
		return false;
	} else if ($error >= 1) {
		alertify.error("请输入正确规范的配置项");
		return false;
	} else {
		for (var i=0; i <forLength;i++) {
			var myobj = new Object(),$inputs = $rows.eq(i).find("input");
			var config_app, config_filePath, config_type;
			config_app = $inputs.eq(0).val();
			config_type = $inputs.eq(1).val();
			config_filePath = $inputs.eq(2).val();
			myobj.app = config_app;
			myobj.filePath = config_filePath;
			myobj.type = config_type;
			myinfo.push(myobj);
		}
	}

	$.ajax({
		url: "/" + $basePath + "/configuration/hostConf/updateConfigV2.do",
		data: {
			ip: ip,
			info: JSON.stringify(myinfo)
		},

		type: 'post',
		error: function () {

		},
		success: function (data) {
			// treeNodes = data;
		}
	});
	return false

});

function hostChoose(event, treeId, treeNode) {
	$("#input_ip").val(treeNode.name);//delete
	$("#showHost>.row").remove();
	if (treeNode.isParent)
		return;
	$("#show_ip").show();
	var jsonStr = {ip: treeNode.name};
	console.log(jsonStr);
	$.ajax({
		url: "/" + $basePath + "/configuration/hostConf/getConfigV2.do",
		data: jsonStr,
		type: 'POST',
		error: function () {

		},
		success: function (data) {

			treeNodes = data;
			prepareForUpdate(treeNode.name, JSON.parse(data).info);
		}
	});
}

function prepareForAdd() {
	$("#input_ip").val('');
	$("#input_ip").attr("readonly", false);

	$("#input_content").val('');

	$("#btn_submit_configuration").show();

	$("#btn_update_configuration").hide();
}

/*生成域名、路径、类型输入框*/
function addInput(id_input, isPlus) {
	var $rows = $("<div class='row'></div>");
	var $app = $("<div class='col-sm-4'><label>应用名:</label></div>");
	var $type = $("<div class='col-sm-4'><label>类型:</label></div>");
	var $filepath = $("<div class='col-sm-4'><label>路径:</label></div>");
	var $appinput = $("<input  class='form-control myinput' name='app' id=app" + id_input + ">");
	var $typeinput = $("<input class='form-control myinput' name='type' id=type" + id_input + ">");
	var $fileinput = $("<input class='form-control myinput' name='filePath' id=filePath" + id_input + ">");
	var $minus = $("<span class='glyphicon-minus'></span>");
	var $plus = $("<span class='glyphicon-plus'></span>");
	$app.append($appinput);
	$type.append($typeinput);
	$filepath.append($fileinput);
	$rows.append($app).append($type).append($filepath);
	$minus.on("click", change_input);
	$plus.on("click", change_input);
	$appinput.on("blur", testInput);
	$typeinput.on("blur", testInput);
	$fileinput.on("blur", testInput);
	if (isPlus) {
		$rows.find("div").eq(2).append($plus);
	} else {
		$rows.find("div").eq(2).append($minus);
	}
	$("#showHost").append($rows);
}
/*失去焦点验证*/
function testInput() {
	var $inputName = $(this).attr("name");
	var myinput = /[\s\_]/;
	var $inputval = $(this).val();
	var $divs = $(this).parents(".row").find("div.col-sm-4");
	var $prevRows = $(this).parents(".row").siblings(".row");
	var for_length = $prevRows.length;
	var currentApp, currentType, currentPath;
	var currentFlag;
	var inputError = myinput.test($inputval) ? "true" : "false";
	$(this).parent(".col-sm-4").find(".error").remove();
	/*当前应用_类型值*/
	current_app_type = $divs.eq(0).find("input").val() + "_" + $divs.eq(1).find("input").val();

	/*应用名、类型判断是否重复*/
	if ($inputName !== "filePath") {
		for (var i = 0; i < for_length; i++) {
			var apptype = $prevRows.eq(i).find("input").eq(0).val() + "_" + $prevRows.eq(i).find("input").eq(1).val();
			if (current_app_type == apptype) {
				currentFlag = "repeat";
				break;
			}
		}
	}
	if ($inputval == "") {
		$(this).after("<span class='error'>不能为空</span>");
	} else if (inputError == "true") {
		$(this).after("<span class='error'>不能含有下划线或空格</span>");
	} else if (currentFlag == "repeat") {
		$(this).after("<span class='error'>应用名_类型不能重复</span>");
	} else if ($inputval !== "" && inputError == "false") {
		$(this).next("span.error").remove();
	}

}
function change_input() {
	var $this = $(this);
	var $className = $this.prop("className");
	$currentInput= $this.parents(".row").find("input");
	var app = $currentInput.eq(0).val(),
		type = $currentInput.eq(1).val(),
		path = $currentInput.eq(2).val();
	var $error = $("span.error").length;
	if ($className == "glyphicon-plus") {
		if (app == "" || type == "" || path == "") {
			alertify.error("配置项不能为空");
		} else if ($error >= 1) {
			alertify.error("请输入正确规范的配置项");
		} else {
			var addLength = $("#showHost>.row").length;
			addInput(addLength, true);
			$this.addClass("glyphicon-minus").removeClass("glyphicon-plus");
		}

	} else {
		$this.parents(".row").remove();
	}
}
/*查询配置*/
function prepareForUpdate(ip, content) {
	var ipdata = content, inputLength = 0, index;
	console.log(ipdata);
	$("#showHost>.row").remove();
	{
		for (var i in ipdata) {
			inputLength++;
			index=inputLength-1;
			addInput(index);
			var $rows=$("#showHost>.row").eq(index);
			$rows.find("input").eq(0).val(ipdata[i].app);
			$rows.find("input").eq(1).val(ipdata[i].type);
			$rows.find("input").eq(2).val(ipdata[i].filePath);
		}
	}
	addInput(index + 1, true);
	$("#input_ip").val(ip);
	$("#input_ip").attr("readonly", true);
}
var ztree_height = $("#host").height()-105;
$("#treeHosts,#form_configuration").css("height", ztree_height);
$("#form-group2").css("maxHeight", ztree_height-270);