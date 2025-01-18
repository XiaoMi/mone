ALTER TABLE m78_code_generation_info
    ALTER COLUMN class_name SET DEFAULT '';

ALTER TABLE m78_code_generation_info
    ADD COLUMN tier1 VARCHAR(128) COMMENT '一级部门';

ALTER TABLE m78_code_generation_info
    ADD COLUMN tier2 VARCHAR(128) COMMENT '二级部门';

ALTER TABLE m78_code_generation_info
    ADD COLUMN tier3 VARCHAR(128) COMMENT '三级部门';

ALTER TABLE m78_code_generation_info
    ADD COLUMN tier4 VARCHAR(128) COMMENT '四级部门';

ALTER TABLE m78_code_generation_info
    ADD COLUMN tier5 VARCHAR(128) COMMENT '五级部门';