
package run.mone.moner.server.role.actions;

import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;

/**
 * @author goodjava@qq.com
 * @date 2023/6/13 10:30
 * 下雪效果
 */
public class SnowAction extends Action {

    public SnowAction() {
        setName("SnowAction");
        setDescription("""
                #.下雪效果TOOL(如果你想在页面上添加下雪效果,可以使用这个工具)
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>SnowAction</tool_name>
                <arguments>
                {
                    "name": "添加下雪效果"
                }
                </arguments>
                </use_mcp_tool>
                """);
        setFunction((req, action, ctx) -> Message.builder().content("""
                // 注入下雪效果的JavaScript
                <action type="script" code="(function() {alert(123);})();">
                </action>
                """).build());
    }
}
