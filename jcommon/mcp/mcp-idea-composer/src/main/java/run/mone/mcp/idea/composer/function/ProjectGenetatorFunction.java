package run.mone.mcp.idea.composer.function;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import run.mone.hive.common.Constants;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.composer.config.Const;
import run.mone.mcp.idea.composer.handler.ProjectGeneratorTeam;
import run.mone.mcp.idea.composer.handler.ConversationContext;
import run.mone.mcp.idea.composer.handler.biz.BotChainCallContext;

import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class ProjectGenetatorFunction implements McpFunction {

    public ProjectGenetatorFunction() {
    }

    private static String name = "stream_project_generator";

    private static String desc = "创建新项目，只需要用一句话描述你想要的项目即可，比如：帮我创建一个图书管理系统";


    private static String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "requirement": {
                        "type": "string",
                        "description": "用一句话描述你想要创建的项目，比如：帮我创建一个图书管理系统"
                    }
                },
                "required": ["requirement"]
            }
            """;

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getToolScheme() {
        return toolScheme;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("from", "idea_mcp");
            req.addProperty("requirement", (String) arguments.get("requirement"));

            return Flux.<String>create(fluxSink -> {
                try {
                    String result = createProject(req);
                    fluxSink.next(result);
                    fluxSink.complete();
                } catch (Throwable a) {
                    log.error("Error creating project: ", a);
                    fluxSink.error(a);
                }
            }).map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        } catch (Exception e) {
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
        }
    }

    private String createProject(JsonObject req) {
        // 创建上下文对象
        BotChainCallContext botChainCallContext = new BotChainCallContext();
        ConversationContext conversationContext = new ConversationContext();
        conversationContext.setUserQuery(req.get("requirement").getAsString());

        // 调用ProjectGeneratorTeam生成项目
        ProjectGeneratorTeam.generateProject(
            req.get("requirement").getAsString(),  // 自然语言需求
            botChainCallContext,                   // Bot链上下文
            conversationContext,                   // 对话上下文
            null                                   // 不需要提供json，让AI分析需求
        );

        return "开始创建项目，需求：" + req.get("requirement").getAsString();
    }
} 