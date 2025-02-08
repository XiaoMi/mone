package run.mone.m78.service.agent.state;

import lombok.Builder;
import lombok.Data;
import run.mone.m78.service.bo.chatgpt.PromptInfo;
import run.mone.m78.service.bo.chatgpt.PromptType;

/**
 * @author goodjava@qq.com
 * @date 2023/12/3 10:18
 */
@Data
@Builder
public class StateReq {

    private PromptInfo promptInfo;

    private PromptType promptType;

    public void reset() {
        this.promptInfo = null;
        this.promptType = null;
    }

}
