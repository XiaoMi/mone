ALTER TABLE m78_chat_topics ADD type int DEFAULT 0 NOT NULL COMMENT '0:ai chat,1:m78 bot debug,2:m78 bot chat,3:app bot chat';

ALTER TABLE m78_chat_topics ADD app_id int DEFAULT 0;
CREATE INDEX idx_app_id ON m78_chat_topics(app_id);

-----
ALTER TABLE m78_bot ADD app_id int DEFAULT 0;
CREATE INDEX idx_app_id ON m78_bot(app_id);

ALTER TABLE m78_bot_character_setting ADD `dialogue_timeout` int(11) DEFAULT NULL COMMENT '对话超时时间(ms)';




CREATE TABLE `m78_long_term_chat_summary`
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    app_id INT DEFAULT 0,
    bot_id INT NOT NULL,
    username varchar(255) DEFAULT NULL,
    summary TEXT NOT NULL,
    positive int(1) COMMENT '是否是正面的',
    priority int(1) COMMENT '重要程度 0-10',
    deleted  int(1) DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    ctime  BIGINT(20) DEFAULT NULL,
    KEY  `idx_bot_id` (`bot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_short_term_chat_summary`
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    app_id INT DEFAULT 0,
    bot_id INT NOT NULL,
    username varchar(255) DEFAULT NULL,
    summary TEXT NOT NULL,
    positive int(1) COMMENT '是否是正面的',
    priority int(1) COMMENT '重要程度 0-10',
    deleted  int(1) DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    ctime  BIGINT(20) DEFAULT NULL,
    utime  BIGINT(20) DEFAULT NULL,
    expire_time  BIGINT(20) DEFAULT NULL,
    KEY  `idx_bot_id` (`bot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `m78_bot_fsm`
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    app_id INT DEFAULT 0,
    bot_id INT NOT NULL,
    username varchar(255) DEFAULT NULL,
    state INT NOT NULL,
    meta json,
    ctime  BIGINT(20) DEFAULT NULL,
    utime  BIGINT(20) DEFAULT NULL,
    KEY  `idx_bot_id` (`bot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;