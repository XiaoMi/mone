package run.mone.m78.service.agent.multiagent.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 18:02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentResponse implements Message {

    private String content;

    private String name;

    private String cmd;

    public AgentResponse(String content, String name) {
        this.content = content;
        this.name = name;
    }

}
