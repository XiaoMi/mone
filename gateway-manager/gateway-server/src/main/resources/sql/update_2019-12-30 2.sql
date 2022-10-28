alter table project_env_deploy_setting add max_replicate bigint DEFAULT 0;
alter table project_env add last_auto_scale_time bigint DEFAULT 0;
