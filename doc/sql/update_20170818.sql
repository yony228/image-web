CREATE TABLE r_model_class
(
    model_id INT(11) COMMENT '模型id',
    class_id INT(11) COMMENT '分类id'
);

drop table train_class;

ALTER TABLE `classifications`
ADD COLUMN `create_user_id`  int(11) NULL COMMENT '创建人' AFTER `model_id`,
ADD COLUMN `create_time`  datetime NULL COMMENT '创建时间' AFTER `create_user_id`;

alter table classifications drop column model_id;
update classifications set create_user_id=2,create_time=now();


//TODO 清理数据
truncate table trains;
truncate table models;