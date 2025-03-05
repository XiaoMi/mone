package run.mone.mcp.idea.composer.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import run.mone.hive.Team;
import run.mone.hive.actions.AnalyzeArchitecture;
import run.mone.hive.actions.WriteDesign;
import run.mone.hive.actions.programmer.WriteCode;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Architect;
import run.mone.hive.roles.Design;
import run.mone.hive.roles.Engineer;
import run.mone.hive.schema.Message;
import run.mone.mcp.idea.composer.handler.biz.BotChainCallContext;
import run.mone.mcp.idea.composer.service.ComposerService;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/1/3 20:19
 */
public class CodeGeneratorTeam {


    public static void generateCode(String prompt, BotChainCallContext botChainCallContext, ConversationContext conversationContext, JsonObject json) {
        LLM llm = new LLM(LLMConfig.builder().debug(false).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);

        PromptResult promptResult = new PromptResult("", true);

        //架构师
        Architect architect = new Architect();
        setActions(conversationContext, promptResult, architect, json);

        //设计者
        Design design = new Design();
        WriteDesign writeDesign = new WriteDesign();
        setActions(prompt, botChainCallContext, conversationContext, writeDesign, promptResult, design);

        //工程师
        Engineer engineer = new Engineer();
        WriteCode writeCode = new WriteCode();
        setActions(prompt, botChainCallContext, conversationContext, writeCode, promptResult, engineer);

        team.hire(architect, design, engineer);

        publishUserMessage(prompt, team);

        team.run(3);
    }

    private static void publishUserMessage(String prompt, Team team) {
        Message message = Message.builder()
                .id(java.util.UUID.randomUUID().toString())
                .role("user")
                .sentFrom("user")
                .sendTo(List.of("Architect"))
                .content(prompt)
                .build();

        team.publishMessage(message);
    }

    private static void setActions(String prompt, BotChainCallContext botChainCallContext, ConversationContext conversationContext, WriteCode writeCode, PromptResult promptResult, Engineer engineer) {
        writeCode.setFunction((req, action, context) -> Message.builder().content(new CodeGenerationHandler(botChainCallContext).getBotResponse(prompt, promptResult, conversationContext)).role(engineer.getName()).build());
        engineer.setActions(Lists.newArrayList(writeCode));
    }

    private static void setActions(String prompt, BotChainCallContext botChainCallContext, ConversationContext conversationContext, WriteDesign writeDesign, PromptResult promptResult, Design design) {
        writeDesign.setFunction((req, action, context) -> {
            String res = new FunctionalAnalysisHandler(botChainCallContext).getAnalysisResponse(prompt, promptResult, conversationContext);
            promptResult.setContent(res);
            return Message.builder().content(res).role(design.getName()).build();
        });
        design.setActions(Lists.newArrayList(writeDesign));
    }

    private static void setActions(ConversationContext conversationContext, PromptResult promptResult, Architect architect, JsonObject json) {
        AnalyzeArchitecture analyzeArchitecture = new AnalyzeArchitecture();
        analyzeArchitecture.setFunction((req, action, context) -> {
            ProjectReportHandler handler = new ProjectReportHandler();
            String res = handler.generateProjectReport(conversationContext, json);
            promptResult.setContent(res);
            return Message.builder().content(res).role(architect.getName()).build();
        });
        architect.setActions(Lists.newArrayList(analyzeArchitecture));
    }

}
