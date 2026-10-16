-- 创建会员套餐表
CREATE TABLE `member_package` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
  `level` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '会员等级：0-普通用户，1-高级会员',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `duration` INT(11) NOT NULL COMMENT '有效期（天数）',
  `description` VARCHAR(500) NOT NULL COMMENT '套餐描述',
  `features` TEXT COMMENT '包含功能（JSON格式）',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_level` (`level`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员套餐表';

-- 创建会员订单表
CREATE TABLE `member_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `package_id` BIGINT(20) NOT NULL COMMENT '套餐ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已取消',
  `payment_method` VARCHAR(20) COMMENT '支付方式',
  `payment_time` DATETIME COMMENT '支付时间',
  `expire_time` DATETIME COMMENT '过期时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_order_no` (`order_no`),
  INDEX `idx_member_id` (`member_id`),
  INDEX `idx_package_id` (`package_id`),
  INDEX `idx_status` (`status`),
  CONSTRAINT `fk_member_order_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_member_order_package` FOREIGN KEY (`package_id`) REFERENCES `member_package` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员订单表';

-- 插入默认会员套餐数据
INSERT INTO `member_package` (`name`, `level`, `price`, `duration`, `description`, `features`, `status`) VALUES
('高级会员月卡', 1, 19.90, 30, '高级会员月卡，享受30天高级会员权限', '{"features":["无限制语音识别","音频转换加速","优先处理","高级功能解锁"]}', 1),
('高级会员季卡', 1, 49.90, 90, '高级会员季卡，享受90天高级会员权限', '{"features":["无限制语音识别","音频转换加速","优先处理","高级功能解锁"]}', 1),
('高级会员年卡', 1, 199.00, 365, '高级会员年卡，享受365天高级会员权限', '{"features":["无限制语音识别","音频转换加速","优先处理","高级功能解锁","专属客服"]}', 1);
