create table if not exists m78.m78_agent
(
    id      bigint auto_increment
        primary key,
    name    varchar(255) null,
    role    varchar(255) null,
    status  int          null,
    ctime   bigint       null,
    utime   bigint       null,
    creator varchar(255) null,
    meta    json         null
);

create table if not exists m78.m78_agent_contacts
(
    id               bigint auto_increment
        primary key,
    label            varchar(255) null,
    agent_id         bigint       null,
    contact_agent_id bigint       null,
    status           int          null,
    ctime            bigint       null,
    utime            bigint       null,
    meta             json         null
);

create table if not exists m78.m78_agent_messages
(
    id           bigint auto_increment
        primary key,
    source_agent bigint null,
    target_agent bigint null,
    content      text   null,
    status       int    null,
    ctime        bigint null,
    utime        bigint null,
    meta         json   null
);

create table if not exists m78.m78_bot
(
    id             bigint auto_increment
        primary key,
    name           varchar(128)            not null comment '名称',
    workspace_id   bigint                  not null comment 'workspaceId',
    remark         varchar(512)            null comment '备注',
    creator        varchar(255) default '' null,
    updator        varchar(255) default '' null,
    avatar_url     varchar(255)            null,
    permissions    int(1)       default 0  not null comment '开放权限0-私有 1-公开',
    publish_status int(1)       default 0  not null,
    publish_time   datetime                null,
    create_time    datetime                null,
    update_time    datetime                null,
    deleted        int(1)       default 0  not null comment '是否删除 0-否 1-是',
    bot_use_times  bigint(10)              null comment 'bot使用次数'
);

create index idx_workspace_id
    on m78.m78_bot (workspace_id);

create table if not exists m78.m78_bot_character_setting
(
    id                      bigint auto_increment
        primary key,
    bot_id                  bigint                  not null comment 'bot id',
    setting                 text                    null comment 'bot人设',
    ai_model                varchar(64)             not null comment '模型',
    dialogue_turns          int(2)                  not null comment '对话轮次',
    opening_remarks         text                    null comment '开场白文案',
    opening_ques            json                    null comment '开场白问题',
    customize_prompt_switch int(2)                  not null comment '预留问题开关，0-关闭 1-开启',
    customize_prompt        varchar(3000)           null comment '预留问题prompt',
    timbre_switch           int(2)                  not null comment '音色开关，0-关闭 1-开启',
    timbre                  varchar(128)            null comment '音色',
    deleted                 int(1)       default 0  not null,
    creator                 varchar(128) default '' not null comment '创建人',
    updater                 varchar(128) default '' not null comment '更新人',
    create_time             datetime                null,
    update_time             datetime                null,
    dialogue_timeout        int                     null comment '对话超时时间(ms)'
);

create index idx_bot_id
    on m78.m78_bot_character_setting (bot_id);

create table if not exists m78.m78_bot_comment
(
    id              int auto_increment
        primary key,
    bot_id          bigint       not null,
    score           int          not null,
    comment_content text         not null,
    create_time     datetime     not null,
    update_time     datetime     not null,
    create_by       varchar(255) not null,
    update_by       varchar(255) not null
);

create index bot_id
    on m78.m78_bot_comment (bot_id);

create table if not exists m78.m78_bot_db_table
(
    id           bigint auto_increment
        primary key,
    workspace_id bigint        not null,
    table_name   varchar(128)  not null,
    creator      varchar(128)  not null,
    create_time  datetime      null on update CURRENT_TIMESTAMP,
    bot_id       bigint        null,
    column_info  json          null,
    table_desc   varchar(2048) null,
    demo         text          null comment '示例数据'
);

create table if not exists m78.m78_bot_db_table_rel
(
    id          bigint auto_increment
        primary key,
    bot_id      bigint           not null,
    db_table_id bigint           not null,
    creator     varchar(128)     not null,
    create_time datetime         null,
    deleted     int(1) default 0 null
);

create index idx_bot_id
    on m78.m78_bot_db_table_rel (bot_id);

