ALTER TABLE `tags`
ADD COLUMN `is_public`  int NULL DEFAULT 1 COMMENT '是否公开（1：是，2：否）' AFTER `user_id`;

