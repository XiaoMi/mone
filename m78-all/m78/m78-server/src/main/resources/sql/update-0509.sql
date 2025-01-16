-- 用户表
CREATE TABLE m78_im_users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) ,
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 好友关系表
CREATE TABLE m78_im_friendships (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id1 INT NOT NULL,
    user_id2 INT NOT NULL,
    status int default 0 comment '0 未通过 1 通过 2 拒绝',
    create_time TIMESTAMP,
    FOREIGN KEY (user_id1) REFERENCES m78_im_users(id),
    FOREIGN KEY (user_id2) REFERENCES m78_im_users(id)
);

-- 消息表
CREATE TABLE m78_im_messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    app_id INT NOT NULL,
    status INT,
    type INT,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message TEXT NOT NULL,
    sent_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES m78_im_users(id),
    FOREIGN KEY (receiver_id) REFERENCES m78_im_users(id),
    INDEX (app_id)
);


ALTER TABLE `m78_im_users` ADD `app_id` INT;
CREATE INDEX idx_app_id ON m78_im_users(app_id);

ALTER TABLE `m78_im_friendships` ADD `app_id` INT;
CREATE INDEX idx_app_id ON m78_im_friendships(app_id);

ALTER TABLE `m78_user_login` ADD `app_id` INT;
ALTER TABLE `m78_user_login` ADD `last_login_time` BIGINT;
CREATE INDEX idx_app_id ON m78_user_login(app_id);