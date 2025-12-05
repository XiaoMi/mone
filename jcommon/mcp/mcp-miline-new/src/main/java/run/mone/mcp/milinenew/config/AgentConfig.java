package run.mone.mcp.milinenew.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.milinenew.function.*;
import run.mone.mcp.milinenew.tools.*;

import run.mone.mcp.git.tool.GitCloneTool;
import run.mone.mcp.git.tool.GitCommitTool;
import run.mone.mcp.git.tool.GitPushTool;
import run.mone.mcp.milinenew.tools.RunPipelineTool;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author wangmin
 * @author goodjava@qq.com
 * @date 2025/1/1
 */
@Configuration
public class AgentConfig {
    @Autowired
    private GitCloneTool gitCloneTool;
    @Autowired
    private GitCommitTool gitCommitTool;
    @Autowired
    private GitPushTool gitPushTool;

    @Value("${mcp.agent.mode:MCP}")
    private String agentMode;

    @Value("${mcp.agent.name:miline_new}")
    private String agentName;

    @Value("${mcp.http.port:8082}")
    private String httpPort;

    @Autowired
    private CreatePipelineFunction createPipelineFunction;

    @Autowired
    private CreateProjectFunction createProjectFunction;

    @Autowired
    private GenerateGitCodeFunction generateGitCodeFunction;

    @Autowired
    private RunPipelineFunction runPipelineFunction;

    @Autowired
    private GetDeployMachinesFunction getDeployMachinesFunction;

    @Autowired
    QueryPipelineByGitUrlFunction queryPipelineByGitUrlFunction;

    @Autowired
    private CreatePipelineTool createPipelineTool;

    @Autowired
    private CreateProjectTool createProjectTool;

    @Autowired
    private GenerateGitCodeTool generateGitCodeTool;

    @Autowired
    private RunPipelineTool runPipelineTool;

    @Autowired
    GetDeployMachinesTool getDeployMachinesTool;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的miline助手")
                .goal("你的目标是更好的帮助用户")
                .constraints("专注于提供帮助")
                //内部工具
                .tools(Lists.newArrayList(
                                new ChatTool(),
                                new AskTool(),
                                new AttemptCompletionTool(),
                                new ListFilesTool(false),
                                new ExecuteCommandToolOptimized(),
                                new ReadFileTool(false),
                                new SearchFilesTool(false),
                                new ReplaceInFileTool(false),
                                new ListCodeDefinitionNamesTool(),
                                new WriteToFileTool(false),
                                createProjectTool,
                                generateGitCodeTool,
                                createPipelineTool,
                                runPipelineTool,
                                getDeployMachinesTool,
                                gitCloneTool,
                                gitCommitTool,
                                gitPushTool
                        )
                )
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .mcpTools(
                        RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT)
                                ? Lists.newArrayList(new ChatFunction(agentName, 20))
                                : Lists.newArrayList(createPipelineFunction, createProjectFunction, generateGitCodeFunction, runPipelineFunction, getDeployMachinesFunction, queryPipelineByGitUrlFunction)
                )
                .workflow("""
                            你是智能化系统，严格按照以下步骤执行：
                                - 根据projectName生成项目
                                        - 根据提供的projectId、env生成代码,
                                        - 拉取代码到本地
                                        - 根据需求及已有代码进行开发；注意：前端样式要按照pc端展示进行开发(如果提供了要实现的需求则进行代码实现，否则跳过代码实现并检查下没有语法bug后，再进行后续提交操作)
                                        - 先进入xxx-server/src/main/resources/static目录，执行npm i && npm run build
                                        - 添加完代码后，一定要将本地代码使用git_commit工具进行git commit，commit信息是如果是修复代码提交信息为：自动代码修复否则根据commit提交范式进行补充, 使用git_push进行git push
                                        - 根据projectId、pipelineName、gitUrl、gitName创建流水线
                                        - 根据projectId、pipelineId触发流水线进行发布
                                        - (询问的话)获取流水线部署机器信息
                        """)
                .meta(ImmutableMap.of(Const.HTTP_PORT, httpPort, Const.AGENT_SERVER_NAME, "miline_server", Const.HTTP_ENABLE_AUTH, "true"))
                .build();
    }

}


