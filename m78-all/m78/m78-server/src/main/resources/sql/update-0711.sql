ALTER TABLE m78_code_generation_info
    ADD COLUMN week_of_year VARCHAR(4) COMMENT '周，一年中的第几周';

ALTER TABLE m78_code_generation_info
    ADD COLUMN day VARCHAR(30) COMMENT '天，字符串，方便统计';