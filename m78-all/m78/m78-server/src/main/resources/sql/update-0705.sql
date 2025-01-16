ALTER TABLE m78_code_generation_info
    ADD COLUMN plugin_version VARCHAR(256) COMMENT '插件版本';

ALTER TABLE m78_code_generation_info
    ADD COLUMN ip VARCHAR(256) COMMENT 'IP地址';

ALTER TABLE m78_code_generation_info
    ADD COLUMN system_version VARCHAR(256) COMMENT '系统版本';

ALTER TABLE m78_code_generation_info
    ADD COLUMN action TINYINT DEFAULT 2 COMMENT '操作类型 (1聊天 2生成代码 3代码建议 4生成注释 5智能命名 6一键push 7单元测试 8bug_fix)';

ALTER TABLE m78_code_generation_info
    ADD COLUMN ide_version VARCHAR(256) COMMENT 'IDE版本';