package run.mone.m78.ip.consumer;

import com.intellij.openapi.editor.Editor;
import run.mone.m78.ip.bo.AiMessage;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.MessageConsumer;
import run.mone.m78.ip.service.CodeService;

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
    }

}
