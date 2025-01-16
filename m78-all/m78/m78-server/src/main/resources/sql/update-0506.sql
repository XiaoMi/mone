CREATE INDEX idx_publish_status_deleted ON m78_bot(publish_status, deleted);
ALTER TABLE `m78_bot` ADD INDEX `idx_bot_avg_star` (`bot_avg_star` DESC);