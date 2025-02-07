package run.mone.m78.service.agent.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/5/26 11:59
 */
@Data
@Builder
public class ChatSetup {

    private String sessionId;

    private String topicId;

    private String user;

    private String fsmKey;

    private String model;

}
