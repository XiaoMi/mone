package run.mone.hive.configs;

/**
 * @author goodjava@qq.com
 * @date 2025/3/13 16:16
 */
public class Const {

    public static String MC_CLIENT_ID = "MC_CLIENT_ID";

    public static String PROJECT_NAME = "__PROJECT__";

    public static int GRPC_PORT = 50051;

    public static final String CLIENT_ID = "clientId";

    public static final String TIMEOUT = "timeout";

    public static final String USER_ID = "__user_id__";

    public static final String USER_INTERNAL_NAME = "__user_internal_name__";

    // User info from Bearer Token authentication
    /**
     * Complete user information Map from token validation
     * Contains all user fields extracted from the authentication endpoint
     */
    public static final String USER_INFO = "__userInfo";

    /**
     * User ID extracted from token validation (convenience field)
     */
    public static final String TOKEN_USER_ID = "__userId";

    /**
     * Username extracted from token validation (convenience field)
     */
    public static final String TOKEN_USERNAME = "__username";

    public static final String AGENT_ID = "__agent_id__";

    public static final String SEND_FROM = "__send_from__";

    public static final String SEND_TO = "__send_to__";

    public static final String ROLE = "__role__";

    //有这个agent的拥有者
    public static final String OWNER_ID = "__owner_id__";

    public static final String TOKEN = "token";

    public static final String HIVE_VERSION = "2025-03-28:0.0.1";

    public static final String NOTIFY_MSG = "msg";

    public static final String NOTIFY_HIVE_MANAGER = "notify_hive_manager";

    //退出agent
    public static final String ROLE_EXIT = "___exit___";

    //清空agent记忆
    public static final String CLEAR_HISTORY = "___clear_history___";

    //刷新配置
    public static final String REFRESH_CONFIG = "___refresh_config___";

    public static final String DEFAULT = "default";

    public static final String INTERNAL_SERVER = "internalServer";

    public static final String AGENT_CONFIG = "__agent_config__";

    public static final String SWITCH_AGENT = "__switch_agent__";

    public static final String WORKSPACE_PATH_KEY = "workspacePath";

    public static final String SKILLS_PATH_KEY = "skillsPath";

    public static final String MCP = "mcp";

    public static final String REPLY = "reply";

    public static final String BLOCK = "block";

    public static final String RES_ID = "resId";

    public static final String CMD = "cmd";

    // Agent 相关
    public static final String AGENT_SERVER_NAME = "agent_server_name";

    public static final String NAME = "name";

    public static final String PROFILE = "profile";

    public static final String GOAL = "goal";

    public static final String CONSTRAINTS = "constraints";

    public static final String WORKFLOW = "workflow";

    public static final String META = "meta";

    public static final String HTTP_PORT = "http.port";

    public static final String HTTP_ENABLE_AUTH = "http.enable.auth";

    public static final String HTTP_ENDPOINT = "http.endpoint";

    public static final String HTTP_KEEPALIVE_SECONDS = "http.keepalive.seconds";

    public static final String HTTP_DISALLOW_DELETE = "http.disallow.delete";

    public static final String AGENT_SERVER_VERSION = "agent_server_version";

    public static final String TASK_PROGRESS = "task_progress";

    public static final String TRUE = "true";

}
