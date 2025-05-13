package run.mone.mcp.multimodal.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.roles.tool.FileTool;
import run.mone.mcp.multimodal.function.MultimodalFunction;
import run.mone.mcp.multimodal.service.MultimodalService;

import javax.annotation.Resource;

/**
 * 多模态界面操作Agent配置
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Resource
    private MultimodalService multimodalService;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名界面操作助手，可以帮助用户根据截图执行各种操作")
                .goal("你的目标是根据用户提供的截图和指令，执行点击、拖拽、输入等操作")
                .workflow("""
                        界面操作流程:
                        <1>分析用户提供的截图 调用multimodal->analyzeScreenshot operation
                        <2>执行点击操作 调用multimodal->click/doubleClick/rightClick operation
                        <3>执行拖拽操作 调用multimodal->dragAndDrop operation
                        <4>执行键盘输入 调用multimodal->typeText operation
                        <5>执行组合键 调用multimodal->pressHotkey operation
                        """)
                .outputFormat("直接输出文本即可，描述你的操作结果")
                .constraints("只执行用户要求的界面操作，不要执行任何可能有安全风险的操作")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        new FileTool()
                ))
                //mcp工具
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName),
                        new MultimodalFunction(multimodalService)
                ))
                .build();
    }
} 