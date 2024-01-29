package run.mone.ultraman.state;

import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.common.PromptType;
import lombok.Builder;
import lombok.Data;

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
