package com.xiaomi.youpin.tesla.ip.consumer;

import com.intellij.openapi.editor.Editor;
import com.xiaomi.youpin.tesla.ip.bo.AiMessage;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.MessageConsumer;
import com.xiaomi.youpin.tesla.ip.common.NotificationCenter;
import com.xiaomi.youpin.tesla.ip.service.CodeService;

/**
 * @author goodjava@qq.com
 * @date 2023/7/16 09:25
 */
public class CodeConsumer extends MessageConsumer {

    private Editor editor;

    private GenerateCodeReq req;

    public CodeConsumer(Editor editor, GenerateCodeReq req) {
        this.editor = editor;
        this.req = req;
    }

    @Override
    public void onEvent(AiMessage message) {
        CodeService.writeCode4(req.getProject(), editor, message.getText(), false);
    }

    @Override
    public void end(AiMessage message) {
//        NotificationCenter.notice(com.xiaomi.youpin.tesla.ip.bo.Message.finishMsg);
    }

}
