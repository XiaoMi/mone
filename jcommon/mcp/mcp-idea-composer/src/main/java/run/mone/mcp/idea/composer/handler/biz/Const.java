package run.mone.mcp.idea.composer.handler.biz;

import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 * @date 2023/6/17 09:37
 */
public interface Const {

    String MODULE_FILE_NAME = "athena_module.md";

    String BOT_ID = "botId";

    String TOKEN = "token";

    String REQ_PACKAGE = "req_package";

    String DEFAULT_REQ_PACKAGE = "run.mone.bo";

    String DISABLE_SEARCH = "disable.search";

    String AI_PROXY_CHAT = "ai.proxy.chat";

    String GIT_COMMIT_TYPE = "commit.type";

    String GIT_COMMIT_TYPE_AUTO = "auto";

    String GIT_COMMIT_TYPE_MANUAL = "manual";

    String ENABLE_ATHENA_STATUS_BAR = "enable.athena.status.bar";

    String DISABLE_ACTION_GROUP = "disable.action.group";

    String BIZ_WRITE = "biz_write";

    String OPEN_AI_KEY = "open_ai_key";

    String OPEN_AI_PROXY = "open_ai_proxy";

    //直接本地调用chatgpt(local模式)
    String OPEN_AI_LOCAL = "open_ai_local";

    //使用bot
    String USE_BOT = "use_bot";

    String INLAY = "inlay";

    //整体的代码补全功能是否关闭
    String DISABLE_CODE_COMPLETION = "DISABLE_CODE_COMPLETION";

    //开启Prompt menu
    String ENABLE_PROMPT_MENU = "enable.prompt.menu";

    String STREAM = "stream";

    String FALSE = "false";

    String TRUE = "true";

    //本地测试
    String OPEN_AI_TEST = "open.ai.test";

    //提问的时候是否开启选中的文本
    String OPEN_SELECT_TEXT = "open.select.text";

    //本地使用的模型(优先:gpt-4o)
    String OPEN_AI_MODEL = "open_ai_model";

    String SEARCH = "search";

    int UI_CANCEL_CODE = 555;

    String OPEN_GUIDE = "open.guide";

    String DEBUG = "debug";

    //多模态模式
    String VISION = "vision";

    String CODE_SCRIPT = "code.script";

    String TREE_SELECT = "tree.select";

    String BOT_URL = "bot.url";

    String INLAY_DELAY = "inlay.delay";

    String INLAY_BOT_ID = "inlay.bot.id";

    String INLAY_SCOPE = "inlay.scope";

    String INLAY_FIM = "inlay.fim";

    String INLAY_FIM_MODEL = "inlay.fim.model";

    String INLAY_QWEN_ENDPOINT = "inlay.fim.qwen.endpoint";

    String BUG_FIX_BOT_ID = "160415";

    String AI_CODE_BOT_ID = "160449";

    String FUNCTION_ANALYSIS_BOT_ID = "130462";
    String CONF_BUG_FIX_BOT = "conf.bugfix.botId";

    String CONF_AI_CODE_BOT = "conf.aicode.botId";

    String CONF_FUNCTION_ANALYSIS_BOT = "conf.funcAnalysis.botId";

    String BOT_CHAIN_TYPE = "botChainType";

    String FIX_BUG_BOT_CHAIN = "bugfix";

    String FIX_BUG_CODE_CONTEXT = "bugfixCodeContext";

    String FIX_BUG_ERROR_INFO = "bugfixErrorInfo";

    String BIZ_JAR = "bizJar";

    String CODE_BASE = "codeBase";

    String KNOWLEDGE_BASE = "knowledgeBase";

    String FILE_LIST = "fileList";

    String FOLDER = "FOLDER";

    String COMPOSER_IMAGE_CONTEXT = "composerImageContext";

    String COMPOSER_IMAGE_BASE64 = "composerImageBase64";
    String COMPOSER_IMAGE_MEDIA_TYPE = "composerImageMediaType";

    String CONF_NICK_NAME = "conf.nickName";

    String CONF_DASH_URL = "conf.dash.url";

    String CONF_DASH_URL_VALUE = "https://mone.test.mi.com/idea/#/code";
    //String CONF_DASH_URL_VALUE = "http://10.220.17.183:5173/idea#/code";

