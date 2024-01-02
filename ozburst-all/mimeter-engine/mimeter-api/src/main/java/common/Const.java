package common;

public class Const {

    public static final boolean sendLog = true;

    public static final String HEAR_HEADER_KEY = "heracontext";

    public static final String DUBBO_TRACE_HEADER_KEY = "traceparent";

    public static final String MIMETER_UA_KEY = "mimeter";

    public static final int SINGLE_API_DEBUG = 0;
    public static final int SCENE_DEBUG = 1;
    public static final int SINGLE_BENCH = 2;

    public static final String METRICS_NAME_TPS = "tps";
    public static final String METRICS_NAME_RPS = "rps";

    public static final String METRICS_TYPE_SCENE = "scene";


    public static final String METRICS_NAME_TPS_API = "tps_api";

    public static final String METRICS_NAME_RPS_API = "rps_api";

    public static final String METRICS_NAME_RT_API = "rt_api";

    public static final String TASK_CTX_RECORD_LOG = "recordLog";

    public static final String TASK_CTX_LINE_FLAG = "lineFlag";

    public static final String TASK_CTX_SCENE_QPS = "sceneQps";

    public static final int PUSH_STAT_RATE = 10;

    public static final int API_TYPE_HTTP = 1;
    public static final int API_TYPE_DUBBO = 3;

    public static final int SCENE_TYPE_HTTP = 0;
    public static final int SCENE_TYPE_DUBBO = 1;

    public static final int HTTP_REQ_GET = 0;
    public static final int HTTP_REQ_POST = 1;

    public static final int FEI_SHU_ALARM = 0;

    public static final int SMS_ALARM = 1;
    public static final String HTTP_GET = "get";
    public static final String HTTP_POST = "post";

    public static final String CONTENT_TYPE_APP_JSON = "application/json";
    public static final String CONTENT_TYPE_APP_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_APP_FORM2 = "x-www-form-urlencoded";

    public static final String HERA_SCENE_TASK = "scene_task";
    public static final String HERA_SERIAL_LINK = "serial_link";
    public static final String HERA_API_ID = "api";


    public static final String SuccessRate = "SuccessRate";

    public static final String P99ResponseTime = "P99ResponseTime";
    public static final String AvgResponseTime = "AvgResponseTime";
    public static final String RequestPerSecond = "RequestPerSecond";
    public static final String CpuUtilization = "CpuUtilization";
    public static final String MemoryUtilization = "MemoryUtilization";
    public static final String Load5Average = "Load5Average";
    public static final String Load5Max = "Load5Max";

    public static final int STATUS_CODE = 1;
    public static final int HEADER_CODE = 2;
    public static final int OUTPUT_CODE = 3;

    public static final int BIGGER = 1;
    public static final int BIGGER_AND_EQ = 2;
    public static final int SMLLER = 3;
    public static final int SMALLER_AND_EQ = 4;
    public static final int EQ = 5;
    public static final int CONTAIN = 6;
    public static final int NOT_CONTAIN = 7;
    public static final int NOT_EQ = 8;


    /**
     * http 错误状态码前缀
     */
    public static final String ERR_STATUS_CODE_PREFIX = "sCode_";

    /**
     * dubbo 错误状态
     */
    public static final String ERR_STATUS_DUBBO_PREFIX = "dubbo_call_fail";

    /**
     * 检查点 错误规则前缀
     */
    public static final String ERR_CHECKPOINT_PREFIX = "cpId_";

    /**
     * http 错误状态码前缀
     */
    public static final int ERR_STATUS_CODE_TYPE = 0;

    /**
     * 检查点 错误规则前缀
     */
    public static final int ERR_CHECKPOINT_TYPE = 1;


    /**
     * dubbo调用错误类型
     */
    public static final int ERR_DUBBO_CALL_TYPE = 2;

    public static final int DUBBO_DATA_TOO_LONG_ERR_CODE = 200012;

    /**
     * 记录每个接口的rt列表
     */
    public static final String RT_LIST = "rt_list";


    /**
     * 记录每个接口的平均rt
     */
    public static final String AVG_RT = "avg_rt";

    /**
     * 记录每个接口的最大rt
     */
    public static final String MAX_RT = "max_rt";

    /**
     * 记录每个接口的平均 tps
     */
    public static final String AVG_TPS = "avg_tps";

    /**
     * 记录每个接口的平均 rps
     */
    public static final String AVG_RPS = "avg_rps";

    /**
     * 记录每个接口的最大tps
     */
    public static final String MAX_TPS = "max_tps";

    /**
     * 记录每个接口的最大rps
     */
    public static final String MAX_RPS = "max_rps";

    /**
     * 接口发压请求总次数（返回 用于rps）
     */
    public static final String API_REQ_TOTAL_R = "api_req_total_r";

    /**
     * 接口请求总次数（返回 用于tps）
     */
    public static final String API_REQ_TOTAL_T = "api_req_total_t";

    /**
     * 接口请求成功数
     */
    public static final String API_REQ_SUCC = "api_req_succ";

    /**
     * 接口请求失败数
     */
    public static final String API_REQ_FAIL = "api_req_fail";

    /**
     * 单台agent最大承受rps量
     */
    public static final int AGENT_MAX_RPS = 15000;

    public static final String DISABLE_URL_ENCODE = "disable-url-encode";

    /**
     * 单台机器单个配置项最多允许使用10000条数据，防止OOM
     */
    public static final int AGENT_MAX_TRAFFIC_SIZE = 10000;
}
