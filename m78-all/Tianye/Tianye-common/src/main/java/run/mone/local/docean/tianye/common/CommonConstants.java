package run.mone.local.docean.tianye.common;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/27/24 16:31
 */
public class CommonConstants {
    // TODO
    public static final String TY_CODE_INPUT_MARK = "$$TY_CODE$$";

    public static final String TY_SQL_INPUT_MARK = "$$TY_SQL$$";

    public static final String TY_PLUGIN_ID_MARK = "$$TY_PLUGIN_ID$$";

    //flow编辑时固定选择的知识库id
    public static final String TY_KNOWLEDGE_ID_MARK = "$$TY_KNOWLEDGE_ID$$";

    //动态知识库id
    public static final String TY_KNOWLEDGE_REF_BASE_ID_MARK = "$$TY_KNOWLEDGE_REF_BASE_ID$$";

    public static final String TY_KNOWLEDGE_MAX_RECALL_MARK = "$$TY_KNOWLEDGE_MAX_RECALL$$";

    public static final String TY_KNOWLEDGE_MIN_MATCH_MARK = "$$TY_KNOWLEDGE_MIN_MATCH$$";

    public static final String TY_KNOWLEDGE_QUERY_MARK = "$$TY_KNOWLEDGE_QUERY$$";

    public static final String TY_LLM_IMAGE_UNDERSTAND_MARK = "$$TY_LLM_IMAGE_UNDERSTAND$$";
    public static final String TY_LLM_PDF_UNDERSTAND_MARK = "$$TY_LLM_PDF_UNDERSTAND$$";
    public static final String TY_LLM_PDF_PREFIX = "llm_pdf:";

    public static final String TY_LLM_PDF_SIGN = "pdf";

    //图片相关
    public static final String TY_LLM_IMAGE_MARK = "$$TY_LLM_IMAGE$$";

    //PDF相关
    public static final String TY_LLM_PDF_MARK = "$$TY_LLM_PDF$$";

    public static final String TY_LLM_PROMPT_MARK = "$$TY_LLM_PROMPT$$";

    public static final String TY_LLM_MODEL_MARK = "$$TY_LLM_MODEL$$";

    public static final String TY_LLM_USE_CACHE = "USE_CACHE";

    //批处理还是单条处理(single batch)
    public static final String TY_PROCESS_TYPE = "$$TY_PROCESS_TYPE$$";

    public static final String TY_LLM_TIMEOUT_MARK = "$$TY_LLM_TIMEOUT$$";

    public static final String TY_LLM_TEMPERATURE_MARK = "$$TY_LLM_TEMPERATURE$$";

    public static final String TY_END_MESSAGE_CONTENT_MARK = "$$TY_END_MESSAGE_CONTENT$$";

    // 批处理最大循环次数
    public static final String TY_BATCH_MAX_TIMES_MARK = "$$TY_BATCH_MAX_TIMES$$";

    //每批次时间间隔
    public static final String TY_BATCH_TIME_INTERVAL_MARK = "$$TY_BATCH_TIME_INTERVAL$$";

    public static final String TY_USERNAME_MARK = "$$TY_USERNAME$$";

    public static final String TY_HISTORY_MARK = "__TY_HISTORY__";

    public static final String TY_PLUGIN_DUBBO_PARAMS_MARK = "$$TY_PLUGIN_DUBBO_PARAMS$$";

    public static final String TY_PLUGIN_DUBBO_TIMEOUT_MARK = "$$TY_PLUGIN_DUBBO_TIMEOUT$$";

    public static final String TY_INTENT_QUERY_MARK = "$$TY_INTENT_QUERY$$";

    public static final String TY_INTENT_MATCH_MARK = "$$TY_INTENT_MATCH$$";

    public static final String TY_SUB_FLOW_ID_MARK = "$$TY_SUB_FLOW_ID$$";

    public static final String TY_WORK_CHART_TYPE_MARK = "$$TY_WORK_CHART_TYPE$$";

    public static final String TY_INPUT_MARK = "$$TY_INPUT$$";

    public static final String TY_STYLE_INDEX_MARK = "$$TY_STYLE_INDEX$$";

    public static final String TY_BASE_IMAGE_URL_MARK = "$$TY_BASE_IMAGE_URL$$";

    public static final String TY_LLM_DEBUG_MARK = "TY_LLM_DEBUG";

    //新版选择器配置
    public static final String TY_PRECONDITION_EXPRESS_MARK = "TY_PRECONDITION_EXPRESS";



    //平台特殊出参
    public static final String TY_ELAPSED_TIME_MARK = "$$TY_ELAPSED_TIME$$";

    public static final String TY_CODE_LOG_MARK = "$$TY_CODE_LOG$$";

    public static final String TY_LLM_QUESTION_MARK = "$$TY_LLM_QUESTION$$";

    public static final String TY_LLM_ANSWER_MARK = "$$TY_LLM_ANSWER$$";

    public static final String TY_SUB_FLOW_RECORD_ID_MARK = "$$TY_SUB_FLOW_RECORD_ID$$";
}
