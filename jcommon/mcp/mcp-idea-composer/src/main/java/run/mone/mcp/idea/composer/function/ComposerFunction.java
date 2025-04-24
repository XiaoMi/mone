package run.mone.mcp.idea.composer.function;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.composer.config.Const;
import run.mone.mcp.idea.composer.handler.biz.BotChainCall;
import run.mone.mcp.idea.composer.handler.biz.BotChainCallContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class ComposerFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    public ComposerFunction(String port) {
        this.ideaPort = port;
    }

    private static String name = "stream_composer";

    private static String desc = "根据需求或者需求图片，生成业务代码或者单元测试等一切涉及项目的改动，支持创建新文件、修改现有代码、重构代码结构、添加注释文档、修复bug、添加高危元素注解、更新配置文件（如pom.xml）、生成API接口、编写数据库操作逻辑等。可以处理项目中的任何文件类型，包括但不限于Java、XML、YAML、JSON、SQL等。如果有图片，无需知道图片内容，只按要求返回即可";

    public static String getName() {
        return name;
    }

    public static String getDesc(String agentName) {
        return desc;
    }

    public static String getToolScheme() {
        return toolScheme;
    }

    private String ideaPort;

    private static String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "requirement": {
                        "type": "string",
                        "description":"需求描述，用户输入什么就传什么，不要有任何更改，否则会有不好的事情发生"
                    },
                    "fileLists": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        },
                        "description":"文件列表，根据需求分析出来要操作的文件数组，如果没有，则不需要返回"
                    }
            
                },
                "required": ["requirement"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        try {
            JsonObject req = new JsonObject();
            req.addProperty("from", "idea_mcp");
            req.addProperty("requirement", (String) arguments.get("requirement"));
            req.addProperty("projectName", (String) arguments.get("projectName"));
            req.add("fileLists", JsonParser.parseString((String) arguments.get("fileLists")));
            req.addProperty("folder", (String) arguments.get("folder"));
            req.addProperty("codebase", (Boolean) arguments.get("codebase"));
            req.addProperty("analyze", (Boolean) arguments.get("analyze"));
            req.addProperty("bizJar", (Boolean) arguments.get("bizJar"));
            req.addProperty("bugfix", (Boolean) arguments.get("bugfix"));
            req.addProperty("knowledgeBase", (Boolean) arguments.get("knowledgeBase"));
            req.addProperty("imageType", (String) arguments.get("imageType"));
            req.addProperty("imageBase64", (String) arguments.get("imageBase64"));
            req.addProperty("methodCode", (String) arguments.get("methodCode"));
            req.addProperty("rules", (String) arguments.get("rules"));
            String athenaPluginIp = StringUtils.isEmpty((String) arguments.get("athenaPluginIp")) ? Const.IP : (String) arguments.get("athenaPluginIp");
            req.addProperty("athenaPluginIp", athenaPluginIp);
            req.addProperty("athenaPluginHost", athenaPluginIp + ":" + ideaPort);

            // retry
            String isFull = (String) arguments.get("retryIsFull");

            return Flux.<String>create(fluxSink -> {
                try {
                    BotChainCall call = new BotChainCall();
                    BotChainCallContext context = BotChainCallContext.of("", fluxSink);
                    completeBotContext(context, req);
                    call.executeProjectBotChain(context, req, isFull);
                } catch (Throwable a) {
                    log.error("ERROR create Flux, e: ", a);
                    System.out.println("ERROR create Flux, msg: " + a.getMessage());
                }
            }).map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        } catch (Exception e) {
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
        }
    }

    private void completeBotContext(BotChainCallContext context, JsonObject req) {
        boolean bizJar = get(req, "bizJar", false);
        boolean bugfix = get(req, "bugfix", false);
        boolean knowledgeBase = get(req, "knowledgeBase", false);
        String imageBase64 = get(req, "imageBase64", "");
        String requirement = get(req, "requirement", "");
        if (bugfix) {
            context.addParam(run.mone.mcp.idea.composer.handler.biz.Const.BOT_CHAIN_TYPE, run.mone.mcp.idea.composer.handler.biz.Const.FIX_BUG_BOT_CHAIN);
            context.addParam(run.mone.mcp.idea.composer.handler.biz.Const.FIX_BUG_CODE_CONTEXT, requirement);
            context.addParam(run.mone.mcp.idea.composer.handler.biz.Const.FIX_BUG_ERROR_INFO, requirement);
        }
        if (StringUtils.isNotEmpty(imageBase64)) {
            context.addParam(run.mone.mcp.idea.composer.handler.biz.Const.COMPOSER_IMAGE_CONTEXT, imageBase64);
        }
        //允许读取务的jar代码(一个biz.md文件)
        if (bizJar) {
            context.addParam(run.mone.mcp.idea.composer.handler.biz.Const.BIZ_JAR, true);
        }

        //项目的知识库
        if (knowledgeBase) {
            context.addParam(run.mone.mcp.idea.composer.handler.biz.Const.KNOWLEDGE_BASE, true);
        }

    }

    private boolean get(JsonObject obj, String key, boolean defaultValue) {
        if (obj.has(key)) {
            if(!(obj.get(key) instanceof JsonNull)) {
                return Boolean.valueOf(obj.get(key).getAsString());
            }
        }
        return defaultValue;
    }

    private String get(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key)) {
            if(!(obj.get(key) instanceof JsonNull)) {
                return obj.get(key).getAsString();
            }
        }
        return defaultValue;
    }

}
