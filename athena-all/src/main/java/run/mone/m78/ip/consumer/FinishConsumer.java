package run.mone.m78.ip.consumer;

import run.mone.m78.ip.bo.AiMessage;
import run.mone.m78.ip.bo.MessageConsumer;

/**
 * @author goodjava@qq.com
 * @date 2023/7/23 11:43
 */
public class FinishConsumer extends MessageConsumer {

    private StringBuilder sb = new StringBuilder();

    @Override
    public void begin(AiMessage message) {
        super.begin(message);
    }

    @Override
    public void onEvent(AiMessage message) {
        sb.append(message.getText());
    }

    public String getContent() {
        return this.sb.toString();
    }

    @Override
    public void end(AiMessage message) {

    }
}