create table if not exists m78.m78_bot_flow_rel
(
    id           bigint auto_increment
        primary key,
    bot_id       bigint           not null,
    flow_base_id bigint           not null,
    creator      varchar(128)     not null,
    create_time  datetime         null,
    deleted      int(1) default 0 not null
);

create table if not exists m78.m78_bot_knowledge_rel
(
    id                bigint auto_increment
        primary key,
    bot_id            bigint       not null,
    knowledge_id_list json         null,
    creator           varchar(128) null,
    create_time       datetime     null
);

create table if not exists m78.m78_bot_plugin
(
    id                bigint unsigned auto_increment
        primary key,
    org_id            bigint unsigned                               null comment '插件所属组织id',
    name              varchar(1024)       default ''                not null comment '名称',
    api_url           text                                          null comment '插件api_url',
    feature_router_id bigint unsigned                               null comment '导出的featureRouterId',
    meta              json                                          null,
    release_time      datetime                                      null comment '发布时间',
    create_time       datetime            default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time       timestamp           default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    status            int                 default 0                 not null comment '预留, 行状态标记',
    user_name         varchar(255)        default ''                not null comment '用户名',
    type              int                 default 0                 not null comment '类型标记, 0：featureRouter类型，1: 自定义类型',
    category          varchar(1024)       default ''                not null comment '分类',
    category_id       bigint unsigned                               null comment '插件所属分类id',
    description       text                                          null comment '描述(可以很长)',
    plugin_use_times  bigint(10) unsigned default 0                 null,
    avatar_url        varchar(1024)                                 null comment '头像地址',
    workspace_id      bigint                                        null,
    debug_status      int                 default 0                 not null,
    modifier          varchar(255)        default ''                not null comment '更新人'
);

create table if not exists m78.m78_bot_plugin_favorite
(
    id          bigint unsigned auto_increment
        primary key,
    plugin_id   bigint unsigned                        null comment '插件id',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    status      int          default 0                 not null comment '预留, 行状态标记',
    user_name   varchar(255) default ''                not null comment '用户名',
    type        int          default 0                 not null comment '类型标记, 预留'
);

create table if not exists m78.m78_bot_plugin_org
(
    id           bigint unsigned auto_increment
        primary key,
    name         varchar(1024) default ''                not null comment '名称',
    release_time datetime                                null comment '最新的插件发布时间',
    create_time  datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time  timestamp     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    status       int           default 0                 not null comment '预留, 行状态标记',
    user_name    varchar(255)  default ''                not null comment '用户名',
    type         int           default 0                 not null comment '类型标记, 预留',
    description  text                                    null comment '描述(可以很长)',
    avatar_url   varchar(1024)                           null,
    modifier     varchar(255)  default ''                not null comment '更新人',
    workspace_id bigint                                  null
);

create table if not exists m78.m78_bot_plugin_rel
(
    id          bigint auto_increment
        primary key,
    bot_id      bigint           not null,
    plugin_id   bigint           not null,
    deleted     int(1) default 0 not null,
    creator     varchar(128)     not null,
    create_time datetime         null
);

create index idx_bot_id
    on m78.m78_bot_plugin_rel (bot_id);

create index idx_plugin_id
    on m78.m78_bot_plugin_rel (plugin_id);

create table if not exists m78.m78_bot_plugin_usage
(
    plugin_id   bigint unsigned                     null comment '插件id',
    bot_id      bigint unsigned                     null comment 'bot id',
    create_time datetime  default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    status      int       default 0                 not null comment '预留, 行状态标记',
    type        int       default 0                 not null comment '类型标记, 预留'
);

create table if not exists m78.m78_bot_publish_record
(
    id                 bigint auto_increment
        primary key,
    bot_id             bigint        not null,
    version_record     varchar(2000) not null comment '版本记录',
    publish_im_channel json          null comment '发布渠道，ex:[1,2,3], id参考m78_im_type',
    publisher          varchar(64)   not null comment '发布人',
    publish_time       datetime      null comment '发布时间',
    bot_snapshot       text          null comment 'bot快照'
);

create table if not exists m78.m78_category
(
    id          bigint auto_increment
        primary key,
    name        varchar(128) not null comment '类目名称',
    deleted     int(1)       not null,
    create_time datetime     null,
    type        int(1)       null
);

