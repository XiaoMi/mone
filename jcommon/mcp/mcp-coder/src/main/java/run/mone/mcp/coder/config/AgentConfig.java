package run.mone.mcp.coder.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;

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

    @Bean
    public RoleMeta roleMeta() {

        ListFilesTool listFilesTool = isRemoteFile ? new ListFilesTool(true) : new ListFilesTool(false);
        ReadFileTool readFileTool = isRemoteFile ? new ReadFileTool(true) : new ReadFileTool(false);
        WriteToFileTool writeToFileTool = isRemoteFile ? new WriteToFileTool(true) : new WriteToFileTool(false);
        ReplaceInFileTool replaceInFileTool = isRemoteFile ? new ReplaceInFileTool(true) : new ReplaceInFileTool(false);
        SearchFilesTool searchFilesTool = isRemoteFile ? new SearchFilesTool(true) : new SearchFilesTool(false);

        return RoleMeta.builder()
                .profile("你是一名优秀的软件工程师")
                .goal("你的目标是根据用户的需求写好代码")
                .constraints("不要探讨和代码不想关的东西,如果用户问你,你可以直接拒绝掉")
                .tools(Lists.newArrayList(
                                listFilesTool,
                                new ExecuteCommandToolOptimized(),
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
