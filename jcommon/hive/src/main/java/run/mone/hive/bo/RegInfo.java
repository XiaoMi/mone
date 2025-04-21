package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hive.roles.tool.ITool;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/4/16 09:38
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegInfo implements Serializable {

    private String name;

    private String version;

    private String group;

    private String ip;

    private int port;

    private Map<String, ITool> toolMap;

    private Map<String, ITool> mcpToolMap;

}