create table if not exists m78.m78_category_bot_rel
(
    id          bigint auto_increment
        primary key,
    cat_id      bigint   not null comment '分类id',
    bot_id      bigint   not null comment 'bot id',
    deleted     int(1)   not null comment '是否删除0-否 1-是',
    create_time datetime null
);

create table if not exists m78.m78_chat_info
(
    id              bigint unsigned auto_increment
        primary key,
    session_id      varchar(255) default ''                not null comment 'sessionId, relate to UUID',
    content         mediumtext                             null comment '单条聊天记录内容',
    mapping_content mediumtext                             null comment '聊天记录对应映射内容，eg: sql',
    conditions      json                                   null comment '解析后的查询条件',
    user_name       varchar(255) default ''                not null comment '用户名',
    heat            bigint       default 0                 not null comment '热度，排序预留',
    create_time     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time     timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    status          int          default 0                 not null comment '预留, 行状态标记',
    chat_info_meta  json                                   null comment 'meta信息',
    type            int          default 0                 null
);

create table if not exists m78.m78_chat_topics
(
    id               int auto_increment
        primary key,
    title            varchar(255) not null,
    user_name        varchar(200) null,
    description      text         null,
    ctime            bigint       not null,
    utime            bigint       not null,
    state            int          not null,
    knowledge_config json         null
);

create table if not exists m78.m78_chat_messages
(
    id           int auto_increment
        primary key,
    topic_id     int          not null,
    user_name    varchar(200) null,
    message_role varchar(200) null,
    message      text         not null,
    ctime        bigint       not null,
    utime        bigint       not null,
    state        int          not null,
    meta         json         null,
    constraint topic_id
        foreign key (topic_id) references m78.m78_chat_topics (id)
);

create index ctime
    on m78.m78_chat_messages (ctime);

create index state
    on m78.m78_chat_messages (state);

create index utime
    on m78.m78_chat_messages (utime);

create index ctime
    on m78.m78_chat_topics (ctime);

create index state
    on m78.m78_chat_topics (state);

create index utime
    on m78.m78_chat_topics (utime);

create table if not exists m78.m78_code
(
    id      bigint auto_increment
        primary key,
    utime   bigint       not null,
    ctime   bigint       not null,
    code    json         null,
    creator varchar(128) not null,
    type    int          not null,
    name    varchar(255) not null,
    `desc`  text         null,
    model   varchar(255) null comment '模型'
);

create table if not exists m78.m78_connection_info
(
    id               int unsigned auto_increment
        primary key,
    host             varchar(255) default ''                null,
    port             varchar(255) default ''                null,
    `database`       varchar(255) default ''                null,
    user             varchar(255) default ''                null,
    pwd              varchar(255) default ''                null,
    jdbc_url         varchar(255) default ''                null,
    cluster          varchar(255)                           null,
    kerberos         varchar(255)                           null,
    queue            varchar(255)                           null,
    type             int          default 0                 null,
    user_name        varchar(255)                           null,
    create_time      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time      timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    custom_knowledge text                                   null comment 'meta信息的文本'
);

create table if not exists m78.m78_contact_info
(
    id                     bigint unsigned auto_increment
        primary key,
    contact_name           varchar(255) default ''                not null comment '联系名',
    contact_email          varchar(255) default ''                not null comment '联系email',
    contact_subject        varchar(255) default ''                not null comment '主题',
    contact_content        mediumtext                             null comment '内容',
    processing_status      int          default 0                 not null comment '处理状态',
    processing_person_name varchar(255) default ''                not null comment '处理人名',
    user_name              varchar(255) default ''                not null comment '用户名',
    create_time            datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time            timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
);

create table if not exists m78.m78_custom_task
(
    id               bigint unsigned auto_increment
        primary key,
    task_name        varchar(50)   default '' not null comment 'task_name',
    task_type        int           default 1  not null comment 'task_type',
    scheduled_time   varchar(30)              not null comment 'scheduled_time',
    status           int           default 1  not null comment 'status',
    user_name        varchar(25)   default '' not null comment '用户名',
    task_description varchar(1024) default '' not null comment 'task_description',
    ctime            bigint                   null,
    utime            bigint                   null,
    bot_id           bigint                   null,
    moon_id          bigint                   null,
    task_detail      json                     null,
    core_type        varchar(15)              null
);

