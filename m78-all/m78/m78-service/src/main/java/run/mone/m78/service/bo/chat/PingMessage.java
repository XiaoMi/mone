package run.mone.m78.service.bo.chat;

import lombok.Data;
import run.mone.m78.service.bo.BaseMessage;

/**
 * @author goodjava@qq.com
 * @date 2024/5/9 15:43
 */
@Data
public class PingMessage extends BaseMessage {

    private String data = "pong";
}
