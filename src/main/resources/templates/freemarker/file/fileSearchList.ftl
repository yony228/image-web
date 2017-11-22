<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <link href="${rc.contextPath}/viewerDist/viewer.css" rel="stylesheet" type="text/css">
    <script src="${rc.contextPath}/viewerDist/viewer.js"></script>
    <script src="${rc.contextPath}/js/masonry-docs.min.js"></script>
	<title>文件搜索</title>
    <style>
        /*.container-fluid {*/
            /*padding: 20px;*/
        /*}*/

		.box{
			margin-bottom: 5px;
			float: left;
			width: 200px;
		}

		.box img{
			max-width: 100%;
		}
	</style>
</head>
<body id="fater-body">
<#include "../common.ftl" />
<div class="container" style="margin-left: 140px;">
	<form id="searchForm" class="form-horizontal" role="form" action="${rc.contextPath}/fileSearch/pic/searchList"
	      method="post" enctype="multipart/form-data" onkeydown="if(event.keyCode==13)return false;">
		<div class="row">
			<div class="col-md-1" style="padding: 0px; transform: translateY(-20px);<#if Session["userInfo"]??>display: none;</#if> ">
				<img src="../../images/logoko.png" style="width: 100px;">
			</div>
			<!--请输入关键字:-->
			<div class="col-md-6">
				<input name="keyName" type="text" id="keyName" class="form-control" value="${keyName!}" placeholder="请选择图片进行搜索">
                <span class="upload"></span>
                <input style="display: none" name="file" type="file" id="file" class="form-control" accept=".jpg" onchange="checkForm()">
			</div>
			<div class="col-md-4" style="display: none">
				<button type="button" onclick="checkForm()" class="btn btn-primary submit1">搜索</button>
			</div>
		</div>
	</form>
	<br>
	<div class="row" style="position:absolute;height: 100px;" id="showLabelDiv">
	<#if returnFiles?size==0>
		查无结果
	</#if>
	<#if keyNameList??>
		<#list keyNameList as keyName>
			&nbsp;&nbsp;<a style="color: #333" href="#" onclick="queryPicByKeyName('${keyName.classification}')">${keyName.description}(${keyName.countImages})</a>
		</#list>
	</#if>
	</div>
	<br><br>
	<div id="masonry" class="container-fluid">
	<#if returnFiles??>
		<#list returnFiles as returnFile>
			<div class="box">
				<img data-original="${fileUrl}${returnFile}_1000x1000" src="${fileUrl}${returnFile}_200x200" onerror="this.src='../../images/invalid.jpeg'">
			</div>
		</#list>
	</#if>
	</div>
    <input type="hidden" id="nowPage" name="nowPage" value="${nowPage!}">
    <input type="hidden" id="maxPage" name="maxPage" value="${maxPage!}">
    <input type="hidden" id="returnStr" name="returnStr" value="${returnStr!}">
</div>
<div id="uploading">正在搜索……</div>
<script>
	$(window).scroll(function (event) {
		var scrollTop = $(window).scrollTop();
        var viewH = $(window).height();
        var docH = $(document).height();
		var bot = 5;

        if ((bot + scrollTop) >= (docH - viewH)) {
        	var nowPage = $("#nowPage").val() * 1 + 1;
            console.log("当前页："+nowPage);

            var maxPage = $("#maxPage").val() * 1;
            if (nowPage > maxPage) {
                return;
            }

            var returnStr = $("#returnStr").val();

            var fileUrl = '${fileUrl!}';
            $.ajax({
                type: 'post',
                url: "${rc.contextPath}/fileSearch/queryPicPage",
                data: {
                	'nowPage': nowPage,
					'returnStr': returnStr
				},
                dataType: 'json',
                async: false,
                success: function (result) {
                    var str = "";
                    var templ = '<div class="box" name="'+nowPage+'"><img src="{{src}}_200x200" data-original="{{src}}_1000x1000" onerror=this.src="../../images/invalid.jpeg"></div>';
                    var re = new RegExp("{{src}}","g");//g是全部替换，t是替换第一个
                    for (var i = 0; i < result.data.length; i++) {
                        str += templ.replace(re , fileUrl + result.data[i]);
                    }
                    $("#masonry").append(str);

					//页面重新布局
                    $("#masonry").imagesLoaded(function () {
                   		 $("#masonry").masonry('appended',$('#masonry div[name="'+nowPage+'"]'), true);
                    });

                    $("#masonry").viewer('destroy');
                    $("#masonry").viewer({url:'data-original'});

                    $("#nowPage").val(nowPage);
                },
                error: function () {
                    alert("系统错误，请联系管理员！");
                }
            });
        }
    });


	//图片预览效果
	$("#masonry").viewer({url:'data-original'});

	$(function () {
		$("#fileSearch").css("color", "#d0cb16");

        $("#masonry").imagesLoaded(function () {
			$("#masonry").masonry({
	//            itemSelector:'.box',
				gutter:5,
			});
        });
	});

    function checkForm() {
        if($("#keyName").val()=='' && $("#file").val() == ''){
            //swal("提示!", "请输入文字或上传图片", "warning");
            return;
        }else if ($("#file").val() == '') {
            $("#searchForm").attr("action","${rc.contextPath}/fileSearch/key/searchList");
        }else {
            $("#searchForm").attr("action","${rc.contextPath}/fileSearch/pic/searchList");
		}

        $("#uploading").show().delay(5000);
        $("#searchForm").submit();
    }

	function queryPicByKeyName(keyName) {
		var fileUrl = '${fileUrl!}';
		$.ajax({
			type: 'post',
			url: "${rc.contextPath}/fileSearch/queryPicByKeyName",
			data: {'keyName': keyName},
			dataType: 'json',
			async: false,
			success: function (result) {
				var str = "";
				var templ = '<div class="box"><img src="{{src}}_200x200" data-original="{{src}}_1000x1000" onerror=this.src="../../images/invalid.jpeg"></div>';
				var re = new RegExp("{{src}}","g");//g是全部替换，t是替换第一个
                for (var i = 0; i < result.data.length; i++) {
					str += templ.replace(re , fileUrl + result.data[i]);
				}
				$("#masonry").html(str);
                $("#masonry").masonry('destroy');
                $("#masonry").imagesLoaded(function () {
                    $("#masonry").masonry({
                        gutter: 5,
                    });
                });

                $("#masonry").viewer('destroy');
                $("#masonry").viewer({url:'data-original'});

				$("#returnStr").val(keyName);
                $("#nowPage").val(1);
                $("#maxPage").val(result.maxPage);
			},
			error: function () {
				alert("系统错误，请联系管理员！");
			}
		});
	}

    //文件上传伸缩面板
    $(".upload").on("click", upload);
    function upload() {
        $("#file").trigger('click');
    }
</script>
</body>
</html>