
package run.mone.moner.server.role.actions;

import com.google.gson.JsonParser;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;
import run.mone.moner.server.common.Result;

/**
 * @author goodjava@qq.com
 * @date 2023/6/13 10:30
 * 下雪效果
 */
public class CodeAction extends Action {

    public CodeAction() {
        setName("CodeAction");
        setDescription("""
                #.代码TOOL(如果你发现可以通过javascript代码可以来实现的功能,你直接生成代码然后放到code中,如果你需要通过code来实现,可以使用这个工具)
                code的格式是一段可执行的javascript代码,类似:alert(123);
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>CodeAction</tool_name>
                <arguments>
                {
                    "name": "$name"
                    "code": $code
                }
                </arguments>
                </use_mcp_tool>
                """);
        setFunction((req, action, ctx) -> {
                    Result result = (Result) req.getMessage().getData();
                    String code = JsonParser.parseString(result.getKeyValuePairs().get("arguments")).getAsJsonObject().get("code").getAsString();
                    return Message.builder().content("""
                            // 注入下雪效果的JavaScript
                            <action type="script" code="%s">
                            </action>
                            """.formatted(code)).build();
                }


        );
    }
}
