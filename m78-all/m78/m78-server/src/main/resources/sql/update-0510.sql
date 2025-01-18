ALTER TABLE m78_bot_db_table
    ADD type int DEFAULT 0 NOT NULL COMMENT '表类型(0:内部表,1:外部表)';

ALTER TABLE m78_bot_db_table
    ADD connection_id bigint NULL COMMENT '外部表连接信息id';