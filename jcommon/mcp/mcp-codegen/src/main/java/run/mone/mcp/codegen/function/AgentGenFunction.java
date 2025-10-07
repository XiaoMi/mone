package run.mone.mcp.codegen.function;

import com.xiaomi.youpin.codegen.AgentGen;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * AgentGen Function - 生成基于Hive的MCP Agent项目
 * 
 * @author goodjava@qq.com
 * @date 2025/10/7
 */
@Slf4j
@Component
public class AgentGenFunction implements McpFunction {

    private static final String TOOL_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "projectPath": {
                  "type": "string",
                  "description": "项目生成路径，例如: /tmp/agent-projects"
                },
                "projectName": {
                  "type": "string",
                  "description": "项目名称，例如: mcp-my-agent"
                },
                "groupId": {
                  "type": "string",
                  "description": "Maven GroupId，例如: run.mone"
                },
                "packageName": {
                  "type": "string",
                  "description": "包名，例如: run.mone.mcp.myagent"
                },
                "author": {
                  "type": "string",
                  "description": "作者名称或邮箱"
                },
                "versionId": {
                  "type": "string",
                  "description": "版本号，例如: 1.0.0"
                },
                "agentName": {
                  "type": "string",
                  "description": "Agent名称，例如: myagent"
                },
                "agentGroup": {
                  "type": "string",
                  "description": "Agent分组，例如: staging 或 production"
                },
                "agentProfile": {
                  "type": "string",
                  "description": "Agent简介，描述这个Agent的角色定位，例如: 你是一名优秀的测试工程师"
                },
                "agentGoal": {
                  "type": "string",
                  "description": "Agent目标，描述这个Agent要达成的目标，例如: 你的目标是编写高质量的测试代码"
                },
                "agentConstraints": {
                  "type": "string",
                  "description": "Agent约束，描述这个Agent的限制和边界，例如: 不要探讨和测试不相关的东西"
                },
                "parentGroupId": {
                  "type": "string",
                  "description": "父项目GroupId，默认: run.mone"
                },
                "parentArtifactId": {
                  "type": "string",
                  "description": "父项目ArtifactId，默认: mcp"
                },
                "parentVersion": {
                  "type": "string",
                  "description": "父项目版本，默认: 1.6.1-jdk21-SNAPSHOT"
                },
                "grpcPort": {
                  "type": "string",
                  "description": "gRPC端口，默认: 9186"
                },
                "hiveManagerUrl": {
                  "type": "string",
                  "description": "Hive Manager地址，默认: http://127.0.0.1:8080"
                },
                "llmModel": {
                  "type": "string",
                  "description": "LLM模型，默认: qwen"
                },
                "javaVersion": {
                  "type": "string",
                  "description": "Java版本，默认: 21"
                }
              },
              "required": ["projectPath", "projectName", "groupId", "packageName", "author", "versionId", "agentName", "agentGroup", "agentProfile", "agentGoal", "agentConstraints"]
            }
            """;

    private final AgentGen agentGen = new AgentGen();

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.create(sink -> {
            try {
                log.info("AgentGenFunction called with arguments: {}", arguments);

                // 提取必需参数
                String projectPath = (String) arguments.get("projectPath");
                String projectName = (String) arguments.get("projectName");
                String groupId = (String) arguments.get("groupId");
                String packageName = (String) arguments.get("packageName");
                String author = (String) arguments.get("author");
                String versionId = (String) arguments.get("versionId");
                String agentName = (String) arguments.get("agentName");
                String agentGroup = (String) arguments.get("agentGroup");
                String agentProfile = (String) arguments.get("agentProfile");
                String agentGoal = (String) arguments.get("agentGoal");
                String agentConstraints = (String) arguments.get("agentConstraints");

                // 提取可选参数（带默认值）
                String parentGroupId = (String) arguments.getOrDefault("parentGroupId", "run.mone");
                String parentArtifactId = (String) arguments.getOrDefault("parentArtifactId", "mcp");
                String parentVersion = (String) arguments.getOrDefault("parentVersion", "1.6.1-jdk21-SNAPSHOT");
                String grpcPort = (String) arguments.getOrDefault("grpcPort", "9186");
                String hiveManagerUrl = (String) arguments.getOrDefault("hiveManagerUrl", "http://127.0.0.1:8080");
                String llmModel = (String) arguments.getOrDefault("llmModel", "qwen");
                String javaVersion = (String) arguments.getOrDefault("javaVersion", "21");

                // 调用AgentGen生成项目
                Result<String> result = agentGen.generateAndZip(
                        projectPath, projectName, groupId, packageName, author, versionId,
                        agentName, agentGroup, agentProfile, agentGoal, agentConstraints,
                        parentGroupId, parentArtifactId, parentVersion, grpcPort,
                        hiveManagerUrl, llmModel, javaVersion
                );

                // 构造返回消息
                String message;
                if (result.getCode() == 0) {
                    message = String.format(
                            "✅ MCP Agent项目生成成功！\n\n" +
                            "📦 生成文件位置: %s\n\n" +
                            "🎯 生成的项目包含：\n" +
                            "  ✓ Hive MCP 框架集成\n" +
                            "  ✓ gRPC 服务支持\n" +
                            "  ✓ Spring Boot 启动类\n" +
                            "  ✓ Agent 配置类（Profile、Goal、Constraints）\n" +
                            "  ✓ application.properties 配置文件\n" +
                            "  ✓ logback.xml 日志配置\n" +
                            "  ✓ README.md 项目文档\n\n" +
                            "🤖 Agent信息：\n" +
                            "  - Agent名称: %s\n" +
                            "  - Agent分组: %s\n" +
                            "  - 角色定位: %s\n" +
                            "  - 目标: %s\n" +
                            "  - 约束: %s\n\n" +
                            "📝 项目信息：\n" +
                            "  - 项目名: %s\n" +
                            "  - 包名: %s\n" +
                            "  - 版本: %s\n" +
                            "  - gRPC端口: %s\n" +
                            "  - LLM模型: %s\n" +
                            "  - Java版本: %s\n\n" +
                            "💡 下一步：\n" +
                            "  1. 解压生成的zip文件\n" +
                            "  2. 在项目中实现自定义Function（参考hive-mcp框架）\n" +
                            "  3. 在AgentConfig中注册你的Function\n" +
                            "  4. 编译打包: mvn clean package\n" +
                            "  5. 运行: java -jar target/app.jar",
                            result.getData(), agentName, agentGroup, agentProfile, agentGoal,
                            agentConstraints, projectName, packageName, versionId, grpcPort,
                            llmModel, javaVersion
                    );
                } else {
                    message = String.format("❌ Agent项目生成失败: %s", result.getMessage());
                }

                List<McpSchema.Content> contents = List.of(new McpSchema.TextContent(message));
                sink.next(new McpSchema.CallToolResult(contents, result.getCode() != 0));
                sink.complete();

            } catch (Exception e) {
                log.error("AgentGenFunction error", e);
                String errorMessage = String.format("❌ 生成Agent项目时发生错误: %s", e.getMessage());
                List<McpSchema.Content> errorContents = List.of(new McpSchema.TextContent(errorMessage));
                sink.next(new McpSchema.CallToolResult(errorContents, true));
                sink.complete();
            }
        });
    }

    @Override
    public String getName() {
        return "generate_agent_project";
    }

    @Override
    public String getDesc() {
        return "生成基于Hive框架的MCP Agent项目。MCP Agent是可以与AI模型集成的智能代理，支持自定义工具函数、gRPC通信、与Hive Manager集成等功能。适合构建专业领域的AI Agent。";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}

