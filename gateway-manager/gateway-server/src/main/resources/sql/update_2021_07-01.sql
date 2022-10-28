ALTER TABLE `plugin_info`
    ADD COLUMN `git_domain` varchar(255) NULL DEFAULT 'git.n.xiaomi.com';
ALTER TABLE `gitlab_access_token`
    ADD COLUMN `domain` varchar(255) NULL DEFAULT 'git.n.xiaomi.com';