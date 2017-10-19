# image-web

image detect web site source

项目说明
  搜图系统主要是通过把图片进行分类管理，然后进行模型训练，生成相应的模型（生成模型根据图片的数量，训练集分片数量和训练步数不同而进行搜索的时候的精确度就会不同）。当该模型进行上线时，上传图片就能搜索出该上线模型相应的图片。
搜图系统主要有三类用户，第一类是非登录用户，该用户进入系统只能进行上线模型的相应图片搜索。第二类用户是登录用户，该用户可以搜索上线模型的相应图片，自己上传图片，进行标签管理，对自己上传的图片进行图片管理。第三类为管理员用户，该用户不仅可以搜索上线模型的相应图片，对自己上传的图片进行管理而且能根据图片分类进行模型管理，进行模型上线管理。

模块介绍
1.	图片搜索.
2.	图片上传.(普通用户特有)
3.	图片管理.
4.	分类(标签)管理.
5.	模型管理.(管理员用户特有)
6.	用户管理.(管理员用户特有)

代码结构
Image-web
|--doc 文档
|   |--sql 项目SQL语句
|
|--src.main.java.com.web
|   |--image 功能模块
|   |--ImageWebApplication项目启动文件
|
|--src.main.resources 项目前端页面及配置文件信息
|   |--static 第三方库、插件等静态资源
|   |--templates.freemarker 项目前端页面

所用技术
springBoot+bootStrap

本地部署方式
1.下载源码。
2. 创建数据库scenes_new，编码为UTF-8。
    执行doc/sql/里的文件，初始化数据。
    修改application.properties,更新MySQL帐号密码。

3.下载附件中的image-grpc-client.jar包。
  导入自己maven库，并引用。
4.安装nginx。
  修改file_web.properties，uploadUrl为nginx图片保存路径，picFullUrl为nginx路径。
5.安装预测服务,预测服务使用tensorflow-serving，如果使用ubuntu16.04可以直接下载编译好的可执行文件     。
   修改file_web.properties。
6.安装训练服务, 下载image-grpc-server训练代码并部署,python server.py。
   修改file_web.properties。
7.Eclipse、IDEA运行ImageWebApplication.java，则可启动项目。
   项目访部路径：http://localhost
   账号密码：client/123123
8.项目初始没有模型，需使用管理员用户训练并上线一个模型之后，图片搜索功能才能使用。
