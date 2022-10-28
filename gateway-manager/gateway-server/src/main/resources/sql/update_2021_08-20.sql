create table project_env_deploy_setting_limit
(
    id             bigint(64) auto_increment primary key,
    env_id         bigint(64) not null,
    cpu_limit      integer    not null default 8 COMMENT 'CPU最大上限',
    memory_limit   integer    not null default 32 COMMENT '内存最大上限',
    instance_limit integer    not null default 10 COMMENT 'docker实例数最大上限'
) charset = utf8 COMMENT '项目单个环境对应的物理配置上限';