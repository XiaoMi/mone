package run.mone.hive.mcp.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/4/28 11:08
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleMeta {

    protected String profile;

    protected String goal;

    protected String constraints;

}
