package run.mone.mcp.milinenew.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.milinenew.function.CreatePipelineFunction;
import run.mone.mcp.milinenew.function.CreateProjectFunction;
import run.mone.mcp.milinenew.function.GenerateGitCodeFunction;
import run.mone.mcp.milinenew.function.RunPipelineFunction;
import run.mone.mcp.milinenew.tools.CreatePipelineTool;
import run.mone.mcp.milinenew.tools.GenerateGitCodeTool;
import run.mone.mcp.milinenew.tools.RunPipelineTool;

import run.mone.mcp.git.tool.GitCloneTool;
import run.mone.mcp.git.tool.GitCommitTool;
import run.mone.mcp.git.tool.GitPushTool;
import run.mone.mcp.milinenew.tools.RunPipelineTool;
import run.mone.mcp.milinenew.tools.CreateProjectTool;

import org.springframework.beans.factory.annotation.Value;

/**
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
                                new AttemptCompletionTool()
                                // new CreateProjectTool(),
                                // new GenerateGitCodeTool(),
                                // new ListFilesTool(false),
                                // new ExecuteCommandToolOptimized(),
                                // new ReadFileTool(false),
                                // new SearchFilesTool(false),
                                // new ReplaceInFileTool(false),
                                // new ListCodeDefinitionNamesTool(),
                                // new WriteToFileTool(false),
                                // new CreatePipelineTool(),
                                // new RunPipelineTool()
                        )
                )
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .mcpTools(
                    RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT) 
                        ? Lists.newArrayList(new ChatFunction("miline-new", 20)) 
                        : Lists.newArrayList(new CreatePipelineFunction(), new CreateProjectFunction(), new GenerateGitCodeFunction(), new RunPipelineFunction())
                )
                .workflow("""
                    你是智能化系统，严格按照以下步骤执行：
                        - 根据projectName生成项目
                        - 根据提供的projectId、env生成代码,
                        - 拉取代码到本地
                        - 修改service的pom文件，不要排除spring-boot-starter-tomcat这个包
                        - 根据需求进行代码修改，检查下没有语法bug在进行后续提交操作
                        - 完成后，将本地代码使用git_commit工具进行git commit，commit信息是自动代码修复, 使用git_push进行git push
                        - 根据projectId、pipelineName、gitUrl、gitName创建流水线
                        - 根据projectId、pipelineId触发流水线进行发布
                """)
                .build();
    }

}


