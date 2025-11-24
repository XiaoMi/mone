package run.mone.mcp.coder.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.hive.utils.RemoteFileUtils;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
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


    @Bean
    public RoleMeta roleMeta() {
        RemoteFileUtils.userKey = userKey;
        RemoteFileUtils.userSecret = userSecret;
        RemoteFileUtils.remoteFileApiHost = remoteFileApiHost;

        ListFilesTool listFilesTool = new ListFilesTool(isRemoteFile);
        ReadFileTool readFileTool = new ReadFileTool(isRemoteFile);
        WriteToFileTool writeToFileTool = new WriteToFileTool(isRemoteFile);
        ReplaceInFileTool replaceInFileTool = new ReplaceInFileTool(isRemoteFile);
        SearchFilesTool searchFilesTool = new SearchFilesTool(isRemoteFile);
        ExecuteCommandToolOptimized  executeCommandToolOptimized = new ExecuteCommandToolOptimized(isRemoteFile);

        return RoleMeta.builder()
                .profile("你是一名优秀的软件工程师")
                .goal("你的目标是根据用户的需求写好代码")
                .constraints("不要探讨和代码不想关的东西,如果用户问你,你可以直接拒绝掉")
                .tools(Lists.newArrayList(
                                listFilesTool,
                                executeCommandToolOptimized,
                                readFileTool,
                                searchFilesTool,
                                replaceInFileTool,
                                new ListCodeDefinitionNamesTool(),
                                writeToFileTool,
                                new ChatTool(),
                                new AskTool(),
                                new AttemptCompletionTool()
                        )
                )
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
//                .taskList(Lists.newArrayList((role) -> {
//                    role.putMessage(Message.builder()
//                            .role("user")
//                            .content("1+1=?")
//                            .sink(new McpTransportFluxSink(transport, role))
//                            .build());
//                    return "ok";
//                }))
                .build();
    }


}