    String CONF_FLOW_URL = "https://mone.test.mi.com/ai-plugin/probot/workflow";

    String CONF_AI_PROXY_URL = "conf.ai-proxy.url";

    String CONF_KNOWLEDGE_PROXY_URL = "conf.knowledge.url";

    String CONF_PORT = "conf.port";

    String LSP_PORT = "lsp.port";

    String CONF_M78_URL = "conf.m78.url";

    String CONF_WS_URL = "conf.m78.lb.url";

    String CONF_M78_FLOW_URL = "conf.m78.flow.url";


    String CONF_M78_FLOW_SHOW_JUMP_URL = "conf.m78.flow.jump";

    String CONF_M78_SPEECH2TEXT = "conf.a2t.endpoint";

    String CONF_M78_TEXT2SPEECH = "conf.t2a.endpoint";

    String CONF_Z_KNOWLEDGE_LIST = "conf.z.knowledge.base.list";

    String CONF_Z_KNOWLEDGE_QUERY = "conf.z.knowledge.base.query";

    String CONF_KNOWLEDGE_GITLAB_PROJECT = "conf.knowledge.gitlab.project";

    String CONF_KNOWLEDGE_GITLAB_COMMITS = "conf.knowledge.gitlab.commits";

    String CONF_KNOWLEDGE_GITLAB_DIFFS = "conf.knowledge.gitlab.diffs";

    String CONF_KNOWLEDGE_GITLAB_CHAT = "conf.knowledge.gitlab.chat";


    String CONF_M78_UPLOAD_CODE_INFO = "conf.upload.info.endpoint";

    String CONF_M78_CODE_GEN_WITH_ENTER = "conf.codegen.trigger.enter";

    String CONF_M78_LOCAL_MODEL = "local.model";

    String CONF_CHAT_WITH_KNOWLEDGE = "conf.chat.knowledge";

    String CONF_KNOWLEDGE_TIMEOUT = "conf.knowledge.timeout";

    String CONF_BOT_LIST = "conf.bot.list";

    String CONF_BOT_LIST_USER = "conf.bot.list.user";

    String CONF_FORMAT_CODE = "conf.format.code";

    String CONF_PRIVATE_MODEL_INFO = "conf.private.model.info";

    String CONF_USE_MCP = "conf.use.mcp";

    String CONF_CHAT_LOCAL = "conf.chat.local";

    String PROMPT_LABEL_TYPE = "type";

    String CONF_COMPLETION_TYPE = "conf.completion.type";

    //生成代码的prompt name
    String GENERATE_CODE = "biz_sidecar";

    String GENERATE_CODE_IN_METHOD = "biz_completion";

    //添加注释
    String GENERATE_COMMENT = "comment_2";

    //生产代码的注释
    String GENERATE_CODE_COMMENT = "biz_sidecar_comment";

    //单元测试的prompt name
    String UNIT_TEST = "test_code";

    String CLASS_CTX_CHAT = "class_context_chat";

    String CLASS_ANNOTATION_GENERATE = "class_annotation_generate";

    String METHOD_CTX_CHAT = "method_context_chat";

    String CLASS_CTX_CHAT_VIEW_NAME = "***Chat上下文加入类信息：***\n```java\n%s";

    String METHOD_CTX_CHAT_VIEW_NAME = "***Chat上下文加Method信息：***\n```java\n%s";

    String CLIENT_VERSION = "clientVersion";

    String CLIENT_NAME = "clientName";

    String PLUGIN_NAME = "Mione";

    String MIONE_CONSOLE_NAME = "MioneConsole";

    // 右侧聊天用的prompt name
    String ATHENA_CHAT = "athena_chat";

    // 右侧聊天知识库prompt name，knowledge-doc
    String KNOWLEDGE_DOC = "knowledge-doc";
    // 右侧聊天知识库prompt name，knowledge-miapi
    String KNOWLEDGE_MIAPI = "knowledge-miapi";
    // 右侧聊天知识库prompt name，knowledge-git
    String KNOWLEDGE_GIT = "knowledge-git";
    // 右侧聊天知识库prompt name，knowledge-code
    String KNOWLEDGE_CODE = "knowledge-code";

