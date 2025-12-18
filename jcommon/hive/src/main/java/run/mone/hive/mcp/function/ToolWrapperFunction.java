package run.mone.hive.mcp.function;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.ITool;

import java.util.List;
import java.util.Map;

/**
 * Wrapper class to convert ITool to McpFunction
 *
 * This class allows traditional ITool implementations to be used as McpFunction,
 * enabling them to be registered in mcpTools for MCP protocol support.
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
public class ToolWrapperFunction implements McpFunction {

    private final ITool tool;
    private final Gson gson = new Gson();

    /**
     * Constructor
     *
     * @param tool The ITool instance to wrap
     */
    public ToolWrapperFunction(ITool tool) {
        this.tool = tool;
    }

    @Override
    public String getName() {
        return tool.getName();
    }

    @Override
    public String getDesc() {
        return tool.description();
    }

    @Override
    public String getToolScheme() {
        // 创建一个通用的 JSON Schema
        // 由于 ITool 不提供结构化的参数定义，我们创建一个接受任意属性的 schema
        return """
                {
                    "type": "object",
                    "properties": {},
                    "additionalProperties": true
                }
                """;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                log.info("ToolWrapperFunction executing tool: {}, args: {}", tool.getName(), args);

                // 将 Map<String, Object> 转换为 JsonObject
                JsonObject inputJson = convertMapToJsonObject(args);

                // 调用 ITool 的 execute 方法
                // 注意：传递 null 作为 ReactorRole，因为在 MCP 上下文中可能没有 Role
                JsonObject result = tool.execute(null, inputJson);

                // 检查结果
                if (result == null) {
                    log.warn("Tool {} returned null result", tool.getName());
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Tool execution completed but returned no result")), true));
                }

                // 检查是否有错误
                if (result.has("error")) {
                    String error = result.get("error").getAsString();
                    log.error("Tool {} execution failed: {}", tool.getName(), error);
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Error: " + error)), true));
                }

                // 将 JsonObject 结果转换为格式化的字符串
                String resultText = formatToolResult(result);

                log.info("Tool {} execution succeeded", tool.getName());
                return createSuccessFlux(resultText);

            } catch (Exception e) {
                log.error("Tool {} execution failed with exception", tool.getName(), e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Execution failed: " + e.getMessage())), true));
            }
        });
    }

    /**
     * 将 Map 转换为 JsonObject
     *
     * @param map 输入的参数 Map
     * @return JsonObject
     */
    private JsonObject convertMapToJsonObject(Map<String, Object> map) {
        // 先转换为 JSON 字符串，再解析为 JsonObject，以保持类型信息
        String json = gson.toJson(map);
        return gson.fromJson(json, JsonObject.class);
    }

    /**
     * 格式化工具结果
     *
     * @param result 工具执行结果
     * @return 格式化后的字符串
     */
    private String formatToolResult(JsonObject result) {
        // 如果结果包含 success 字段，提取主要信息
        if (result.has("success") && result.get("success").getAsBoolean()) {
            // 优先返回 message 或 content 字段
            if (result.has("message")) {
                return result.get("message").getAsString();
            }
            if (result.has("content")) {
                JsonElement content = result.get("content");
                if (content.isJsonPrimitive()) {
                    return content.getAsString();
                }
            }
        }

        // 否则返回美化后的 JSON
        return gson.toJson(result);
    }

    /**
     * 创建成功响应的Flux
     *
     * @param result 操作结果
     * @return 包含结果和完成标记的Flux
     */
    private Flux<McpSchema.CallToolResult> createSuccessFlux(String result) {
        return Flux.just(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false),
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[DONE]")), false)
        );
    }
}
