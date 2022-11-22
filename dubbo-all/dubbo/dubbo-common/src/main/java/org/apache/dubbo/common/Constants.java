package org.apache.dubbo.common;

import org.apache.dubbo.common.constants.*;

public class Constants implements
        ClusterRules,
        CommonConstants,
        ConsulConstants,
        FilterConstants,
        LoadbalanceRules,
        QosConstants,
        RegistryConstants,
        RemotingConstants {

    /**
     * xiaomi
     * 兼容以前的变量
     */
    public static final String TRACE_ID = "_trace_id_";

    public static final String RETRY_SPAN_ID = "_retry_span_id_";

    public static final String SPAN_ID = "_span_id_";

    public static final String LOG_ARGUMENTS = "log_arguments";

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final String LOG_RESULT = "log_result";

    public static final String LOG_EXCEPTION = "log_exception";

    public static final String GSON_GENERIC_ARGS = "gson_generic_args";

    public static final String GENERIC_SERIALIZATION_YOUPIN_JSON = "youpin_json";

    public static final String PREFIX_GENERIC_RETRIES_KEY = "generic.retries";

    public static final String YOUPIN_PROTOCOL_VERSION = "youpin_protocol_version";

    public static final String FILTER_FIELD = "filter_field";





}
