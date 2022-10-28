alter table project_env_deploy_setting add labels varchar(200) default '';
alter table project_env add health_check_task_id int default 0;