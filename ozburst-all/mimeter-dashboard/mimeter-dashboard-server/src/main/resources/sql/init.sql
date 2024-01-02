-- table: 数据源 --
CREATE TABLE `agent_apply_info`
(
    `id`             int(32) NOT NULL AUTO_INCREMENT COMMENT '唯一id',
    `apply_user`     varchar(255) NOT NULL DEFAULT '' COMMENT '申请人',
    `apply_org_id`   varchar(255) NOT NULL DEFAULT '' COMMENT '申请人组织id',
    `apply_org_name` varchar(255) NOT NULL DEFAULT '' COMMENT '申请人组织中文名',
    `agent_ip`       varchar(255) NOT NULL DEFAULT '' COMMENT '申请的发压机ip',
    `agent_hostname` varchar(255) NOT NULL DEFAULT '' COMMENT '申请的发压机的hostname',
    `apply_status`   int(11) NOT NULL DEFAULT '0' COMMENT '申请状态 0：待审核 1：审核完成',
    `ctime`          bigint(64) NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30001 COMMENT='压测机申请记录表';

CREATE TABLE `agent_info`
(
    `id`          int(32) NOT NULL AUTO_INCREMENT COMMENT '压测机id',
    `server_name` varchar(255)          DEFAULT NULL COMMENT '服务名',
    `ip`          varchar(255) NOT NULL COMMENT '压测机ip',
    `port`        int(32) DEFAULT NULL COMMENT '压测机端口',
    `cpu`         int(32) DEFAULT NULL COMMENT '压测机总cpu',
    `mem`         bigint(64) DEFAULT NULL COMMENT '压测机总内存',
    `use_cpu`     int(32) DEFAULT NULL COMMENT '已使用的cpu数',
    `use_mem`     bigint(20) DEFAULT NULL COMMENT '已使用的内存数',
    `hostname`    varchar(255)          DEFAULT NULL COMMENT '主机名',
    `client_desc` varchar(255)          DEFAULT NULL COMMENT '描述',
    `ctime`       bigint(64) DEFAULT NULL COMMENT '创建时间',
    `utime`       bigint(64) DEFAULT NULL COMMENT '更新时间',
    `enable`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否可用',
    `node_ip`     varchar(255)          DEFAULT NULL COMMENT '压测机所属宿主机ip',
    `tenant`      varchar(255)          DEFAULT NULL COMMENT '租户信息',
    `tenant_cn`   varchar(255) NOT NULL DEFAULT '' COMMENT '租户，中文名',
    `domain_conf` json                  DEFAULT NULL COMMENT '域名绑定',
    PRIMARY KEY (`id`),
    UNIQUE KEY `agent_ip_uindex` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=60001;

CREATE TABLE `checkpoint_info`
(
    `id`              int(32) NOT NULL AUTO_INCREMENT,
    `check_type`      int(11) DEFAULT NULL COMMENT '检查点类型',
    `check_obj`       varchar(255) DEFAULT NULL COMMENT '检查对象',
    `check_condition` int(11) DEFAULT NULL COMMENT '检查条件',
    `check_content`   text         DEFAULT NULL COMMENT '检查内容',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=60001;

CREATE TABLE `dataset`
(
    `id`                 int(32) NOT NULL AUTO_INCREMENT,
    `name`               varchar(128)  DEFAULT NULL,
    `note`               varchar(128)  DEFAULT NULL,
    `type`               int(32) DEFAULT NULL,
    `default_param_name` varchar(128)  DEFAULT NULL,
    `ignore_first_row`   int(32) DEFAULT '0',
    `file_name`          varchar(128)  DEFAULT NULL,
    `file_url`           varchar(1024) DEFAULT NULL,
    `file_ks_key`        varchar(256)  DEFAULT NULL,
    `file_rows`          bigint(64) DEFAULT NULL,
    `file_size`          bigint(64) DEFAULT NULL,
    `preview_file_rows`  json          DEFAULT NULL,
    `header`             json          DEFAULT NULL,
    `interface_url`      varchar(256)  DEFAULT NULL,
    `traffic_record_id`  int(32) DEFAULT NULL,
    `ctime`              bigint(64) DEFAULT NULL,
    `utime`              bigint(64) DEFAULT NULL,
    `creator`            varchar(128)  DEFAULT NULL,
    `updater`            varchar(128)  DEFAULT NULL,
    `tenant`             varchar(255)  DEFAULT NULL COMMENT '租户信息',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=60003;

CREATE TABLE `dataset_scene_relation`
(
    `id`         int(32) NOT NULL AUTO_INCREMENT,
    `dataset_id` int(32) DEFAULT NULL,
    `scene_id`   int(32) NOT NULL DEFAULT '0',
    `ctime`      bigint(64) DEFAULT NULL,
    `utime`      bigint(64) DEFAULT NULL,
    `creator`    varchar(128) DEFAULT NULL,
    `updater`    varchar(128) DEFAULT NULL,
    `enable`     tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否真正引用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `dataset_id` (`dataset_id`,`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=60001;

CREATE TABLE `domain_apply_info`
(
    `id`            int(32) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `apply_user`    varchar(255) NOT NULL DEFAULT '' COMMENT '申请人',
    `domain`        varchar(255) NOT NULL DEFAULT '' COMMENT '要绑定的域名',
    `ip`            varchar(255) NOT NULL DEFAULT '' COMMENT '要绑定的ip',
    `agent_ip_list` json                  DEFAULT NULL COMMENT '绑定的机器ip列表',
    `apply_status`  int(11) NOT NULL DEFAULT '0' COMMENT '申请状态 0：待审核 1：审核完成',
    `ctime`         bigint(64) NOT NULL DEFAULT '0' COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=30001;

CREATE TABLE `mibench_task`
(
    `id`                             int(32) NOT NULL AUTO_INCREMENT,
    `qps`                            int(32) DEFAULT NULL,
    `origin_qps`                     int(32) DEFAULT NULL COMMENT '起始qps',
    `max_qps`                        int(11) DEFAULT NULL COMMENT '最大qps',
    `scene_id`                       int(32) DEFAULT NULL,
    `serial_link_id`                 int(32) DEFAULT NULL COMMENT '所属链路id',
    `scene_api_id`                   int(32) DEFAULT NULL COMMENT '该任务属于哪个接口',
    `time`                           int(32) DEFAULT NULL,
    `agent_num`                      int(32) DEFAULT NULL,
    `finish_agent_num`               int(32) DEFAULT NULL,
    `ctime`                          bigint(64) DEFAULT NULL,
    `utime`                          bigint(64) DEFAULT NULL,
    `state`                          int(32) DEFAULT NULL,
    `version`                        int(32) DEFAULT NULL,
    `success_num`                    bigint(64) DEFAULT NULL,
    `failure_num`                    bigint(64) DEFAULT NULL,
    `task_type`                      int(32) DEFAULT NULL COMMENT '任务类型：1 http 2 dubbo 3 dag',
    `parent_task_id`                 int(32) DEFAULT NULL COMMENT '所属父任务id',
    `debug_result`                   longtext     DEFAULT NULL COMMENT '调试的结果',
    `debug_result_header`            json         DEFAULT NULL,
    `report_id`                      varchar(255) DEFAULT NULL COMMENT '标记为同一次压测任务',
    `request_params`                 json         DEFAULT NULL COMMENT '请求参数',
    `req_param_type`                 int(11) DEFAULT NULL COMMENT '请求参数所属类型',
    `ok`                             tinyint(1) DEFAULT NULL COMMENT '是否成功',
    `connect_task_num`               int(11) DEFAULT NULL COMMENT '同一次压测下的总任务数',
    `debug_trigger_cp`               json         DEFAULT NULL COMMENT '触发的cp',
    `debug_trigger_filter_condition` json         DEFAULT NULL COMMENT '触发的过滤条件',
    `debug_req_headers`              json         DEFAULT NULL,
    `debug_url`                      text         DEFAULT NULL COMMENT '实际使用的url',
    `debug_rt`                       int(32) NOT NULL DEFAULT '0' COMMENT '调试的rt',
    `debug_size`                     int(32) NOT NULL DEFAULT '0' COMMENT '调试返回结果的大小',
    `bench_mode`                     int(11) NOT NULL DEFAULT '0' COMMENT '压力模式 0 rps、1并发',
    `increase_mode`                  int(11) NOT NULL DEFAULT '0' COMMENT 'Rps压力增加模式 0 固定（） 1 手动 2 百分比递增',
    `increase_percent`               int(11) NOT NULL DEFAULT '0' COMMENT '递增百分比',
    PRIMARY KEY (`id`),
    KEY                              `idx_parent_task_id` (`parent_task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=152326;

CREATE TABLE `operation_log`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `report_id`         varchar(255)         DEFAULT '' COMMENT '报告id',
    `scene_id`          int(32) NOT NULL COMMENT '所属场景id',
    `type`              int(4) unsigned DEFAULT '0' COMMENT '操作类型',
    `content`           text                 DEFAULT NULL COMMENT '操作内容',
    `support_operation` json                 DEFAULT NULL,
    `create_by`         varchar(64) NOT NULL DEFAULT '' COMMENT '操作人用户名',
    `create_time`       bigint(64) NOT NULL COMMENT '创建时间',
    `update_time`       bigint(64) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY                 `operation_log_scene_id_index` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=60003 COMMENT='压测操作记录';

CREATE TABLE `report_info`
(
    `id`                             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `scene_id`                       bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '场景id',
    `snapshot_id`                    varchar(128) NOT NULL DEFAULT '' COMMENT '场景快照id',
    `report_id`                      varchar(128) NOT NULL DEFAULT '' COMMENT '报告id',
    `report_name`                    varchar(255) NOT NULL DEFAULT '' COMMENT '报告名',
    `duration`                       int(10) unsigned DEFAULT '0' COMMENT '压测时长',
    `concurrency`                    int(10) unsigned NOT NULL DEFAULT '0' COMMENT '默认并发数',
    `concurrency_max`                int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大并发数',
    `create_by`                      varchar(64)  NOT NULL DEFAULT '' COMMENT '操作人用户名',
    `create_time`                    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`                    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status`                         int(11) unsigned NOT NULL DEFAULT '0',
    `task_id`                        bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '执行任务id',
    `file_path`                      varchar(255) NOT NULL DEFAULT '' COMMENT '报告路径',
    `finish_time`                    bigint(64) DEFAULT NULL COMMENT '压测结束时间',
    `agents`                         text                  DEFAULT NULL COMMENT 'agent ip列表',
    `extra`                          text                  DEFAULT NULL COMMENT '扩展信息',
    `sla_event_list`                 json                  DEFAULT NULL COMMENT '事件列表',
    `total_stat_analysis_event_list` json                  DEFAULT NULL COMMENT '错误分析事件记录',
    `tenant`                         varchar(255)          DEFAULT NULL COMMENT '租户信息',
    `link_to_dag_id`                 json                  DEFAULT NULL COMMENT '链路id与dag任务id的映射',
    PRIMARY KEY (`id`),
    UNIQUE KEY `report_info_report_id_uindex` (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=90001 COMMENT='压测报告信息';

CREATE TABLE `scene_api_info`
(
    `id`                 int(32) NOT NULL AUTO_INCREMENT COMMENT '场景接口id',
    `scene_id`           int(32) NOT NULL COMMENT '该接口所属场景id',
    `api_order`          int(11) NOT NULL COMMENT '接口在该场景下的顺序',
    `api_name`           varchar(255) NOT NULL DEFAULT '' COMMENT '接口名',
    `source_type`        int(11) NOT NULL COMMENT '接口来源 1：mi-api导入 2 手动输入',
    `api_type`           int(11) NOT NULL COMMENT '接口类型 0:http 1:dubbo',
    `api_url`            text                  DEFAULT NULL COMMENT '接口请求url',
    `request_method`     int(11) NOT NULL DEFAULT '0' COMMENT '请求方式  0:get,1:post',
    `request_timeout`    int(11) NOT NULL DEFAULT '0' COMMENT '请求超时时间  ms',
    `need_login`         tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否需要登录 0:否 1:是',
    `token_type`         int(11) DEFAULT '0' COMMENT '使用的token部门  0 有品(tokens) 1 米粉app ',
    `api_header`         json                  DEFAULT NULL COMMENT '接口请求头',
    `content_type`       varchar(255)          DEFAULT NULL COMMENT 'post接口的请求数据格式   x-www-form-urlencoded   raw(多种)',
    `request_param_info` json                  DEFAULT NULL COMMENT 'http请求参数，带结构',
    `output_param_info`  json                  DEFAULT NULL COMMENT '接口出参定义',
    `request_body`       json                  DEFAULT NULL COMMENT 'post 请求体',
    `nacos_type`         int(11) DEFAULT NULL COMMENT 'dubbo接口使用的nacos地址',
    `service_name`       varchar(255)          DEFAULT '' COMMENT 'dubbo服务名',
    `method_name`        varchar(255)          DEFAULT '' COMMENT 'dubbo接口方法名',
    `param_type_list`    varchar(255)          DEFAULT '' COMMENT 'dubbo参数类型列表',
    `dubbo_param_json`   json                  DEFAULT NULL COMMENT 'dubbo请求参数',
    `dubbo_group`        varchar(255)          DEFAULT '' COMMENT 'dubbo接口分组',
    `dubbo_version`      varchar(255)          DEFAULT '' COMMENT 'dubbo接口版本',
    `serial_link_id`     int(32) NOT NULL COMMENT '所属串联链路id',
    `check_point`        json                  DEFAULT NULL COMMENT '接口检查点配置',
    `filter_condition`   json                  DEFAULT NULL COMMENT '接口的过滤条件',
    `api_tsp_auth`       json                  DEFAULT NULL COMMENT '接口级别tsp验权',
    `api_traffic_info`   json                  DEFAULT NULL COMMENT '接口的流量录制配置',
    `api_x5_info`        json                  DEFAULT NULL COMMENT 'x5鉴权信息',
    PRIMARY KEY (`id`),
    KEY                  `idx_scene_link` (`scene_id`,`serial_link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=90001;

CREATE TABLE `scene_group`
(
    `id`         int(32) NOT NULL AUTO_INCREMENT COMMENT '分组id',
    `group_name` varchar(255) NOT NULL COMMENT '分组名',
    `group_desc` varchar(255) DEFAULT NULL COMMENT '分组描述',
    `creator`    varchar(255) DEFAULT NULL COMMENT '创建人',
    `ctime`      bigint(64) NOT NULL COMMENT '创建时间',
    `tenant`     varchar(255) DEFAULT NULL COMMENT '租户信息',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=60002;

CREATE TABLE `scene_info`
(
    `id`               int(32) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`             varchar(255) NOT NULL DEFAULT '' COMMENT '场景名',
    `scene_status`     int(11) NOT NULL COMMENT '场景状态 0 待启动 1 执行中',
    `creator`          varchar(255) NOT NULL DEFAULT '' COMMENT '创建人',
    `updator`          varchar(255) NOT NULL DEFAULT '' COMMENT '更新人',
    `apiNum`           int(11) NOT NULL COMMENT '有效api数',
    `remark`           varchar(255)          DEFAULT '' COMMENT '备注',
    `scene_type`       int(11) NOT NULL COMMENT '场景类型 0:http 1:dubbo',
    `bench_mode`       int(11) NOT NULL DEFAULT '0' COMMENT '压力模式 0 RPS （目前仅支持该模式）',
    `Increment_mode`   int(11) NOT NULL DEFAULT '0' COMMENT '递增模式 0 手动（Rps下仅支持手动模式）',
    `increase_percent` int(11) NOT NULL DEFAULT '0' COMMENT '递增模式下的递增百分比',
    `bench_time`       int(11) NOT NULL DEFAULT '3' COMMENT '压测时间（秒）',
    `max_bench_qps`    int(11) DEFAULT NULL COMMENT '场景最大施压qps',
    `rps_rate`         int(11) NOT NULL DEFAULT '100' COMMENT '发压比例',
    `api_bench_infos`  json                  DEFAULT NULL COMMENT '场景各接口施压配置，最大、起始RPS，json格式',
    `log_rate`         int(11) DEFAULT '0' COMMENT '日志采样率  百分比',
    `sla`              json                  DEFAULT NULL COMMENT '场景对应的sla 信息',
    `request_timeout`  int(11) NOT NULL DEFAULT '0' COMMENT '通用请求超时时间  ms',
    `success_code`     varchar(255)          DEFAULT NULL COMMENT '请求成功状态码（默认200 ）"301,302"',
    `ctime`            bigint(64) NOT NULL COMMENT '创建时间',
    `utime`            bigint(64) NOT NULL COMMENT '更新时间',
    `scene_group_id`   int(32) NOT NULL DEFAULT '0' COMMENT '所属分组id',
    `global_header`    json                  DEFAULT NULL COMMENT '全局header',
    `cur_report_id`    varchar(255)          DEFAULT NULL COMMENT '当前运行时对应的报告id',
    `agent_list`       json                  DEFAULT NULL COMMENT '场景绑定的压测机id列表',
    `scene_env`        int(11) NOT NULL DEFAULT '0' COMMENT '场景环境',
    `ref_dataset_ids`  text                  DEFAULT NULL COMMENT '真正引用的数据集id',
    `tenant`           varchar(255)          DEFAULT NULL COMMENT '租户信息\n',
    `bench_count`      int(11) NOT NULL DEFAULT '0' COMMENT '压测次数',
    `scene_source`     int(11) NOT NULL DEFAULT '0' COMMENT '场景来源 0:控制台创建 1:openapi创建',
    `person_in_charge` json                  DEFAULT NULL COMMENT '场景负责人',
    `last_bench_time`  bigint(64) NOT NULL DEFAULT '0' COMMENT '上次压测时间',
    `bench_calendar`   json                  DEFAULT NULL COMMENT '压测日历',
    `global_tsp_auth`  json                  DEFAULT NULL COMMENT '全局tsp验证信息',
    PRIMARY KEY (`id`),
    KEY                `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=90001;

CREATE TABLE `scene_snapshot`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `scene_id`    bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '场景id',
    `snapshot_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'snapshot id',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`   varchar(64)           DEFAULT '' COMMENT '创建人用户名',
    `version`     int(10) unsigned DEFAULT '0' COMMENT '场景配置版本',
    `md5`         varchar(64)  NOT NULL DEFAULT '',
    `scene`       longtext              DEFAULT NULL COMMENT '场景配置',
    `type`        int(32) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `scene_snapshot_id_uindex` (`snapshot_id`),
    KEY           `scene_snapshot_scene_index` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=90001 COMMENT='压测配置快照';

CREATE TABLE `serial_link`
(
    `id`       int(32) NOT NULL AUTO_INCREMENT COMMENT '串联链路id',
    `name`     varchar(255) NOT NULL COMMENT '串联链路名',
    `scene_id` int(32) NOT NULL COMMENT '所属场景id',
    `enable`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启用',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin AUTO_INCREMENT=90001;

CREATE TABLE `sla`
(
    `id`             int(32) NOT NULL AUTO_INCREMENT,
    `name`           varchar(128) DEFAULT NULL,
    `note`           varchar(128) DEFAULT NULL,
    `business_group` varchar(128) DEFAULT NULL,
    `ctime`          bigint(64) DEFAULT NULL,
    `utime`          bigint(64) DEFAULT NULL,
    `creator`        varchar(128) DEFAULT NULL,
    `updater`        varchar(128) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY              `k_creator` (`creator`),
    KEY              `k_ctime` (`ctime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=90003;

CREATE TABLE `sla_rule`
(
    `id`                int(32) NOT NULL AUTO_INCREMENT,
    `sla_id`            int(32) NOT NULL,
    `name`              varchar(128)          DEFAULT NULL COMMENT '规则名称',
    `rule_item_type`    varchar(128)          DEFAULT NULL COMMENT '指标类型',
    `rule_item`         varchar(128)          DEFAULT NULL COMMENT '指标名',
    `compare_condition` varchar(128) NOT NULL DEFAULT '' COMMENT '对比条件',
    `compare_value`     int(32) DEFAULT NULL,
    `degree`            int(32) DEFAULT NULL,
    `action_level`      varchar(128)          DEFAULT NULL,
    `ctime`             bigint(64) DEFAULT NULL,
    `utime`             bigint(64) DEFAULT NULL,
    `creator`           varchar(128)          DEFAULT NULL,
    `updater`           varchar(128)          DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY                 `k_sla_id` (`sla_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=90003;