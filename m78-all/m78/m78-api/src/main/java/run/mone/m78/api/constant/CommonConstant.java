package run.mone.m78.api.constant;


/**
 * @author bot
 */
public class CommonConstant {
    public final static int DEF_DUBBO_TIMEOUT = 30000;

    public static final int PRESET_QUESTION_TIMEOUT = 60 * 1000;

    public static final int AGENT_RPC_TIMEOUT= 180 * 1000;

    public static final long SSE_TIMEOUT = 600L * 1000L;

    public static final int GENERATE_PIC_TIMEOUT = 10 * 1000;

    public static final int PLUGIN_CALL_TIMEOUT = 60 * 1000;

    public static final int AI_TABLE_TIMEOUT = 120; // in second
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    public static final String API_VERSION = "v1";

    public static final String API_PREFIX = "/api/" + API_VERSION;

    public static final String OPEN_API_PREFIX = "/open-apis/" + API_VERSION + "/ai-plugin-new";

    public static final String SUCCESS = "0";

    public static final String TABLE_CREATED = "3";

    public static final String PROCESSING = "2";

    public static final String DONE = "1";

    public static final String FAILED = "500";


    public static final int STATUS_OK = 0;

    public static final String COLUMN_NAME = "COLUMN_NAME";

    public static double SIM_THRESHOLD = 0.8;

    public static final String TABLE_USER_DISABLE = "no_user";

    public static final int MULTIMODAL_PDF_LIMIT = 10;
}