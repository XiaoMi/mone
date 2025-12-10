package run.mone.mcp.tester.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.hive.utils.RemoteFileUtils;
import run.mone.mcp.tester.tool.TestGenerationTool;

/**
 * MCP Tester Agent 配置
 * 配置测试生成代理的角色、工具和行为
 *
 * @date 2025/12/09
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Value("${mcp.remote.file}")
    private Boolean isRemoteFile;

    @Value("${remote.file.user.key:}")
    private String userKey;

    @Value("${remote.file.user.secret:}")
    private String userSecret;

    @Value("${remote.file.api.host:}")
    private String remoteFileApiHost;

    @Value("${test.framework:junit5}")
    private String testFramework;

    @Value("${test.mock.framework:mockito}")
    private String mockFramework;

    @Value("${test.output.path:src/test/java}")
    private String testOutputPath;

    @Bean
    public RoleMeta roleMeta() {
        initRemoteConfig();
        return RoleMeta.builder()
                .profile("你是一名专业的测试工程师，擅长为 Java 项目编写高质量的单元测试。" +
                        "你熟悉 JUnit、Mockito 等测试框架，能够分析代码结构并生成全面的测试用例。")
                .goal("你的目标是根据用户提供的项目路径和源代码，生成完整、规范的单元测试代码。" +
                        "测试代码应该覆盖主要的业务逻辑，包含正常场景和异常场景的测试。")
                .constraints("1. 只生成测试代码，不修改源代码\n" +
                        "2. 测试代码必须遵循最佳实践和编码规范\n" +
                        "3. 为每个公共方法生成至少一个测试用例\n" +
                        "4. 使用合适的断言和 Mock 对象\n" +
                        "5. 测试方法命名要清晰明确，能够表达测试意图")
                .workflow("1. 分析项目结构，识别需要测试的源文件\n" +
                        "2. 读取并解析源文件，提取类、方法、依赖等信息\n" +
                        "3. 为每个方法生成测试用例框架\n" +
                        "4. 添加必要的 Mock 对象和测试数据\n" +
                        "5. 生成测试文件并保存到指定目录")
                .tools(Lists.newArrayList(
                                // 文件操作工具
                                new ListFilesTool(isRemoteFile),
                                new ReadFileTool(isRemoteFile),
                                new SearchFilesTool(isRemoteFile),
                                new WriteToFileTool(isRemoteFile),

                                // 代码分析工具
                                new ListCodeDefinitionNamesTool(),

                                // 命令执行工具（用于运行测试）
                                new ExecuteCommandToolOptimized(),

                                // 辅助工具
                                new DiffTool(),
                                new ChatTool(),
                                new AskTool(),
                                new SkillRequestTool(),
                                new AttemptCompletionTool()
                        )
                )
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
                .build();
    }

    private void initRemoteConfig() {
        RemoteFileUtils.userKey = userKey;
        RemoteFileUtils.userSecret = userSecret;
        RemoteFileUtils.remoteFileApiHost = remoteFileApiHost;
    }
}
