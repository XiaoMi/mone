package run.mone.hive.mcp.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/4/8 11:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientMeta {

    private long time;

    private String token;

}
