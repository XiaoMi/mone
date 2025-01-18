ALTER TABLE m78_code_generation_info
ADD COLUMN type TINYINT DEFAULT 1 COMMENT '生成代码的来源, 1是根据注释生成代码, 2是inlay提示生成代码';

ALTER TABLE m78_code_generation_info
ADD COLUMN annotation text COMMENT '生成代码时的注释';

ALTER TABLE m78_code_generation_info
ADD COLUMN source TINYINT DEFAULT 1 COMMENT '生成代码的来源, 1是idea, 2是vscode';