ALTER TABLE `m78_bot_character_setting`
    ADD COLUMN `audio_config` json NULL COMMENT '语音配置' AFTER `streaming`;