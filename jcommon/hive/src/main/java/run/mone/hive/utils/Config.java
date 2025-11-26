package run.mone.hive.utils;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/12/25 21:46
 */
@Data
public class Config {
    
    public boolean isInc() {
        return false;
    }

    private String userId;

    private String agentId;

    private String clientId;
}
