<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8"/>
    <title>异常</title>
</head>
<body>
<div style="position: :absolute;width: 100%;height: 100%;z-index: :-1">
    <img src="../../images/back04.jpg" style="position: fixed;-webkit-filter: blur(6px);opacity: 0.5" width="100%" height="100%">
</div>
<div style="position: absolute;z-index: 10">
    <h1>Error</h1>

    <p><span>异常原因：${error}</span></p>
    <p><span>异常代码：${status}</span></p>
    <p><span>异常路径：${path}</span></p>
</div>
</body>
</html>