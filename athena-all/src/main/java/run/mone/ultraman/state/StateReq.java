package run.mone.ultraman.state;

import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
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
