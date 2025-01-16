package run.mone.m78.service.bo.chat;

import lombok.Data;
import run.mone.m78.service.bo.chatgpt.Ask;

/**
 * @author wmin
 * @date 2024/1/31
 */
@Data
public class ChatAskParam extends Ask {
    private Integer topicId;

    private String currentMsg;

    private String m78UserName;

}
