package run.mone.mcp.coder.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.roles.tool.*;
import run.mone.hive.spring.starter.WebSocketSessionManager;
import run.mone.hive.utils.RemoteFileUtils;
import run.mone.hive.utils.WebSocketFileUtils;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Slf4j
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    /**
     * 文件操作模式配置
     * 支持的值: LOCAL, REMOTE_HTTP, REMOTE_WS
     * 默认值: LOCAL
     */
    @Value("${mcp.file.operation.mode:LOCAL}")
    private String fileOperationMode;

    // 兼容旧配置
    @Value("${mcp.remote.file:false}")
    private Boolean isRemoteFile;

    @Value("${remote.file.user.key:}")
    private String userKey;

    @Value("${remote.file.user.secret:}")
    private String userSecret;

    @Value("${remote.file.api.host:}")
    private String remoteFileApiHost;


    @Bean
    public RoleMeta roleMeta() {
        initRemoteConfig();
        initWebSocketMessageSender();

        // 确定文件操作模式
        FileOperationMode mode = getFileOperationMode();
        log.info("文件操作模式: {}", mode);

        return RoleMeta.builder()
                .profile("你是一名优秀的软件工程师")
                .goal("你的目标是根据用户的需求写好代码")
                .constraints("不要探讨和代码不想关的东西,如果用户问你,你可以直接拒绝掉")
                .workflow("有文件修改或写入动作后(比如调用了write_to_file或者replace_in_file 这两个Tool后)，自动触发差异对比(调用DiffTool)")
                .tools(Lists.newArrayList(
                                new ListFilesTool(mode),
                                new ExecuteCommandToolOptimized(),
                                new ReadFileTool(mode),
                                new SearchFilesTool(mode),
                                new ReplaceInFileTool(mode),
                                new ListCodeDefinitionNamesTool(),
                                new WriteToFileTool(mode),
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

    /**
     * 获取文件操作模式
     */
    private FileOperationMode getFileOperationMode() {
        try {
            return FileOperationMode.valueOf(fileOperationMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("无效的文件操作模式: {}，使用默认值 LOCAL", fileOperationMode);
            return FileOperationMode.LOCAL;
        }
    }

    /**
     * 初始化远程文件配置
     */
    private void initRemoteConfig() {
        RemoteFileUtils.userKey = userKey;
        RemoteFileUtils.userSecret = userSecret;
        RemoteFileUtils.remoteFileApiHost = remoteFileApiHost;
    }

    /**
     * 初始化 WebSocket 消息发送器
     */
    private void initWebSocketMessageSender() {
        // 设置 WebSocket 消息发送器
        WebSocketFileUtils.setMessageSender((clientId, message) -> {
            WebSocketSessionManager manager = WebSocketSessionManager.getInstance();
            manager.sendMessage(clientId, message);
            log.debug("发送 WebSocket 消息到客户端: clientId={}", clientId);
        });
        log.info("WebSocket 消息发送器已初始化");
    }


}
