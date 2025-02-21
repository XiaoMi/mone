package run.mone.moner.server.role.actions;

import com.google.gson.JsonParser;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;
import run.mone.moner.server.common.Result;

public class ClickAfterRefresh extends Action {

    public ClickAfterRefresh() {
        super("ClickAfterRefresh", "");
        setDescription("""
                #.点击某个element(elementId需要你分析出来),然后刷新页面并构建dom树TOOL
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>ClickAfterRefresh</tool_name>
                <arguments>
                {
                   "elementId":$elementId
                }
                </arguments>
                </use_mcp_tool>
                """);
        setFunction((req, action, ctx) -> {
            Result result = (Result) req.getMessage().getData();
            String elementId = JsonParser.parseString(result.getKeyValuePairs().get("arguments")).getAsJsonObject().get("elementId").getAsString();
            return Message.builder().content("""
                    //点击元素
                    <action type="action" name="click" elementId="%s">
                    </action>
                    
                    <action type="cancelBuildDomTree">
                    </action>
                    
                    <action type="pause">
                    </action>
                    
                    <action type="buildDomTree" fullPage="true">
                    </action>
                    
                    <action type="pause">
                    </action>
                    
                    <action type="screenshot" send="true" test="true">
                    </action>
                    
                    """.formatted(elementId)).build();
        });
    }
}
