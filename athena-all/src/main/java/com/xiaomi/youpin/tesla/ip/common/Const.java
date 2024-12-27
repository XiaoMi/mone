package com.xiaomi.youpin.tesla.ip.common;

import com.intellij.openapi.util.Key;
import com.intellij.util.messages.Topic;
import run.mone.ultraman.listener.AthenaMessageListener;

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

    String CONF_NICK_NAME = "conf.nickName";

    String CONF_DASH_URL = "conf.dash.url";

    String CONF_AI_PROXY_URL = "conf.ai-proxy.url";

    String CONF_PORT = "conf.port";

    Key<String> T_KEY = new Key<>("t");

    Topic<AthenaMessageListener> ATHENA_TOPIC = new Topic<AthenaMessageListener>(AthenaMessageListener.class);

    String CONF_M78_URL = "conf.m78.url";

    String CONF_M78_SPEECH2TEXT = "conf.a2t.endpoint";

    String CONF_M78_TEXT2SPEECH = "conf.t2a.endpoint";

    String CONF_M78_UPLOAD_CODE_INFO = "conf.upload.info.endpoint";

    String CONF_M78_CODE_GEN_WITH_ENTER = "conf.codegen.trigger.enter";

    String PROMPT_LABEL_TYPE = "type";

    String CONF_COMPLETION_TYPE = "conf.completion.type";

    //生成代码的prompt name
    String GENERATE_CODE = "biz_sidecar";

    //生产代码的注释
    String GENERATE_CODE_COMMENT = "biz_sidecar_comment";

    //单元测试的prompt name
    String UNIT_TEST = "test_code";

    String CLIENT_VERSION = "clientVersion";

    String CLIENT_NAME = "clientName";

    String PLUGIN_NAME = "Mione";

    String MIONE_CONSOLE_NAME = "MioneConsole";

    // 右侧聊天用的prompt name
    String ATHENA_CHAT = "athena_chat";

    // 代码检查的prompt name
    String CODE_REVIEW = "bot_stream";

    // 配置用于控制右侧聊天是否走BOT逻辑，默认走
    String CONF_CHAT_USE_BOT = "conf.chat.use.bot";

    // 执行BOT传递参数的模型的key
    String AI_MODEL_PARAM_KEY = "model";

    // 使用BOT默认模型的选项
    String USE_BOT_MODEL = "默认";

    // 生成单测prompt name
    String GENERATE_UNIT_TEST = "generate_unit_test";

}
