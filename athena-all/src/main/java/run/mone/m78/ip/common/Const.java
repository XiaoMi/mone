package run.mone.m78.ip.common;

import com.intellij.openapi.util.Key;
import com.intellij.util.messages.Topic;
import run.mone.ultraman.listener.AthenaMessageListener;

/**
 * @author goodjava@qq.com
 * @date 2023/6/17 09:37
 */
public interface Const {

    String MODULE_FILE_NAME = "";

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

    //本地测试
    String OPEN_AI_TEST = "open.ai.test";

    //提问的时候是否开启选中的文本
    String OPEN_SELECT_TEXT = "open.select.text";

    String OPEN_AI_MODEL = "open_ai_model";

    String SEARCH = "search";

    int UI_CANCEL_CODE = 555;

    String OPEN_GUIDE = "open.guide";

    String DEBUG = "debug";

    //多模态模式
    String VISION = "vision";

    String CODE_SCRIPT = "code.script";

    String TREE_SELECT = "tree.select";

    Key<String> T_KEY = new Key<>("t");

    Topic<AthenaMessageListener> ATHENA_TOPIC = new Topic<AthenaMessageListener>(AthenaMessageListener.class);


}
