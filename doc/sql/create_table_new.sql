/**建表**/
CREATE TABLE `authority` (
  `id` int(11) NOT NULL COMMENT 'id',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `desc` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限表';

CREATE TABLE `classifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `classification` varchar(100) DEFAULT NULL COMMENT '分类名称',
  `alias` varchar(200) DEFAULT NULL COMMENT '分类别名',
  `des` varchar(200) DEFAULT NULL COMMENT '分类描述',
  `create_user_id` int(11) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分类表';


CREATE TABLE `d_image_types` (
  `id` int(11) NOT NULL,
  `value` varchar(50) DEFAULT NULL,
  `des` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图片类型表';


CREATE TABLE `images` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `img_type` int(11) DEFAULT NULL COMMENT '图片类型（img_types字典）',
  `url` varchar(200) DEFAULT NULL COMMENT '图片存储路径',
  `upload_time` datetime DEFAULT NULL COMMENT '上传时间',
  `upload_user_id` int(11) DEFAULT NULL COMMENT '上传用户',
  `batch_no` varchar(50) DEFAULT NULL COMMENT '批次号',
  `bak` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图片表';

CREATE TABLE `models` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `des` varchar(200) DEFAULT NULL COMMENT '模型描述',
  `user_id` int(11) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` int(2) DEFAULT '1' COMMENT '状态（1.暂存，2.上线）',
  `show_rate_top` float(5,2) DEFAULT '0.70',
  `show_rate_bottom` float(5,2) DEFAULT '0.30',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='训练模型信息表';

CREATE TABLE `r_images_classifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `img_id` int(11) DEFAULT NULL COMMENT '图片标识符',
  `classification_id` int(11) DEFAULT NULL COMMENT '分类标识符',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图片分类关系表';

CREATE TABLE `r_images_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `img_id` int(11) DEFAULT NULL,
  `tag_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图片标签关系表';

CREATE TABLE `r_model_class` (
  `model_id` int(11) DEFAULT NULL COMMENT '模型id',
  `class_id` int(11) DEFAULT NULL COMMENT '分类id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `r_models_classifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(11) DEFAULT NULL COMMENT '模型标识符',
  `classification_id` int(11) DEFAULT NULL COMMENT '分类标识符',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模型分类关系表';

CREATE TABLE `r_roles_authority` (
  `role_id` int(11) NOT NULL DEFAULT '0' COMMENT '角色ID',
  `authority_id` int(11) NOT NULL DEFAULT '0' COMMENT '权限id',
  PRIMARY KEY (`role_id`,`authority_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色权限表';

CREATE TABLE `r_users_roles` (
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `role_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户权限表';

CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `desc` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='权限表';

CREATE TABLE `tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag` varchar(100) DEFAULT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `des` varchar(200) DEFAULT NULL COMMENT '标签描述',
  `user_id` int(11) DEFAULT NULL,
  `is_public` int(11) DEFAULT '1' COMMENT '是否公开（1：是，2：否）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='标签表';

CREATE TABLE `trains` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `train_no` varchar(20) DEFAULT NULL,
  `num_shard` int(11) DEFAULT '2' COMMENT '训练集分片数量',
  `num_validation` int(11) DEFAULT '20' COMMENT '验证集数量',
  `num_train` int(11) DEFAULT NULL COMMENT '训练图片数量',
  `train_step_num` int(11) DEFAULT '5000' COMMENT '训练步数',
  `status` int(2) DEFAULT NULL COMMENT '训练状态（-1.训练失败； 0.等待训练；1.训练中；2.训练完成）',
  `description` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `model_id` int(11) DEFAULT NULL,
  `model_url` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `desc` varchar(200) DEFAULT NULL COMMENT '描述',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `status` int(2) DEFAULT '0' COMMENT '状态（0:正常，1:禁用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';


/**初始化数据**/
INSERT INTO roles (name, `desc`) VALUES (1, '普通用户', null);
INSERT INTO roles (name, `desc`) VALUES (2, '训练用户', null);

INSERT INTO users (name, `desc`, password) VALUES ('client', null, 'Qpf0SxOVUjUkWySXOZ16kw==');
INSERT INTO users (name, `desc`, password) VALUES ('admin', null, 'Qpf0SxOVUjUkWySXOZ16kw==');

INSERT INTO r_users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO r_users_roles (user_id, role_id) VALUES (2, 2);

INSERT INTO d_image_types (id, value, des) VALUES (1, 'jpg', 'jpg格式');
INSERT INTO d_image_types (id, value, des) VALUES (2, 'png', 'png格式');
INSERT INTO d_image_types (id, value, des) VALUES (3, 'bmp', 'bmp格式');