create table if not exists m78.m78_feature_router
(
    id          bigint unsigned auto_increment
        primary key,
    name        varchar(255)                        null comment 'feature router名',
    user_name   varchar(255)                        null comment '所属用户名',
    label_id    bigint unsigned                     null comment '对应的chatInfoId',
    status      int       default 0                 not null comment '预留, 行状态标记',
    create_time datetime  default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    router_meta json                                null comment '请求参数信息',
    type        int       default 0                 not null comment '类型'
);

create table if not exists m78.m78_flow
(
    id        bigint auto_increment
        primary key,
    name      varchar(255) not null,
    state     int          null,
    user_name varchar(255) null,
    ctime     bigint       null,
    utime     bigint       null,
    nodes     json         null,
    edges     json         null
);

create table if not exists m78.m78_flow_base
(
    id             bigint auto_increment
        primary key,
    work_space_id  bigint           null,
    name           varchar(255)     not null,
    avatar_url     varchar(255)     null,
    state          int    default 0 null,
    publish_status int(1) default 0 not null,
    publish_time   bigint           null,
    run_status     int(1) default 0 not null,
    user_name      varchar(255)     null,
    ctime          bigint           null,
    utime          bigint           null,
    `desc`         varchar(1024)    null,
    inputs         json             null
);

create table if not exists m78.m78_flow_setting
(
    id           bigint auto_increment
        primary key,
    flow_base_id bigint        null,
    state        int default 0 null,
    ctime        bigint        null,
    utime        bigint        null,
    nodes        json          null,
    edges        json          null,
    constraint unq_flow_base_id
        unique (flow_base_id)
);

create table if not exists m78.m78_im_record
(
    id          bigint auto_increment
        primary key,
    bot_id      bigint       not null comment '机器人id',
    chat_id     varchar(128) null comment '会话id',
    im_type_id  int(2)       not null comment 'im类型id',
    user_name   varchar(30)  not null comment 'username',
    status      int(1)       not null,
    create_time datetime     null comment '创建时间'
);

create table if not exists m78.m78_im_relation
(
    id            bigint auto_increment
        primary key,
    bot_id        bigint       not null comment '机器人id',
    im_type_id    int(2)       not null comment 'im类型id',
    relation_flag varchar(512) not null comment '关联标志，比如openId',
    deleted       int(1)       not null,
    creator       varchar(128) not null comment '创建人',
    create_time   datetime     null comment '创建时间',
    bot_name      varchar(80)  null
);

create index idx_relation_flag
    on m78.m78_im_relation (relation_flag);

create table if not exists m78.m78_im_type
(
    id          int auto_increment
        primary key,
    name        varchar(128) not null,
    deleted     varchar(255) not null,
    create_time datetime     null
);

create table if not exists m78.m78_knowledge_base
(
    id                  bigint unsigned auto_increment comment '自增id'
        primary key,
    knowledge_base_id   bigint unsigned         not null comment '知识库id',
    type                varchar(255)            not null comment '知识库类型',
    status              tinyint(2)   default 0  not null comment '状态',
    auth                tinyint(2)   default 0  not null comment '鉴权',
    knowledge_base_name varchar(255) default '' not null comment '知识库名字',
    labels              text                    null comment '标签',
    remark              text                    null comment '备注',
    avatar_url          text                    null comment '头像链接',
    creator             varchar(255) default '' null comment '创建人',
    updater             varchar(255) default '' null comment '更新人',
    create_time         datetime                null,
    update_time         datetime                null on update CURRENT_TIMESTAMP,
    deleted             tinyint(1)   default 0  not null comment '0-删除  1-未删除'
);

