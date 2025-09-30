package run.mone.mcp.vue3.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.vue3.service.Vue3TemplateService;

import java.util.List;
import java.util.Map;

@Component
public class Vue3TemplateFunction implements McpFunction {

    private final Vue3TemplateService vue3TemplateService;

    @Autowired
    public Vue3TemplateFunction(Vue3TemplateService vue3TemplateService) {
        this.vue3TemplateService = vue3TemplateService;
    }

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["generateComponent", "generatePage", "generateProject"],
                        "description": "Vue3模板生成操作类型"
                    },
                    "componentName": {
                        "type": "string",
                        "description": "组件名称，用于generateComponent操作"
                    },
                    "pageName": {
                        "type": "string",
                        "description": "页面名称，用于generatePage操作"
                    },
                    "projectName": {
                        "type": "string",
                        "description": "项目名称，用于generateProject操作"
                    },
                    "template": {
                        "type": "string",
                        "description": "自定义模板内容"
                    },
                    "script": {
                        "type": "string",
                        "description": "自定义脚本内容"
                    },
                    "style": {
                        "type": "string",
                        "description": "自定义样式内容"
                    },
                    "useCompositionAPI": {
                        "type": "boolean",
                        "description": "是否使用Composition API，默认true"
                    },
                    "useTypeScript": {
                        "type": "boolean",
                        "description": "是否使用TypeScript，默认false"
                    },
                    "usePinia": {
                        "type": "boolean",
                        "description": "是否使用Pinia状态管理，默认false"
                    },
                    "useRouter": {
                        "type": "boolean",
                        "description": "是否使用Vue Router，默认false"
                    },
                    "useVite": {
                        "type": "boolean",
                        "description": "是否使用Vite构建工具，默认true"
                    },
                    "outputDir": {
                        "type": "string",
                        "description": "生成输出目录，用于generateProject操作"
                    },
                    "layout": {
                        "type": "string",
                        "description": "页面布局类型，用于generatePage操作"
                    }
                },
                "required": ["operation"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        
        return Flux.defer(() -> {
            try {
                Flux<String> result = switch (operation) {
                    case "generateComponent" -> vue3TemplateService.generateVue3Component(arguments);
                    case "generatePage" -> vue3TemplateService.generateVue3Page(arguments);
                    case "generateProject" -> vue3TemplateService.generateVue3Project(arguments);
                    default -> throw new IllegalArgumentException("Unknown operation: " + operation);
                };
                
                return result.map(res -> new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(res)), 
                    false
                ));
            } catch (Exception e) {
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())), 
                    true
                ));
            }
        });
    }

    public String getName() {
        return "stream_vue3_template_generator";
    }

    public String getDesc() {
        return "生成Vue3组件、页面和项目模板，支持Composition API、TypeScript、Pinia状态管理、Vue Router等功能";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
