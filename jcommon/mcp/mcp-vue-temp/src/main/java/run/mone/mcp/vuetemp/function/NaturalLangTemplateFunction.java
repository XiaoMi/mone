package run.mone.mcp.vuetemp.function;

import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.vuetemp.service.VueTemplateService;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 支持自然语言输入的生成器，将一段中文/英文描述解析为参数并调用模板生成服务。
 */
public class NaturalLangTemplateFunction implements McpFunction {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(NaturalLangTemplateFunction.class);

    private static final String TOOL_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "input": {"type": "string", "description": "自然语言指令，如：生成vue项目模板，projectName: my-vue-app, outputPath: /tmp"}
              },
              "required": ["input"]
            }
            """;

    @Override
    public String getName() {
        return "nl_generate_vue_template";
    }

    @Override
    public String getDesc() {
        return "用自然语言描述生成 Vue 模板（自动解析 projectName/outputPath）";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.defer(() -> {
            try {
                log.info("[nl_generate_vue_template] enter, args={}", arguments);
                String input = (String) arguments.getOrDefault("input", "");
                if (input == null || input.isBlank()) {
                    log.warn("[nl_generate_vue_template] empty input");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("请输入自然语言指令，例如：生成vue项目模板，projectName: my-vue-app, outputPath: ~/Desktop/vue-out")),
                            false));
                }

                String projectName = extract(input, "(?:projectName|项目名|项目名称)[:：]\\s*([A-Za-z0-9_-]+)");
                String outputPath = extract(input, "(?:outputPath|输出路径)[:：]\\s*([^，,\\n\\r\\t ]+)");
                String description = "Vue 3 + TypeScript + Element Plus + Pinia + Vue Router 项目";

                if (projectName == null || projectName.isBlank()) {
                    log.warn("[nl_generate_vue_template] cannot parse projectName from: {}", input);
                    String prompt = "无法从指令中识别 projectName。示例：projectName: my-vue-app，可选：outputPath: ~/Desktop/vue-out";
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(prompt)), false));
                }

                if (outputPath == null || outputPath.isBlank()) {
                    String userHome = System.getProperty("user.home");
                    outputPath = userHome + "/Desktop/vue-out";
                    log.info("[nl_generate_vue_template] outputPath empty, use default: {}", outputPath);
                }

                VueTemplateService service = new VueTemplateService();
                log.info("[nl_generate_vue_template] call service, projectName={}, outputPath={}", projectName, outputPath);
                String result = service.generateVueTemplate(projectName, description, outputPath);
                String message = "Vue 项目模板生成成功: " + result;
                log.info("[nl_generate_vue_template] success: {}", message);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(message)), false));
            } catch (Exception e) {
                log.error("自然语言生成Vue项目模板失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
            }
        });
    }

    private String extract(String text, String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}