    String KNOWLEDGE_FILE = "knowledge-file";
    String KNOWLEDGE_EMBEDDING_URI = "knowledge-embedding-query-uri";
    String KNOWLEDGE_QUERY_URI = "knowledge-path-query-uri";

    // 代码检查的prompt name
    String CODE_REVIEW = "bot_stream";

    String GIT_PUSH = "bot_call";

    // git提交信息的prompt name
    String GIT_CMT_MSG = "git-commit-msg";

    // 配置用于控制右侧聊天是否走BOT逻辑，默认走
    String CONF_CHAT_USE_BOT = "conf.chat.use.bot";

    // 执行BOT传递参数的模型的key
    String AI_MODEL_PARAM_KEY = "model";

    // 使用BOT默认模型的选项
    String USE_BOT_MODEL = "默认";

    // 生成单测prompt name
    String GENERATE_UNIT_TEST = "generate_unit_test";

    String GENERATE_ALL_UNIT_TEST = "generate_all_unit_test";

    String SHOW_LEGACY = "legacy";

    String INLAY_CONTEXT_LIMIT = "inlay.context.limit";

    String INLAY_ACCEPT_TYPE = "inlay.accept.type";

    String INLAY_CMT_BASED_GEN = "inlay.comment.based.gen";


    String INLAY_PRE_SIZE = "inlay.prefix.size";

    String INLAY_SUF_SIZE = "inlay.suffix.size";

    String HELP_DOC = "https://xiaomi.f.mioffice.cn/wiki/S0RBwt4p3iDcs5kXTOCkZNQY4Vd";

    String MODEL_TEMPLATE_DEEPSEEK = "<｜fim▁begin｜>${PRE}<｜fim▁hole｜>${SUF}<｜fim▁end｜>";

    String MODEL_TEMPLATE_QWEN = "<fim_prefix>${PRE}<fim_suffix>${SUF}<fim_middle>";

    String INLAY_RENDER_WITH_TYPING_EFFECT = "inlay.render.effect.typing";

    String QWEN_CODE_BATCH_API_URL = "http://ak-b2c-systech-mione-master01.bj.idc.xiaomi.com/algo/api/v1/code/batch";

    String QWEN_CODE_BATCH_API_URL_LEGACY = "http://qwen.pt.miui.com/algo/api/v1/code/batch";

    String QWEN_AUTH_TOKEN = "Bearer EJKYNSdH4FpxhmRqtQUEkt3plXiYc0i2";
    Pattern QWEN_ERROR_JSON_PATTERN = Pattern.compile("^\\{.*\"success\":false.*\\}$");
    String COMMERCE_QWEN_MODEL = "commerce_codeqwen";

    String TENCENT_CODE_BATCH_API_URL = "http://llm.pt.miui.com/api/gpthub/complete";
    String TENCENT_AUTH_TOKEN = "Bearer 5ZCI5L2c5LyZ5Ly0QVBJ6K6/6ZeudG9rZW4=";
    String COMMERCE_TENCENT_MODEL = "commerce_tencent";


    String USE_BOT_IN_CHAT_GEN = "chat.gen.code.use.bot";

    String PROMPT_GUID_LABEL = "athena.guid";
    String PROMPT_GUID_CN_LABEL = "athena.guid_cn";
    String PROMPT_GUID_CODE_WRAP = "athena.guid.code_wrap";

    String CHAT_PLACE_HOLDER_VIEW = "<#@view>";
    String CHAT_PLACE_HOLDER_FILE = "<#@file>";
    String CHAT_PLACE_HOLDER_LINE = "<#@line>";
    String CHAT_PLACE_HOLDER_SELECT = "<#@select>";


    String TASK_TITLE = "mione";

    String LABEL_KEY_DISPLAY_NAME = "display_name";
    String CONF_TOOL_BAR_IMPL = "conf.tool.bar.impl";

    String TOOL_BAR_IMPL_LINE_EDITOR = "lineEditor";

    String TOOL_BAR_IMPL_POP_UP = "popup";
    String CONF_DIFF_BY_BOT = "conf.diff.via.bot";

    String ROLE = "_role_";

    String ROLE_EXIT = "___exit___";

    String IDEA_MCP_FROM = "idea_mcp";
}
