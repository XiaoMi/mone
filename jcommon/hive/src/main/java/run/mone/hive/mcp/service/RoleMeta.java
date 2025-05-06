package run.mone.hive.mcp.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.roles.tool.ITool;

import java.util.List;

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

    //只可以内部调用
    protected List<ITool> tools;

    //外边用户也可以调用
    protected List<McpFunction> mcpTools;

}
