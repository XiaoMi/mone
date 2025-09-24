package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/4/16 09:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthInfo {

    private String name;

    private String version;

    private String group;

    private String ip;

    private int port;

    private String token;

}
