#20170727
ALTER TABLE `models`
ADD COLUMN `show_rate_top`  float(5,2) NULL DEFAULT 0.7 AFTER `status`,
ADD COLUMN `show_rate_bottom`  float(5,2) NULL DEFAULT 0.3 AFTER `show_rate_top`;