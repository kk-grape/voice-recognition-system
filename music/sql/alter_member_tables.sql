-- 修改会员套餐表，将TINYINT类型改为INT类型
ALTER TABLE `member_package` MODIFY COLUMN `level` INT(11) NOT NULL DEFAULT 0 COMMENT '会员等级：0-普通用户，1-高级会员';
ALTER TABLE `member_package` MODIFY COLUMN `status` INT(11) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用';

-- 修改会员订单表，将TINYINT类型改为INT类型
ALTER TABLE `member_order` MODIFY COLUMN `status` INT(11) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已取消';

-- 修改会员表，确保相关字段也是INT类型
ALTER TABLE `member` MODIFY COLUMN `gender` INT(11) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女';
ALTER TABLE `member` MODIFY COLUMN `member_level` INT(11) DEFAULT 0 COMMENT '会员等级：0-普通用户，1-高级会员';
