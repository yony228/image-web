
/*设置属性*/
var setting = {
    isSimpleData: true,
    treeNodeKey: "id",
    treeNodeParenKey: "pId",
    showLine: true,
    checkable: true
};
var ztreeobj;
/*模拟Json数据*/
var treeNodes = [
    {
        name: "m1", open: true, children: [
        {
            name: "m11", open: true, children: [
            {name: "m111"}, {name: "tes1pdpi"}
        ]
        }, {name: "login12"}]
    },
    {
        name: "test2", open: true, children: [
        {
            name: "test21", children: []
        }, {name: "test22"}]
    }

];
/*显示树形菜单*/
$(function () {
    ztreeobj = $.fn.zTree.init($("#tree"), setting, treeNodes);
});
$(function () {
    /*点击树形最底层菜单显示对象日志*/
    $("#tree li span.button.ico_docu").next().click(function () {
        /* 判断是否为树形菜单第二次点击*/
        var clickstatus = $(this).attr("status");
        if (clickstatus == undefined) {
            var treeid = $(this).attr("status", "click");
            /*限制最多显示表格菜单数*/
            var maxtitle = $(".log_title li").length;
            if (maxtitle==9) {
                alert("抱歉，超出显示菜单数。请关闭不必要的菜单。")
            } else {
                $(".tab").show();/*显示文本框*/
                $(".title_box").addClass("border_title_box");/*添加弧形分割线*/
                var $thistext = $(this).text();/*获取显示日志标题*/
                var $closer = $("<span class='closer glyphicon glyphicon-remove'></span>");/*创建关闭按钮*/
                var $text = $("<span class='tit_text'></span>").text($thistext);/*显示标题*/
                var $li = $("<li>");
                $li.append($text).append($closer);
                $(".log_title").append($li);
                $closer.bind('click',closetab);
                $li.bind('click', swichtab);
                $(".log_text li").hide();
                $(".log_text").append($("<li>"+"<marquee direction='up' behavior='slide'>"+$thistext+"</marquee>"+"</li>"));
              /*  var i=0;
                var s=document.getElementById("log_text");
               timer=setInterval(function () {
                    s.lastChild.innerHTML=$thistext.substring(0,i);
                    i++;
                   if(s.lastChild.innerHTML==$thistext){
                       clearInterval(timer);
                   }
               },300)*/
            }
        }
    })
    /* 表格切换*/
    function swichtab() {
        var titindex = $(this).index();
        $(this).addClass("active").siblings().removeClass("active");
        $(".log_text li").eq(titindex).show().siblings().hide();
    }
    /* 删除表格*/
    function closetab() {
        var delet_index = $(this).parent().index();
        console.log(delet_index);
        $(".log_title li").eq(delet_index).remove();
        /*删除标题*/
        $(".log_title li").eq(0).addClass("active");
        /*默认显示第一个标题*/
        $(".log_text li").eq(delet_index).remove().siblings().show();
        $(".log_text li").eq(0).show().siblings().hide();
        if (!$(".log_title li").length) {
            $(".tab").hide();
        }
    }
});
