package run.mone.hive.prompt;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.roles.tool.ITool;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/4/10 11:17
 */
public class PromptTest {


//    @Test
//    public void testPrompt() {
//        List<ITool> tools = Lists.newArrayList(new ChatTool(), new AskTool(), new AttemptCompletionTool());
//        List<McpSchema.Tool> mcpTools = Lists.newArrayList(new McpSchema.Tool("name", "d", "{}"));
//        String prompt = MonerSystemPrompt.mcpPrompt(null, "", "default", "zzy", "", tools, mcpTools);
//        System.out.println(prompt);
//    }

}
