package run.mone.m78.client.flow;

import lombok.extern.slf4j.Slf4j;
import run.mone.m78.client.model.M78Message;
import run.mone.m78.client.model.M78MessageType;

import java.util.function.Consumer;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 16:40
 */
@Slf4j
public class DefaultFlowMsgConsumer implements Consumer<M78Message> {

    @Override
    public void accept(M78Message m78Message) {
        int msgLength = m78Message.getMessage().length();
        log.info("receive m78 message type:{}, size:{}", m78Message.getType().name(), msgLength);
        handleFlowMsg(m78Message, m78Message.getType());
    }

    protected void handleFlowMsg(M78Message m78Message, M78MessageType messageType) {
        switch (messageType) {
            case process:
                handleFlowExecuteStatus(m78Message);
                break;
            case failure:
                handleFlowExecuteFailure(m78Message);
                break;
            default:
                log.warn("message type:{}, not a valid type, will discard!", messageType);
        }
    }

    protected void handleFlowExecuteFailure(M78Message m78Message) {
        // send this to visual
        // TODO
    }

    private void handleFlowExecuteStatus(M78Message m78Message) {
        // send this to visual
        // TODO
    }

}
