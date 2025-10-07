package run.mone.mcp.codegen.function;

import com.xiaomi.youpin.codegen.BizGen;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * BizGen Function - 生成完整的Spring Boot业务项目
 * 
 * @author goodjava@qq.com
 * @date 2025/10/7
 */
@Slf4j
@Component
public class BizGenFunction implements McpFunction {

    private static final String TOOL_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "projectPath": {
                  "type": "string",
                  "description": "项目生成路径，例如: /tmp/biz-projects"
                },
                "projectName": {
                  "type": "string",
                  "description": "项目名称，例如: my-shop"
                },
                "groupId": {
                  "type": "string",
                  "description": "Maven GroupId，例如: run.mone"
                },
                "packageName": {
                  "type": "string",
                  "description": "包名，例如: run.mone.shop"
                },
                "author": {
                  "type": "string",
                  "description": "作者名称或邮箱"
                },
                "versionId": {
                  "type": "string",
                  "description": "版本号，例如: 1.0.0"
                },
                "description": {
                  "type": "string",
                  "description": "项目描述"
                },
                "springBootVersion": {
                  "type": "string",
                  "description": "Spring Boot版本，默认: 3.2.0"
                },
                "javaVersion": {
                  "type": "string",
                  "description": "Java版本，默认: 21"
                },
                "serverPort": {
                  "type": "string",
                  "description": "服务端口，默认: 8080"
                },
                "dbName": {
                  "type": "string",
                  "description": "数据库名称，默认使用项目名"
                },
                "jwtSecret": {
                  "type": "string",
                  "description": "JWT密钥，默认: ThisIsASecretKeyForJWT1234567890"
                },
                "jwtExpiration": {
                  "type": "string",
                  "description": "JWT过期时间(毫秒)，默认: 86400000"
                }
              },
              "required": ["projectPath", "projectName", "groupId", "packageName", "author", "versionId", "description"]
            }
            """;

    private final BizGen bizGen = new BizGen();

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.create(sink -> {
            try {
                log.info("BizGenFunction called with arguments: {}", arguments);

                // 提取必需参数
                String projectPath = (String) arguments.get("projectPath");
                String projectName = (String) arguments.get("projectName");
                String groupId = (String) arguments.get("groupId");
                String packageName = (String) arguments.get("packageName");
                String author = (String) arguments.get("author");
                String versionId = (String) arguments.get("versionId");
                String description = (String) arguments.get("description");

                // 提取可选参数（带默认值）
                String springBootVersion = (String) arguments.getOrDefault("springBootVersion", "3.2.0");
                String javaVersion = (String) arguments.getOrDefault("javaVersion", "21");
                String serverPort = (String) arguments.getOrDefault("serverPort", "8080");
                String dbName = (String) arguments.getOrDefault("dbName", projectName);
                String jwtSecret = (String) arguments.getOrDefault("jwtSecret", "ThisIsASecretKeyForJWT1234567890");
                String jwtExpiration = (String) arguments.getOrDefault("jwtExpiration", "86400000");

                // 调用BizGen生成项目
                Result<String> result = bizGen.generateAndZip(
                        projectPath, projectName, groupId, packageName, author, versionId,
                        description, springBootVersion, javaVersion, serverPort, dbName,
                        jwtSecret, jwtExpiration
                );

                // 构造返回消息
                String message;
                if (result.getCode() == 0) {
                    message = String.format(
                            "✅ Biz项目生成成功！\n\n" +
                            "📦 生成文件位置: %s\n\n" +
                            "🎯 生成的项目包含：\n" +
                            "  ✓ Spring Boot %s 基础框架\n" +
                            "  ✓ Spring Security + JWT 认证\n" +
                            "  ✓ JPA + H2 数据库\n" +
                            "  ✓ 用户认证体系（User, UserRepository, CustomUserDetailsService, UserService）\n" +
                            "  ✓ 完整的配置类（Security, WebMvc, App）\n" +
                            "  ✓ JWT 认证过滤器和工具类\n" +
                            "  ✓ 统一 API 响应格式\n" +
                            "  ✓ 完整的用户DTO（UserDTO, RegisterRequest, LoginRequest等）\n" +
                            "  ✓ 用户控制器（HealthController, UserController）\n" +
                            "  ✓ @AuthUser注解和参数解析器\n" +
                            "  ✓ HTTP 日志记录切面\n" +
                            "  ✓ 全局异常处理\n" +
                            "  ✓ Logback 日志配置\n" +
                            "  ✓ Hive Agent 配置（全栈/后端/前端）\n\n" +
                            "📝 项目信息：\n" +
                            "  - 项目名: %s\n" +
                            "  - 包名: %s\n" +
                            "  - 版本: %s\n" +
                            "  - 端口: %s\n" +
                            "  - Java版本: %s",
                            result.getData(), springBootVersion, projectName, packageName, 
                            versionId, serverPort, javaVersion
                    );
                } else {
                    message = String.format("❌ Biz项目生成失败: %s", result.getMessage());
                }

                List<McpSchema.Content> contents = List.of(new McpSchema.TextContent(message));
                sink.next(new McpSchema.CallToolResult(contents, result.getCode() != 0));
                sink.complete();

            } catch (Exception e) {
                log.error("BizGenFunction error", e);
                String errorMessage = String.format("❌ 生成Biz项目时发生错误: %s", e.getMessage());
                List<McpSchema.Content> errorContents = List.of(new McpSchema.TextContent(errorMessage));
                sink.next(new McpSchema.CallToolResult(errorContents, true));
                sink.complete();
            }
        });
    }

    @Override
    public String getName() {
        return "generate_biz_project";
    }

    @Override
    public String getDesc() {
        return "生成完整的Spring Boot业务项目，包含Spring Security + JWT认证、JPA数据库、用户管理、统一API响应、日志配置等企业级开发基础设施。适合快速搭建电商、SaaS等业务系统。";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}

