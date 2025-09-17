ALTER TABLE t_user ADD COLUMN token VARCHAR(64);

-- 添加内部账号字段
ALTER TABLE t_user ADD COLUMN internal_account VARCHAR(64) COMMENT '公司内部账号绑定';