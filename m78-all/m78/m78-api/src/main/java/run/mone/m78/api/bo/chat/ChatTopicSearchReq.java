package run.mone.m78.api.bo.chat;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/5/21
 */
@Data
public class ChatTopicSearchReq implements Serializable {
    private Integer appId;
    private Integer botId;
    private Integer topicType;
    private boolean createIfNotExist;
    private String username;
}
