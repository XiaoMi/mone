package run.mone.mcp.idea.composer.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.Team;
import run.mone.hive.actions.AnalyzeArchitecture;
import run.mone.hive.actions.WriteDesign;
import run.mone.hive.actions.programmer.WriteCode;
import run.mone.hive.common.Constants;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.ActionReq;
import run.mone.mcp.idea.composer.handler.biz.BotChainCallContext;
import run.mone.mcp.idea.composer.handler.role.ProjectInitializer;
import run.mone.mcp.idea.composer.handler.role.ProjectArchitect;
import run.mone.mcp.idea.composer.handler.role.ProjectBuilder;

import java.util.List;
import java.util.Map;

import static run.mone.hive.llm.ClaudeProxy.*;

/**
 * 项目生成团队，负责创建新项目
 */
public class ProjectGeneratorTeam {

    public static void generateProject(String prompt, BotChainCallContext botChainCallContext, ConversationContext conversationContext, JsonObject json) {
        // 如果是自然语言输入，json可以为空，由ProjectArchitect来分析需求
        if (json == null) {
            json = new JsonObject();
        }
        LLMConfig config = LLMConfig.builder()
                .llmProvider(LLMProvider.CLAUDE_COMPANY)
                .url(getClaudeUrl())
                .version(getClaudeVersion())
                .maxTokens(getClaudeMaxToekns())
                .build();
        LLM llm = new LLM(config);
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);

        PromptResult promptResult = new PromptResult("", true);

        // 项目架构师：负责分析项目需求和定义项目结构
        ProjectArchitect projectArchitect = new ProjectArchitect();
        setArchitectActions(conversationContext, promptResult, projectArchitect, json);

        // 项目初始化器：负责创建项目基础结构和配置
        ProjectInitializer initializer = new ProjectInitializer();
        setInitializerActions(botChainCallContext, conversationContext, promptResult, initializer, json);

        // 项目构建者：负责生成基础代码和配置文件
        ProjectBuilder builder = new ProjectBuilder();
        setBuilderActions(botChainCallContext, conversationContext, promptResult, builder, json);

        team.hire(projectArchitect, initializer, builder);

        publishUserMessage(prompt, team);

        team.run(3);
    }

    private static void publishUserMessage(String prompt, Team team) {
        Message message = Message.builder()
                .id(java.util.UUID.randomUUID().toString())
                .role("user")
                .sentFrom("user")
                .sendTo(List.of("ProjectArchitect"))
                .content(prompt)
                .build();

        team.publishMessage(message);
    }

    private static LLM llm(ActionReq req) {
        if (req.getRole().getLlm() != null) {
            return req.getRole().getLlm();
        }
        return new LLM(LLMConfig.builder().debug(false).build());
    }

    private static void setArchitectActions(ConversationContext conversationContext, PromptResult promptResult, ProjectArchitect architect, JsonObject json) {
        AnalyzeArchitecture analyzeArchitecture = new AnalyzeArchitecture();
        analyzeArchitecture.setFunction((req, action, context) -> {
            // 使用LLM分析需求
            JsonObject projectInfo = architect.analyzeRequirement(req.getMessage().getContent(), llm(req));
            
            // 分析项目需求，生成项目结构设计
            StringBuilder analysis = new StringBuilder();
            analysis.append(String.format("项目基本信息：\n项目名称：%s\n项目类型：%s\n基础包名：%s\n描述：%s\n\n",
                    projectInfo.get("projectName").getAsString(),
                    projectInfo.get("projectType").getAsString(),
                    projectInfo.get("basePackage").getAsString(),
                    projectInfo.get("description").getAsString()));
            
            // 添加项目结构建议
            analysis.append(architect.analyzeProjectType(projectInfo.get("projectType").getAsString())).append("\n");
            
            // 添加依赖建议
            analysis.append(architect.suggestDependencies(projectInfo.get("projectType").getAsString()));
            
            String projectAnalysis = analysis.toString();
            promptResult.setContent(projectAnalysis);

            // 更新json对象，供后续步骤使用
            json.add("projectInfo", projectInfo);
            
            // 创建返回消息，并设置下一个接收者为ProjectInitializer
            return Message.builder()
                    .content(projectAnalysis)
                    .role(architect.getName())
                    .sendTo(List.of("ProjectInitializer"))
                    .build();
        });
        architect.setActions(Lists.newArrayList(analyzeArchitecture));
    }

    private static void setInitializerActions(BotChainCallContext botChainCallContext, ConversationContext conversationContext, PromptResult promptResult, ProjectInitializer initializer, JsonObject json) {
        WriteDesign writeDesign = new WriteDesign();
        writeDesign.setFunction((req, action, context) -> {
            // 获取项目信息，优先使用projectInfo，如果没有则使用原始json
            JsonObject projectInfo = json.has("projectInfo") ? json.getAsJsonObject("projectInfo") : json;
            
            // 创建项目基础结构
            String projectStructure = generateProjectStructure(projectInfo);
            promptResult.setContent(projectStructure);
            
            // 创建返回消息，并设置下一个接收者为ProjectBuilder
            return Message.builder()
                    .content(projectStructure)
                    .role(initializer.getName())
                    .sendTo(List.of("ProjectBuilder"))
                    .build();
        });
        initializer.setActions(Lists.newArrayList(writeDesign));
    }

    private static void setBuilderActions(BotChainCallContext botChainCallContext, ConversationContext conversationContext, PromptResult promptResult, ProjectBuilder builder, JsonObject json) {
        WriteCode writeCode = new WriteCode();
        writeCode.setFunction((req, action, context) -> {
            // 获取项目信息，优先使用projectInfo，如果没有则使用原始json
            JsonObject projectInfo = json.has("projectInfo") ? json.getAsJsonObject("projectInfo") : json;
            
            // 生成项目基础代码
            String generatedCode = generateProjectCode(projectInfo);
            return Message.builder().content(generatedCode).role(builder.getName()).build();
        });
        builder.setActions(Lists.newArrayList(writeCode));
    }

    private static String generateProjectStructure(JsonObject json) {
        ProjectInitializer initializer = new ProjectInitializer();
        
        // 获取项目信息
        String projectType = json.get("projectType").getAsString();
        String basePackage = json.get("basePackage").getAsString();
        String projectName = json.get("projectName").getAsString();
        
        // 生成目录列表
        List<String> paths = initializer.generateProjectStructure(projectType, basePackage);
        
        // 实际创建目录
        initializer.createProjectDirectories(projectName, projectType, basePackage);
        
        // 返回创建的目录结构信息
        StringBuilder structure = new StringBuilder();
        structure.append("创建项目目录结构：\n");
        paths.forEach(path -> structure.append("- ").append(path).append("\n"));
        
        return structure.toString();
    }

    private static String generateProjectCode(JsonObject projectInfo) {
        ProjectBuilder builder = new ProjectBuilder();
        Map<String, String> files = builder.generateProjectCode(
            projectInfo.get("projectType").getAsString(),
            projectInfo.get("basePackage").getAsString(),
            projectInfo.get("projectName").getAsString()
        );

        StringBuilder result = new StringBuilder();
        result.append("生成项目代码：\n");
        files.forEach((path, content) -> {
            result.append("文件：").append(path).append("\n");
            result.append("内容：\n").append(content).append("\n\n");
        });

        return result.toString();
    }
} 