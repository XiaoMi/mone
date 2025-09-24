package run.mone.hive.mcp.hub;

import lombok.Getter;
import lombok.Setter;

/**
 * @author goodjava@qq.com
 * @date 2025/3/16 15:57
 */
public class McpConfig {

    @Setter
    @Getter
    private String clientId;


    private static final class LazyHolder {
        private static final McpConfig ins = new McpConfig();
    }


    public static final McpConfig ins() {
        return LazyHolder.ins;
    }


}
