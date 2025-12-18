package run.mone.mcp.multimodal.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.UnicastProcessor;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.mcp.multimodal.function.AndroidFunction;
import run.mone.mcp.multimodal.function.MultimodalFunction;
import run.mone.mcp.multimodal.gui.AndroidGuiAgent;
import run.mone.mcp.multimodal.gui.GuiAgent;
import run.mone.mcp.multimodal.service.GuiAgentService;
import run.mone.mcp.multimodal.service.MultimodalService;
import run.mone.mcp.multimodal.tool.AndroidActionTool;
import run.mone.mcp.multimodal.tool.AndroidScreenshotTool;

import javax.annotation.Resource;
import java.util.Map;
import java.util.function.Function;

/**
 * 多模态界面操作Agent配置
 */
@Slf4j
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    /**
     * Agent 类型配置
     * 可选值: default(默认GUI操作), android(Android设备操作)
     */
    @Value("${mcp.agent.type:android}")
    private String agentType;

    @Autowired(required = false)
    private MultimodalService multimodalService;


    @Autowired(required = false)
    private GuiAgentService guiAgentService;

    @Autowired(required = false)
    private GuiAgent guiAgent;

    @Autowired(required = false)
    private AndroidFunction androidFunction;

    /**
     * 判断是否为 Android 操作员模式
     */
    private boolean isAndroidAgent() {
        return AndroidConfig.AGENT_TYPE.equalsIgnoreCase(agentType);
    }

    @Bean
    public RoleMeta roleMeta() {
        if (isAndroidAgent()) {
            log.info("启用 Android 操作员模式");
            return buildAndroidRoleMeta();
        }
        log.info("启用默认 GUI 操作模式");
        return buildDefaultRoleMeta();
    }

    /**
     * 构建 Android 操作员的 RoleMeta
     */
    private RoleMeta buildAndroidRoleMeta() {
        return RoleMeta.builder()
                .profile(AndroidConfig.PROFILE)
                .goal(AndroidConfig.GOAL)
                .workflow(AndroidConfig.WORKFLOW)
                .outputFormat(AndroidConfig.OUTPUT_FORMAT)
                .constraints(AndroidConfig.CONSTRAINTS)
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AndroidScreenshotTool(),
                        new AndroidActionTool(),
                        new AttemptCompletionTool()
                ))
                //mcp工具 - Android 模式使用 AndroidFunction
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName, 20)
                ))
                .checkFinishFunc(msg -> msg.getContent().contains("发送结果:") || msg.getContent().contains("任务完成:") ? -1 : 1)
                .build();
    }

    /**
     * 构建默认 GUI 操作的 RoleMeta
     */
    private RoleMeta buildDefaultRoleMeta() {
        return RoleMeta.builder()
                .profile(GuiConfig.PROFILE)
                .goal(GuiConfig.GOAL)
                .workflow(GuiConfig.WORKFLOW)
                .outputFormat(GuiConfig.OUTPUT_FORMAT)
                .constraints(GuiConfig.CONSTRAINTS)
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                ))
                //mcp工具
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName, 20),
                        new MultimodalFunction(multimodalService, guiAgent)
                ))
                .checkFinishFunc(msg -> msg.getContent().contains("任务完成:") ? -1 : 1)
                .build();
    }

//    @Bean("sseTaskHandler")
//    public Function<String, String> sseTaskHandler(AndroidGuiAgent androidGuiAgent) {
//        return task -> {
//            new Thread(() -> {
//                // 业务处理逻辑
//                UnicastProcessor<String> processor = UnicastProcessor.create();
//                FluxSink<String> sink = processor.sink();
//
//                // 订阅结果输出
//                processor.subscribe(
//                        msg -> System.out.println("[Android Agent] " + msg),
//                        error -> System.err.println("[Error] " + error.getMessage()),
//                        () -> System.out.println("[Android Agent] 任务完成")
//                );
//                androidGuiAgent.run(task, sink);
//            }).start();
//            return "处理结果";
//        };
//    }
//
//    //处理ws
//    @Bean("wsTaskHandler")
//    public Function<Map<String, Object>, String> wsTaskHandler() {
//        return data -> {
//            // 业务处理逻辑
//            log.info("req:{}", data);
//
//            return "处理结果";
//        };
//    }
}