create table if not exists m78.m78_meta
(
    id                 bigint unsigned auto_increment
        primary key,
    uuid               varchar(255)  default ''                not null comment 'UUID',
    table_name         varchar(1024) default ''                not null,
    create_time        datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time        timestamp     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    status             int           default 0                 not null comment '预留, 行状态标记',
    user_name          varchar(255)  default ''                not null comment '用户名',
    favorite           tinyint(1)    default 0                 not null comment '是否为收藏, 0: 非收藏, 1：收藏',
    type               int           default 0                 not null comment '类型标记, 0：excel文档, 1：翻译文本的输入, 2: 翻译文本的输出',
    original_file_name varchar(1024) default ''                not null comment '上传文件原始名',
    custom_knowledge   text                                    null comment 'meta信息的文本',
    constraint unq_uuid
        unique (uuid)
);

create table if not exists m78.m78_test
(
    id          bigint unsigned auto_increment comment 'id'
        primary key,
    gmt_create  datetime                             null comment '创建时间',
    creator     varchar(64)  default ''              not null comment '创建者',
    text_before text                                 not null comment '原文本',
    text_after  text                                 not null comment '脱敏后文本',
    status      varchar(255)                         null,
    t           json                                 null,
    new_column1 varchar(255)                         not null,
    new_column2 int                                  null,
    new_column  varchar(255) default 'default_value' not null comment 'Newly added column'
)
    comment '文本脱敏记录' charset = utf8;

create table if not exists m78.m78_translate
(
    id               bigint unsigned auto_increment
        primary key,
    uuid             varchar(255) default ''                not null comment 'UUID',
    status           int          default 0                 not null comment '预留, 行状态标记',
    type             int          default 0                 not null comment '类型标记',
    favorite         tinyint(1)   default 0                 not null comment '是否为收藏, 0: 非收藏, 1：收藏',
    from_language    varchar(255) default ''                not null comment '源语种',
    to_language      varchar(255) default ''                not null comment '目标语种',
    from_text        mediumtext                             null comment '翻译文本的输入',
    to_text          mediumtext                             null comment '翻译文本的输出',
    custom_knowledge text                                   null comment 'meta信息的文本',
    user_name        varchar(255) default ''                not null comment '用户名',
    create_time      datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time      timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint unq_uuid
        unique (uuid)
);

create table if not exists m78.m78_user_collect
(
    id          bigint unsigned auto_increment comment '主键id'
        primary key,
    username    varchar(255)            not null comment '用户名',
    type        tinyint(2)              not null comment '收藏的类型 1-probot  2-插件....',
    collect_id  bigint                  not null comment '对应收藏内容的具体id',
    creator     varchar(255) default '' null,
    updater     varchar(255) default '' null,
    create_time datetime                null,
    update_time datetime                null on update CURRENT_TIMESTAMP,
    deleted     int(1)       default 0  not null comment '是否删除 0-否 1-是',
    constraint uniq_name_type_collect_id
        unique (username, type, collect_id)
);

create table if not exists m78.m78_user_config
(
    id           int auto_increment
        primary key,
    ctime        bigint       null,
    utime        bigint       null,
    state        int          null,
    user_name    varchar(255) null,
    model_config json         null
);

create table if not exists m78.m78_user_workspace_role
(
    id           bigint auto_increment
        primary key,
    username     varchar(128)     not null,
    workspace_id bigint           not null,
    role         int(1) default 0 not null,
    deleted      int(1) default 0 null,
    create_time  datetime         null
);

create index idx_username
    on m78.m78_user_workspace_role (username);

create index idx_workspace_id
    on m78.m78_user_workspace_role (workspace_id);

create table if not exists m78.m78_workspace
(
    id          bigint auto_increment
        primary key,
    name        varchar(64)             not null comment 'workspace名称',
    avatar_url  varchar(255)            null,
    remark      varchar(1024)           null comment '描述',
    owner       varchar(128)            not null comment '所有者',
    deleted     int(1)                  not null comment '是否删除0-否 1-是',
    creator     varchar(128)            not null comment '创建人',
    create_time datetime                not null comment '创建时间',
    updater     varchar(128) default '' not null comment '更新人',
    update_time datetime                null comment '更新时间'
);

create index idx_creator
    on m78.m78_workspace (creator);

