package run.mone.mcp.vuetemp.function;

import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.vuetemp.service.VueTemplateService;

import java.util.List;
import java.util.Map;

/**
 * Vue 模板生成功能类
 */
public class VueTemplateFunction implements McpFunction {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(VueTemplateFunction.class);

    private static final String TOOL_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "projectName": {"type": "string", "description": "项目名称"},
                "description": {"type": "string", "description": "项目描述"},
                "outputPath": {"type": "string", "description": "输出路径"}
              },
              "required": ["projectName"]
            }
            """;

    public String getName() {
        return "generate_vue_template";
    }

    public String getDesc() {
        return "生成 Vue 3 + TS + Element Plus + Pinia + Vue Router 项目模板";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.defer(() -> {
            try {
                log.info("[generate_vue_template] enter, args={}", arguments);
                String projectName = (String) arguments.get("projectName");
                String description = (String) arguments.getOrDefault(
                        "description",
                        "Vue 3 + TypeScript + Element Plus + Pinia + Vue Router 项目");
                String outputPath = (String) arguments.get("outputPath");

                if (projectName == null || projectName.isBlank()) {
                    log.warn("[generate_vue_template] missing projectName");
                    String prompt = "缺少必要参数，请至少提供 projectName。\n" +
                            "示例:\n" +
                            "- projectName: my-vue-app\n" +
                            "- outputPath: /Users/mi/Desktop/vue-out (可选，缺省将使用该路径)\n" +
                            "可选: description";
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(prompt)), false));
                }

                if (outputPath == null || outputPath.isBlank()) {
                    String userHome = System.getProperty("user.home");
                    outputPath = userHome + "/Desktop/vue-out";
                    log.info("[generate_vue_template] outputPath empty, use default: {}", outputPath);
                }

                VueTemplateService service = new VueTemplateService();
                log.info("[generate_vue_template] call service, projectName={}, outputPath={}", projectName, outputPath);
                String result = service.generateVueTemplate(projectName, description, outputPath);

                String message = "Vue 项目模板生成成功: " + result;
                log.info("[generate_vue_template] success: {}", message);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(message)), false));
            } catch (Exception e) {
                log.error("生成Vue项目模板失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
            }
        });
    }
}
