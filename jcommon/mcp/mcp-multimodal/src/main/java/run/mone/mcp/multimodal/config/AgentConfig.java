package run.mone.mcp.multimodal.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.mcp.multimodal.function.AndroidFunction;
import run.mone.mcp.multimodal.function.MultimodalFunction;
import run.mone.mcp.multimodal.gui.GuiAgent;
import run.mone.mcp.multimodal.service.GuiAgentService;
import run.mone.mcp.multimodal.service.MultimodalService;

import javax.annotation.Resource;

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
    @Value("${mcp.agent.type:default}")
    private String agentType;

    @Resource
    private MultimodalService multimodalService;


    @Resource
    private GuiAgentService guiAgentService;

    @Resource
    private GuiAgent guiAgent;

    @Resource
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
                .workflow(AndroidConfig.WORKFLOW + "\n" + AndroidConfig.MEITUAN_KFC_WORKFLOW)
                .outputFormat(AndroidConfig.OUTPUT_FORMAT)
                .constraints(AndroidConfig.CONSTRAINTS)
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                ))
                //mcp工具 - Android 模式使用 AndroidFunction
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName, 20),
                        androidFunction
                ))
                .checkFinishFunc(msg -> msg.getContent().contains("发送结果:") || msg.getContent().contains("任务完成:") ? -1 : 1)
                .build();
    }

    /**
     * 构建默认 GUI 操作的 RoleMeta
     */
    private RoleMeta buildDefaultRoleMeta() {
        return RoleMeta.builder()
                .profile("你是一名智能界面操作协调助手，负责将用户的复杂任务拆解为多个步骤，并协调 GUI Agent 执行各个步骤的界面操作")
                .goal("你的目标是理解用户意图，智能拆分任务，并通过多次调用 runGuiAgent 来完成跨界面的复杂操作流程")
                .workflow("""
                        界面操作协调流程:
                        
                        <核心原则>
                        1. 【界面边界识别】每当操作会导致界面切换/变化时（如点击应用图标、打开新窗口、切换标签页），必须将其作为独立步骤
                        2. 【单次调用原则】每次调用 runGuiAgent 只能处理当前界面下的操作，不能跨界面规划
                        3. 【多步拆解原则】将复杂任务拆解为多个 runGuiAgent 调用，确保每次调用时的截图都是最新界面
                        
                        <任务拆解示例>
                        
                        错误示例❌:
                        用户需求: "打开微信并发送消息给张三"
                        错误做法: 调用一次 runGuiAgent(instruction="打开微信并发送消息给张三")
                        问题: GuiAgent 会基于当前界面一次性规划所有操作，但点击微信图标后界面已变化，后续操作会失败
                        
                        正确示例✅:
                        用户需求: "打开微信并发送消息给张三"
                        正确做法:
                        步骤1: 调用 runGuiAgent(instruction="点击微信图标打开微信", appName="微信")
                        步骤2: 等待界面稳定后，调用 runGuiAgent(instruction="在微信中找到张三并发送消息")
                        原因: 每次调用都基于当前最新界面进行截图和分析
                        
                        <拆解策略>
                        1. 识别任务中的界面切换点（打开应用、点击菜单、切换页面等）
                        2. 在每个界面切换点将任务拆分为独立的 runGuiAgent 调用
                        3. 同一界面内的连续操作可以合并到一次 runGuiAgent 调用中
                        4. 需要等待界面加载/响应时，要在两次调用之间添加适当间隔
                        
                        <操作步骤>
                        <1> 分析用户需求，识别是否涉及多个界面
                        <2> 如果涉及界面切换，将任务拆解为多个步骤
                        <3> 按顺序执行每个步骤，每步调用一次 multimodal->runGuiAgent operation
                        <4> 每次调用 runGuiAgent 时，instruction 应该明确且针对当前界面
                        <5> 如果知道目标应用名称，使用 appName 参数自动激活窗口（macOS支持）
                        <6> 总结执行结果并报告给用户
                        
                        <注意事项>
                        - runGuiAgent 内部会自动截图、分析并执行操作，你不需要手动截图
                        - appName 参数支持别名（如 "chrome"/"谷歌浏览器"、"微信"/"wechat"、"终端"/"terminal" 等）
                        - macOS 系统下 appName 会自动切换到应用所在的 Space 并激活窗口
                        - 避免让 GuiAgent 处理跨界面的复杂流程，应该由你来协调多次调用
                        """)
                .outputFormat("直接输出文本即可，描述每一步的操作结果和最终完成情况")
                .constraints("""
                        约束条件:
                        1. 只执行用户要求的界面操作，不要执行任何可能有安全风险的操作
                        2. 涉及界面切换时，必须拆分为多次 runGuiAgent 调用
                        3. 不要尝试在单次 runGuiAgent 中规划跨界面的操作序列
                        4. 每次调用前要清楚当前应该处于什么界面状态
                        """)
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
} 