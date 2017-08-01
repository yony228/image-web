var $location = (window.location + '').split('/');
var $basePath = $location[3];
var startime, endtime, ip,myip, app,myapp, type,mytype, key, mylimit, key,mysearch_data,$search_table = $("#search_table"),search;

var more_page = 0;
$(document).ready(function () {

	alertify.defaults.transition = 'slide';
	alertify.defaults.notifier.delay=2;
	alertify.defaults.glossary.title = "";
	alertify.defaults.theme.ok = "btn btn-primary";
	alertify.defaults.theme.cancel = "btn btn-danger";
	/*时间选择*/
	$(".select_time").datetimepicker();
	Date.prototype.Format = function (fmt) {
		var o = {
			"M+": this.getMonth() + 1,
			"d+": this.getDate(),
			"h+": this.getHours(),
			"m+": this.getMinutes(),
			"s+": this.getSeconds(),
			"q+": Math.floor((this.getMonth() + 3) / 3),
			"S": this.getMilliseconds()
		};
		if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substring(4 - RegExp.$1.length));
		for (var k in o)
			if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substring(("" + o[k]).length)));
		return fmt;
	};
	//设置默认搜索时间
	$("#start_time").val(new Date().Format("yyyy/MM/dd 00:00"));
	$("#end_time").val(new Date().Format("yyyy/MM/dd hh:mm"));
	/*查询域名,下拉框显示*/
	$.ajax({
		type: "post",
		url: "/" + $basePath + "/log/history/queryDomains",
		data: {},
		dataType: "json",
		success: function (ip_data) {
			var $ul= $("#ip"), items= ip_data.rows,i;
			for (i=0; i< ip_data.total; i++) {
				$ul.append("<option>"+items[i].key+"</option>");
			}
			/*默认显示第一条*/

			$("#ip").selectpicker("refresh");
			$("#ip").selectpicker("render");
		}
	});

	/*查询应用名,下拉框显示*/
	$.ajax({
		type: "post",
		url: "/" + $basePath + "/log/history/queryApps",
		data: {},
		dataType: "json",
		success: function (app_data) {
			var $ul= $("#app"),  items= app_data.rows;
			for (var i=0; i < app_data.total; i++) {
				$ul.append("<option>"+items[i].key+"</option>");
			}
			$("#app").selectpicker("refresh");
			$("#app").selectpicker("render");
		}
	});
	/*查询类型,下拉框显示*/
	$.ajax({
		type: "post",
		url: "/" + $basePath + "/log/history/queryTypes",
		data: {},
		dataType: "json",
		success: function (type_data) {
			var $ul= $("#type"),  items= type_data.rows;
			for (var i=0; i < type_data.total; i++) {
				$ul.append("<option>"+items[i].key+"</option>");
			}
			$("#type").selectpicker("refresh");
			$("#type").selectpicker("render");
		}
	});
	/*界面初始化显示搜索信息*/
	search = function () {
		myip="";
		start_time = $("#start_time").val();
		end_time = $("#end_time").val();
	    ip=$("#ip").selectpicker("val");
		if(!(ip==null||ip==undefined)){
			for(var i=0;i<ip.length;i++){
			 if(i<ip.length-1){
			 myip+=ip[i]+",";

			 }else{
			 myip+=ip[i];
			 }
			 }
		}
		myapp="";
        app= $("#app").selectpicker("val");
		if(!(app==null||app==undefined)){

			for(var i=0;i<app.length;i++){
				if(i<app.length-1){
					myapp+=app[i]+",";

				}else{
					myapp+=app[i];
				}
			}
		}
		mytype="";
        type=$("#type").selectpicker("val");
		if(!(type==null||type==undefined)){
			for(var i=0;i<type.length;i++){
				if(i<type.length-1){
					mytype+=type[i]+",";

				}else{
					mytype+=type[i];
				}
			}
		}
		key= $("#mysearch1").val();
		mylimit = 10;
		startime = Date.parse(new Date(start_time));
		endtime = Date.parse(new Date(end_time));
		$("#sub_tooltip").text("");
		more_page = 0;
		if (!start_time == "" & !end_time == "") {
			//展现文本
			$.ajax({
				type: "get",
				url: "/" + $basePath + "/log/history/queryLogs",
				data: {
					"startTime": startime,
					"endTime": endtime,
					"ip":myip,
					"app":myapp,
					"type":mytype,
					"key": key,
					"offset":more_page,
					"limit":mylimit
				},
				dataType: "json",
				beforeSend: function () {
					$("#loading").show();
				},
				success: function (search_data) {
					$("#list_message li").remove();
					$search_table.bootstrapTable("removeAll")
					if (search_data.total !== 0) {
						mysearch_data=search_data.rows;
						/*搜索信息列表*/
						show_listsearch();
						/*搜索信息表格*/
						show_tabsearch();
						$("#more_message").show();
					} else {
						$("#more_message").hide();
					/*$("#showLogo").modal("show");*/
						alertify.success("抱歉，没有数据");
					}
				},
				complete: function () {
					$("#loading").hide();
				}
			});

		} else {
			$("#sub_tooltip").text("请输入查询时间");
		}
	};
	search();
	$search_table.bootstrapTable({
		url: "/" + $basePath + "/log/history/queryLogs",
		queryParams: function (params) {
			params['startTime'] =startime
			params['endTime'] = endtime;
			params['ip']=myip;
			params['app']=myapp;
			params['type']=mytype ;
			params['key']=key;
			return params;
		},
		striped: true,
		columns: [
			{field: 'ip', title: '域名'},
			{field: 'app', title: '应用名'},
			{field: 'type', title: '日志类型'},
			{field: 'payload', title: '日志内容'},
			{field: 'timestamp', title: '时间', formatter: timeFormatter}
		]
	});
	//时间解析
	function timeFormatter(value, row, index) {
		return new Date(Number(value)).Format("yyyy-MM-dd hh:mm:ss");
	}

	/*左右伸缩隐藏侧栏*/
	$("#change_width").click(function () {
		var flag = $(this).hasClass("glyphicon-step-backward")?"true":"false";
        var $this=$(this);
		if (flag == "true") {
			    $(".searchField").animate({left:"-15%"},800, function(){
                    $this.addClass("glyphicon-step-forward").removeClass("glyphicon-step-backward");
			});
            $(".searchResult").animate({left:"0",width:"100%"},800);

		} else {
			$(".searchField").animate({left:"0"},800,function () {
                $this.addClass("glyphicon-step-backward").removeClass("glyphicon-step-forward");
			});
            $(".searchResult").animate({left:"15%",width:"85%"},800);

        }
	});

	/* 切换搜索信息显示方式*/
	$("#search_tab li").click(function () {
		var $index = $(this).index();
		$(this).addClass("click_li").siblings().removeClass("click_li");
		if ($index == 0) {
			$("#tab_message").show().siblings().hide();

		} else {
			$("#list_message").show().siblings().hide();
		}
	});
	/*下拉框伸缩显示*/
	$(".show_div").click(function () {
		var $nonediv = $(this).parent("div").next("div");
		var $nonediv_dis = $nonediv.css("display");
		if ($nonediv_dis == "none") {
			$nonediv.slideDown();
			$(this).addClass("glyphicon-minus").removeClass("glyphicon-plus");
			search();
		} else {
			$nonediv.slideUp();
			$(this).addClass("glyphicon-plus").removeClass("glyphicon-minus");
		    $nonediv.find("select").val("");
			search();
		}
	});
    /*域名、应用名、类型下来框选择执行搜索*/
    $("#ip").on("change",search);
    $("#app").on("change",search);
    $("#type").on("change",search);
	$("#start_time,#end_time").on("blur",search);
	/*回车键执行搜索*/
	$("#mysearch1").on("keydown", enter_search);
	/*点击搜索图标执行搜索*/
	$("#enter_search1").on("click", search);


	function enter_search(event) {
		var event = event || window.event;
		if (event.keyCode == "13"){
			search();
		}
	}
	/*列表显示搜索信息*/
	function show_listsearch() {
		for (var i in mysearch_data) {
			$("#list_message ul").append("<li>" + mysearch_data[i].payload + "</li>");
		}
	}

	/*表格显示搜索信息*/
	function show_tabsearch() {
		$search_table.bootstrapTable("refresh", {
			url: "http://localhost:8080/" + $basePath + "/log/history/queryLogs",
			query: {
				ip: myip,
				app: myapp,
				type:mytype,
				startTime:startime,
				endTime: endtime,
				key: key}
		});
	}

	/*点击more后续加载*/
	$("#more_message").on("click",more_search);
	function more_search() {
		more_page++;
		var more_offset = more_page*mylimit;
		$.ajax({
			type: "get",
			url: "/" + $basePath + "/log/history/queryLogs",
			data: {
				"startTime":startime,
				"endTime":endtime,
				"ip":myip,
				"app":myapp,
				"type":mytype,
				"key":key,
				"offset": more_offset,
				"limit": mylimit
			},
			dataType: "json",
			success: function (more_data) {
				var more_search = more_data.rows;


				if (more_search.length>0) {
					for (var i = 0; i < more_search.length; i++) {
						$("#list_message ul").append("<li>" + more_search[i].payload + "</li>");
					}
				} else {
					if($("#search_end").length<=0){
						$("#list_message ul").append("<li id='search_end'>" + "是不是数据已加载完毕" + "</li>");
						$("#more_message").hide();
					}
				}
			}
		});
	}
	$(window).resize(function () {
		$search_table.bootstrapTable('resetView');


	});
	function tab_responseHandler(res) {
		$.each(res.rows, function (i, row) {
			row.state = $.inArray(row.jobId, selections) !== -1;
		});
		return res;
	}

	//表格行详细信息
	function detailFormatter(index, row) {
		var html = [];
		$.each(row, function (key, value) {
			html.push('<p><b>' + key + ':</b> ' + value + '</p>');
		});
		return html.join('');
	}
	/*多选下拉框*/
	$(".selectpicker").selectpicker();

	//下载
	$("#span_download").on("click", function () {
		location.href = "/" + $basePath + "/log/history/download?"
			+"startTime=" +  Date.parse(new Date($("#start_time").val()).toString())
			+ "&" + "endTime=" + Date.parse(new Date($("#end_time").val()).toString())
			+ "&" + "ip=" +  ($("#ip").parent().is(":hidden")?"":$("#ip").val())
			+ "&" + "app=" +  ($("#app").parent().is(":hidden")?"":$("#app").val())
			+ "&" + "type=" +  ($("#type").parent().is(":hidden")?"":$("#type").val())
			+ "&" + "key=" +  ($("#mysearch1").val());
	});

	var $box_height = $("#search_body").height();
	var $search_information_height = $box_height-145;
	$(".searchField,.searchResult").css("height", $box_height);
	$(".search_information").css("height", $search_information_height);

	$(window).resize(function () {
		var $resize_box_height = $("#search_body").height();
		var $resize_search_information_height = $box_height - 145;
		$(".searchField,.searchResult").css("height", $resize_box_height);
		$(".search_information").css("height", $resize_search_information_height);
	});

});
