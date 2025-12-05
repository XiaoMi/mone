package run.mone.moon.constants;

/**
 * 常量定义类
 */
public class Constants {
    
    // MoonCreateFunction 相关常量
    public static final String PARAM_ID = "id";
    public static final String PARAM_TENANT = "tenant";
    public static final String PARAM_IS_COPY = "isCopy";
    public static final String PARAM_ACCOUNT = "account";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_PROJECT_ID = "projectID";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_EXEC_MODE = "execMode";
    public static final String PARAM_PRIORITY = "priority";
    public static final String PARAM_EXEC_PARAM = "execParam";
    public static final String PARAM_RETRY_WAIT = "retryWait";
    public static final String PARAM_CONCURRENCY = "concurrency";
    public static final String PARAM_MACHINE = "machine";
    public static final String PARAM_SCHEDULE_MODE = "scheduleMode";
    public static final String PARAM_SCHEDULE_PARAM = "scheduleParam";
    public static final String PARAM_START_TIME = "startTime";
    public static final String PARAM_CONCURRENCY_STRATEGY = "concurrencyStrategy";
    public static final String PARAM_ALERT_TIMEOUT = "alertTimeout";
    public static final String PARAM_ALERT_TIMEOUT_LEVEL = "alertTimeoutLevel";
    public static final String PARAM_TIMEOUT = "timeout";
    public static final String PARAM_TIMEOUT_HALT = "timeoutHalt";
    public static final String PARAM_ALERT_SUCCESS = "alertSuccess";
    public static final String PARAM_ALERT_SUCCESS_LEVEL = "alertSuccessLevel";
    public static final String PARAM_ALERT_FAIL = "alertFail";
    public static final String PARAM_ALERT_FAIL_LEVEL = "alertFailLevel";
    public static final String PARAM_ALERT_STOP = "alertStop";
    public static final String PARAM_ALERT_STOP_LEVEL = "alertStopLevel";
    public static final String PARAM_ALERT_SKIP = "alertSkip";
    public static final String PARAM_ALERT_SKIP_LEVEL = "alertSkipLevel";
    public static final String PARAM_MAX_RETRY = "maxRetry";
    public static final String PARAM_ALERT_NO_MACHINE = "alertNoMachine";
    public static final String PARAM_ALERT_NO_MACHINE_LEVEL = "alertNoMachineLevel";
    public static final String PARAM_ALERT_CONFIG = "alertConfig";
    public static final String PARAM_HISTORY_KEEP = "historyKeep";
    public static final String PARAM_FAAS_PARAM = "faasParam";
    public static final String PARAM_HTTP_PARAM = "httpParam";
    public static final String PARAM_DUBBO_PARAM = "dubboParam";
    
    // MoonQueryFunction 相关常量
    public static final String PARAM_TASK_NAME = "taskName";
    public static final String PARAM_TASK_TYPE = "taskType";
    public static final String PARAM_CREATOR = "creator";
    public static final String PARAM_SERVICE_PATH = "servicePath";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_MISCHEDULE_ID = "mischeduleID";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_PAGE_SIZE = "pageSize";
    
    // 任务类型常量
    public static final String TASK_TYPE_HTTP = "httpTask";
    public static final String TASK_TYPE_DUBBO = "dubboTask";
    public static final String TASK_TYPE_FAAS = "faasTask";
    
    // 默认值常量
    public static final String DEFAULT_ACCOUNT = "mcp_user";
    public static final String DEFAULT_EXEC_MODE = "broadcast";
    public static final String DEFAULT_PRIORITY = "P2";
    public static final String DEFAULT_EXEC_PARAM = "{}";
    public static final String DEFAULT_SCHEDULE_MODE = "cron";
    public static final String DEFAULT_SCHEDULE_PARAM = "0 0 * * * ?";
    public static final String DEFAULT_CONCURRENCY_STRATEGY = "cancel_new";
    public static final String DEFAULT_ALERT_TIMEOUT_LEVEL = "P1";
    public static final String DEFAULT_ALERT_SUCCESS_LEVEL = "P2";
    public static final String DEFAULT_ALERT_FAIL_LEVEL = "P1";
    public static final String DEFAULT_ALERT_STOP_LEVEL = "P0";
    public static final String DEFAULT_ALERT_SKIP_LEVEL = "P0";
    public static final String DEFAULT_ALERT_NO_MACHINE_LEVEL = "P0";
    public static final String DEFAULT_ALERT_CONFIG = "{}";
    public static final int DEFAULT_RETRY_WAIT = 1;
    public static final int DEFAULT_CONCURRENCY = 1;
    public static final int DEFAULT_HISTORY_KEEP = 7;
    public static final long DEFAULT_TIMEOUT = 300L;
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final boolean DEFAULT_ALERT_TIMEOUT = true;
    public static final boolean DEFAULT_TIMEOUT_HALT = true;
    public static final boolean DEFAULT_ALERT_SUCCESS = false;
    public static final boolean DEFAULT_ALERT_FAIL = true;
    public static final boolean DEFAULT_ALERT_STOP = true;
    public static final boolean DEFAULT_ALERT_SKIP = false;
    public static final boolean DEFAULT_ALERT_NO_MACHINE = true;
    public static final int DEFAULT_MAX_RETRY = 0;
}