#20170712 修改
ALTER TABLE `classifications`
MODIFY COLUMN `model_id`  int(11) NULL DEFAULT 0 AFTER `des`;

update classifications set model_id=0 where model_id is null;

#20170714 修改
ALTER TABLE `users`
ADD COLUMN `status`  int(2) NULL DEFAULT 0 COMMENT '状态（0:正常，1:禁用）' AFTER `password`;