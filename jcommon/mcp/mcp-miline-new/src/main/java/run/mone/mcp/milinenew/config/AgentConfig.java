package run.mone.mcp.milinenew.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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

import static run.mone.hive.common.Constants.*;

/**
 * @author wangmin
 * @author goodjava@qq.com
 * @author shanwb
 * @date 2025/1/1
 */
@Configuration
@Slf4j
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
    private ScaleOrderFunction scaleOrderFunction;

    @Autowired
    QueryPipelineByGitUrlFunction queryPipelineByGitUrlFunction;

    @Autowired
    private K8sBatchDeployFunction k8sBatchDeployFunction;

    @Autowired
    private UserBatchFunction userBatchFunction;

    @Autowired
    private CancelPipelineFunction cancelPipelineFunction;

    @Autowired
    private JvmGenerationFunction jvmGenerationFunction;

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

    @Autowired
    private ScaleOrderTool scaleOrderTool;

    @Bean
    public RoleMeta roleMeta() {
        boolean agentModel = RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT);
        log.warn("{} is agent model:{}", agentName, agentModel);

        String profile = """
                你是一名优秀的MiLine智能体.
                MiLine是一套CI/CD平台，提供项目脚手架及部署相关能力.
                """;

        String goal = "你的目标是更好的帮助用户";
        String constraints = "专注于处理MiLine相关问题";
        String workflow = """
                你是功能完成的MiLine智能体，涉及MiLine相关的需求内部自闭环.
                接到需求后请先完成意图识别，再按对应的意图流程按步骤处理：
                   1. 项目初始化
                     - 根据projectName生成项目
                     - 根据提供的projectId、env生成代码
                     - 根据projectId、pipelineName、gitUrl、gitName创建流水线
                     - 根据projectId、pipelineId触发流水线进行发布
                   2. 其它单步诉求
                     - 调用内部tool完成
                   3. 非MiLine诉求
                     - 友好回复不在支持范围
                """;

        return RoleMeta.builder()
                .profile(profile)
                .goal(goal)
                .constraints(constraints)
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        createProjectTool,
                        generateGitCodeTool,
                        createPipelineTool,
                        runPipelineTool,
                        scaleOrderTool,
                        getDeployMachinesTool,
                        gitCloneTool,
                        gitCommitTool,
                        gitPushTool
                )
                )
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .mcpTools(
                        agentModel
                                ? Lists.newArrayList(new ChatFunction(agentName, 20))
                                : Lists.newArrayList(createPipelineFunction, createProjectFunction, generateGitCodeFunction, runPipelineFunction, getDeployMachinesFunction, queryPipelineByGitUrlFunction, k8sBatchDeployFunction,
                                        userBatchFunction, scaleOrderFunction, cancelPipelineFunction, jvmGenerationFunction)
                )
                .workflow(workflow)
                .meta(
                        agentModel
                                ? ImmutableMap.of(Const.HTTP_PORT, httpPort,
                                        Const.AGENT_SERVER_NAME, "miline_server",
                                        Const.HTTP_ENABLE_AUTH, "true")
                                : ImmutableMap.of(Const.HTTP_PORT, httpPort,
                                        Const.AGENT_SERVER_NAME, "miline_server",
                                        Const.HTTP_ENABLE_AUTH, "true",
                                        META_KEY_PROFILE, profile,
                                        META_KEY_WORKFLOW, workflow)
                )
                .build();
    }

}
