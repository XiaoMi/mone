-- 扩展调用历史记录表，支持调用次数上报功能
-- 新增字段来替代 t_call_history_report 表

ALTER TABLE `t_invoke_history`
ADD COLUMN `app_name` VARCHAR(100) DEFAULT NULL COMMENT '应用名称' AFTER `relate_id`,
ADD COLUMN `business_name` VARCHAR(100) DEFAULT NULL COMMENT '业务名称' AFTER `app_name`,
ADD COLUMN `class_name` VARCHAR(255) DEFAULT NULL COMMENT '类名' AFTER `business_name`,
ADD COLUMN `method_name` VARCHAR(100) DEFAULT NULL COMMENT '方法名' AFTER `class_name`,
ADD COLUMN `description` VARCHAR(500) DEFAULT NULL COMMENT '方法描述' AFTER `method_name`,
ADD COLUMN `success` BOOLEAN DEFAULT TRUE COMMENT '是否成功' AFTER `outputs`,
ADD COLUMN `error_message` TEXT DEFAULT NULL COMMENT '错误信息' AFTER `success`,
ADD COLUMN `execution_time` BIGINT DEFAULT NULL COMMENT '执行耗时(毫秒)' AFTER `error_message`,
ADD COLUMN `host` VARCHAR(100) DEFAULT NULL COMMENT '主机名/IP' AFTER `execution_time`;

-- 添加索引以支持新的查询场景
CREATE INDEX `idx_app_name` ON `t_invoke_history`(`app_name`);
CREATE INDEX `idx_business_name` ON `t_invoke_history`(`business_name`);
CREATE INDEX `idx_class_name` ON `t_invoke_history`(`class_name`);
CREATE INDEX `idx_app_business` ON `t_invoke_history`(`app_name`, `business_name`);
CREATE INDEX `idx_success` ON `t_invoke_history`(`success`);

-- 说明：
-- 1. type 字段扩展使用：1-agent调用, 2-API调用上报, 3-其他
-- 2. inputs 字段用于存储 input_params (JSON格式)
-- 3. outputs 字段用于存储输出结果
-- 4. invoke_time 和 ctime 都保留，invoke_time 用于业务层记录调用时间

