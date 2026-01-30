// package run.mone.mcp.hera.analysis.config;

// import com.google.common.collect.Lists;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import run.mone.hive.mcp.function.ChatFunction;
// import run.mone.hive.mcp.service.RoleMeta;
// import run.mone.hive.roles.tool.*;
// import run.mone.mcp.git.tool.*;
// import run.mone.mcp.hera.analysis.tool.LogQueryFunctionTool;
// import run.mone.mcp.hera.analysis.tool.RootExceptionSpanTool;
// import run.mone.mcp.miline.tools.GetPipelineDetailTool;
// import run.mone.mcp.miline.tools.RunPipelineTool;


// @Configuration
// @ConditionalOnProperty(value="mcp.agent.mode", havingValue = "AGENT")
// @Slf4j
// public class AgentConfig {

//     @Value("${mcp.agent.name}")
//     private String agentName;

//     @Autowired
//     private RootExceptionSpanTool codeFixTool;

//     @Autowired
//     private GitCloneTool gitCloneTool;
//     @Autowired
//     private GitCommitTool gitCommitTool;
//     @Autowired
//     private GitPushTool gitPushTool;
//     @Autowired
//     private GitCheckoutNewBranchTool gitCheckoutNewBranchTool;
//     @Autowired
//     private GitCreateMergeRequestTool gitCreateMergeRequestTool;
//     @Autowired
//     private LogQueryFunctionTool logQueryFunctionTool;

//     private boolean isRemoteFile = false;

//     @Bean
//     public RoleMeta roleMeta() {
//         log.info("agent config roleMeta init");
//         ChatFunction chat = new ChatFunction(agentName, 20);
//         chat.setDesc("和%s聊天，让%s基于某一个traceId执行漏洞修复。支持各种形式如：'%s'、'请%s帮我修复问题'、'让%s根据cd573409c0e75849c73ddd1ca655dfcc修复漏洞'、'%s cd573409c0e75849c73ddd1ca655dfcc'等。支持上下文连续对话。");

//         return RoleMeta.builder()
//                 .name("OzHera漏洞修复专家")
//                 .profile("你是OzHera漏洞修复专家，精通漏洞修复，能够根据traceId修复trace上的各种问题")
//                 .goal("你的目标是根据用户输入的traceId，查询trace和log的相关信息，根据现有代码进行修复，最后进行发布")
//                 .constraints("不要探讨一些负面的东西,如果用户问你,你可以直接拒绝掉")
//                 //允许自动从知识库获取内容(意图识别的小模型)
// //                .webQuery(WebQuery.builder().autoWebQuery(true).modelType("bert").version("finetune-bert-20250605-73a29258").releaseServiceName("bert-is-network").build())
// //                .rag(Rag.builder().autoRag(true).modelType("bert").version("finetune-bert-20250605-ed8acbcf").releaseServiceName("bert-is-knowledge-base").build())
//                 //内部工具
//                 .tools(Lists.newArrayList(
//                         new ChatTool(),
//                         new AskTool(),
//                         new AttemptCompletionTool(),
//                         codeFixTool,
//                         new ListFilesTool(isRemoteFile),
//                         new ExecuteCommandToolOptimized(),
//                         new ReadFileTool(isRemoteFile),
//                         new SearchFilesTool(isRemoteFile),
//                         new ReplaceInFileTool(isRemoteFile),
//                         new ListCodeDefinitionNamesTool(),
//                         new WriteToFileTool(isRemoteFile),
//                         new RunPipelineTool(),
//                         new GetPipelineDetailTool(),
//                         gitCloneTool,
//                         gitCommitTool,
//                         gitPushTool,
//                         gitCheckoutNewBranchTool,
//                         gitCreateMergeRequestTool,
//                         logQueryFunctionTool
//                 ))
//                 //mcp工具
//                 .mcpTools(Lists.newArrayList(chat))
//                 .workflow("""
//                     你是代码级自动异常修复系统，当前系统环境是Linux，严格按照以下步骤执行： 
//                         1、根据traceId获取链路上根因节点的项目信息与异常信息 
//                         2、查询对应project和流水线（env）endTime前后半小时的该traceId的全部日志
//                         2、根据根因节点的projectId和envId（就是pipelineId）获取流水线详情 
//                         3、根据流水线详情中的gitUrl、gitBranch、gitCommitId调用git_clone工具进行git clone\s
//                         4、基于gitBranch和localPath创建一个新的分支，这个分支用于代码修复分支
//                         5、根据trace链路上的异常信息或者是日志中的信息，结合项目代码进行异常修复\s
//                         6、修复完成后，将本地代码使用git_commit工具进行git commit，commit信息是自动代码修复, 使用git_push进行git push\s
//                         7、根据流水线详情中的gitUrl、代码修复分支、gitBranch创建MR，将代码修复分支的代码合并到gitBranch中
//                         8、使用ask工具询问用户是否能够发布
//                         9、如果允许发布，根据projectId和envId调用RunPipelineTool进行发布
//                         10、最后，删除本地clone的代码仓库目录，使用bash解析器执行rm命令，千万不要使用zsh
//                 """)
//                 .build();
//     }
// }
