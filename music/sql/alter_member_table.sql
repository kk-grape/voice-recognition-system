-- 修改member表结构，添加用户信息字段
ALTER TABLE `member`
ADD COLUMN `nickname` VARCHAR(50) NULL COMMENT '昵称' AFTER `name`,
ADD COLUMN `avatar` VARCHAR(255) NULL COMMENT '头像' AFTER `nickname`,
ADD COLUMN `gender` TINYINT(1) NULL COMMENT '性别：0-未知，1-男，2-女' AFTER `avatar`,
ADD COLUMN `birthday` DATE NULL COMMENT '生日' AFTER `gender`,
ADD COLUMN `email` VARCHAR(100) NULL COMMENT '邮箱' AFTER `birthday`,
ADD COLUMN `bio` VARCHAR(255) NULL COMMENT '个人简介' AFTER `email`,
ADD COLUMN `login_count` INT(11) NULL DEFAULT 0 COMMENT '登录次数' AFTER `bio`,
ADD COLUMN `last_login_at` DATETIME NULL COMMENT '最后登录时间' AFTER `login_count`,
ADD COLUMN `member_level` TINYINT(1) NULL DEFAULT 0 COMMENT '会员等级：0-普通用户，1-高级会员' AFTER `last_login_at`,
ADD COLUMN `member_expire_at` DATETIME NULL COMMENT '会员过期时间' AFTER `member_level`,
ADD COLUMN `member_remark` VARCHAR(255) NULL COMMENT '会员备注' AFTER `member_expire_at`;