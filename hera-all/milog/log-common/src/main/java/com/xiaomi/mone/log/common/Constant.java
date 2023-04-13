package com.xiaomi.mone.log.common;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/9 10:45
 */
public class Constant {
    /**
     * mq http 请求成功返回码
     */
    public static final int SUCCESS_CODE = 100;
    public static final Double SUCCESS_CODE_DOUBLE = 100.0;

    public static final int RPCCMD_AGENT_CODE = 13579;
    public static final int RPCCMD_AGENT_CONFIG_CODE = 24680;
    /**
     * mq http 请求成功返回消息
     */
    public static final String SUCCESS_MESSAGE = "success";

    public static final String COMMON_MESSAGE = "common";

    public static final String STREAM_CONTAINER_POD_NAME_KEY = "MONE_CONTAINER_S_POD_NAME";

    public static final String DEFAULT_STREAM_SERVER_NAME = "hera_log_stream";

    public static final String HAS_CREATED_MESSAGE = "This name has been used!";
    /**
     * 操作符=
     */
    public static final String EQUAL_OPERATE = "=";

    public static final String LIKE_OPERATE = "like";

    public static final String DEFAULT_TAIL_SEPARATOR = "|";
    /**
     * mq消息类型
     */
    public static final String ROCKET_MQ_TYPE = "rocketmq";

    public static final Integer DEFAULT_IMPORT_LEVEL = 3;

    public static final Integer MAX_IMPORT_LEVEL = 1;

    public static final String DEFAULT_OPERATOR = "system";

    public static final String DEFAULT_JOB_OPERATOR = "job";

    public static final String LOG_MANAGE_PREFIX = "log_manage_";
    public static final String LOG_MANAGE_GWDASH_TOKEN = "gwdash_token";

    public static final String NAMESPACE_CONFIG_DATA_ID = "create_namespace_config_open";

    public static final String TAIL_CONFIG_DATA_ID = "create_tail_config_open:";

    public static final String DEFAULT_GROUP_ID = "DEFAULT_GROUP";

    public static final String DEFAULT_APP_NAME = "";

    public static final Long DEFAULT_TIME_OUT_MS = 3000L;

    public static final String DEFAULT_TAGS = "tags_";

    public static final String DEFAULT_CONSUMER_GROUP = "subGroup_";

    public static final String UNDERLINE_SYMBOL = "_";

    public static final String STRIKETHROUGH_SYMBOL = "-";

    public static final String ES_INDEX_PREFIX = "milog_";

    public static final String LOG_STORE = "log-store";

    public static final String SYMBOL_COLON = ":";

    public static final String SYMBOL_COMMA = ",";

    public static final String SYMBOL_MULTI = ".*";

    public static final String SYMBOL_COMMA_SPACE = " ";

    public static final Integer YES = 1;

    public static final Integer COUNT_NUM = 10000;

    public static final String TAILID_KEY = "tailId";

    public static final String LOG_KEY = "log";

    public static final String TRACE_ID_KEY = "traceId";

    public static final String COMMON_MQ_PREFIX = "common_mq_miLog";

    public static final List<String> COMMON_MQ_SUFFIX = Lists.newArrayList("first", "second", "third");

    public static final Integer COMMON_MQ_PARTITION_NUM = 2;

    public static final String DEFAULT_SPACE_SUFFIX = "namespace";

    public static final String DEFAULT_STORE_SUFFIX = "store";

    public static final String DEFAULT_SPACE_DESC = "我们的namespace，都在一个篮子里，属于:%s 项目";

    public static final Integer MIFAAS_STORE_NUM = 10;

    public static final String DEFAULT_COLUMN_TYPE_LIST = "date,keyword,keyword,text,text,keyword,keyword,keyword,keyword,keyword,keyword,keyword,keyword,keyword,long";

    public static final String DEFAULT_KEY_LIST = "timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3";

    public static final String DEFAULT_VALUE_LIST = "0,1,2,3,4,5";

    public static final Integer MIFAAS_APP_TYPE_CODE = 4;

    public static final String MIFAAS_APP_TYPE_TEXT = "serverLess";

    /**
     * rocketmq 数据监控
     */

    public static final String ROCKETMQ_GROUP_DIFF = "rocketmq_group_diff";
    public static final String ROCKETMQ_CLIENT_CONSUME_FAILED_MSG_COUNT = "rocketmq_client_consume_fail_msg_count";
    public static final String ROCKETMQ_CONSUMER_TPS = "rocketmq_consumer_tps";
    public static final String ROCKETMQ_PRODUCER_TPS = "rocketmq_producer_tps";
    public static final String ROCKETMQ_PRODUCER_OFFSET = "rocketmq_producer_offset";

    /**
     * es客户端bean名前缀
     */
    public static String ES_SERV_BEAN_PRE = "esServiceBean_";

    /**
     * es机房
     */
    public static String ES_REGION_EROUP = "Amstega";

    public static final String NULLVALUE = "null";

    public static final String PRODENV = "prod";

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    // milog -alarm use
    public static final String ACCESS_KEY = "accessKey";
    public static final String SECRET_KEY = "secretKey";
    public static final String PRODUCER_ACCESS_KEY = "producerAccessKey";
    public static final String PRODUCER_SECRET_KEY = "producerSecretKey";
    public static final String ALERT_ID = "alertId";
    public static final String CONSUMER_SERVER = "consumerServer";
    public static final String PRODUCER_SERVER = "producerServer";
    public static final String CONSUMER_TOPIC = "consumerTopic";
    public static final String PRODUCER_TOPIC = "producerTopic";
    public static final String MQ_TYPE = "mqType";
    public static final String CONSUMER_GROUP = "consumerGroup";
    public static final String CONSUMER_TAG = "consumerTag";
    public static final String ALERT_RULES_INPUT = "alertRulesInput";
    public static final String ALERT_PARALLELISM = "parallelism";
    public static final String USER_SET = "userSet";

    public static final String ES_CONWAY_PWD = "pwd";
    public static final String ES_CONWAY_TOKEN = "token";
}
