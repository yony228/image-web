<!DOCTYPE html>
<html lang="zh" xmlns:c="http://www.w3.org/1999/html">
<head>
<#include "../head.ftl" />
    <title>训练详情</title>
    <style>
    </style>
</head>
<body>
<div style="position: :absolute;width: 100%;height: 100%;z-index: :-1">
    <img src="../../images/back04.jpg" style="position: fixed;-webkit-filter: blur(6px);opacity: 0.5" width="100%"
         height="100%">
</div>
<#include "../common.ftl" />
<div class="container" style="margin-top: 15px;margin-left:50px;position: absolute;">
    <div id="log-container">
        <div></div>
    </div>
</div>
</body>
<script>
    $(document).ready(function () {
//        var websocket = new WebSocket('ws://im.jg.115.mil/log');
        var websocket = new WebSocket('ws://localhost/log');
        websocket.onmessage = function (event) {
            $("#log-container div").append(event.data);
            $("#log-container").scrollTop($("#log-container").height() - $("#log-container").height());
        }
    });
</script>
</html>