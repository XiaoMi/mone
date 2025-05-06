package run.mone.mcp.idea.composer.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import run.mone.hive.Team;
import run.mone.hive.actions.AnalyzeArchitecture;
import run.mone.hive.actions.WriteDesign;
import run.mone.hive.actions.programmer.WriteCode;
import run.mone.hive.common.Constants;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Message;
import run.mone.mcp.idea.composer.handler.biz.BotChainCallContext;
import run.mone.mcp.idea.composer.handler.role.Architect;
import run.mone.mcp.idea.composer.handler.role.Design;
import run.mone.mcp.idea.composer.handler.role.Engineer;

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
        setActions(botChainCallContext, conversationContext, writeDesign, promptResult, design, json);

        //工程师
        Engineer engineer = new Engineer();
        WriteCode writeCode = new WriteCode();
        setActions(botChainCallContext, conversationContext, writeCode, promptResult, engineer);

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

    private static void setActions(BotChainCallContext botChainCallContext, ConversationContext conversationContext, WriteCode writeCode, PromptResult promptResult, Engineer engineer) {
        writeCode.setFunction((req, action, context) -> {
            String rules = "";
            Object additionalData = conversationContext.getAdditionalData();
            if (additionalData != null) {
                JsonElement element = ((JsonObject) conversationContext.getAdditionalData()).get("rules");
                if (element != null) {
                    rules = element.getAsString();
                }
            }
            return Message.builder()
                    .content(new CodeGenerationHandler(botChainCallContext).getBotResponse(
                            conversationContext.getUserQuery(),
                            rules,
                            promptResult,
                            conversationContext)
                    )
                    .role(engineer.getName())
                    .build();
        });
        engineer.setActions(Lists.newArrayList(writeCode));
    }

    private static void setActions(BotChainCallContext botChainCallContext, ConversationContext conversationContext, WriteDesign writeDesign, PromptResult promptResult, Design design, JsonObject json) {
        writeDesign.setFunction((req, action, context) -> {
            if (json.has(Constants.FROM)) {
                return Message.builder().content("").role(design.getName()).build();
            }

            String res = new FunctionalAnalysisHandler(botChainCallContext).getAnalysisResponse(conversationContext.getUserQuery(), promptResult, conversationContext);
            promptResult.setContent(res);
            return Message.builder().content(res).role(design.getName()).build();
        });
        design.setActions(Lists.newArrayList(writeDesign));
    }

    private static void setActions(ConversationContext conversationContext, PromptResult promptResult, Architect architect, JsonObject json) {
        AnalyzeArchitecture analyzeArchitecture = new AnalyzeArchitecture();
        analyzeArchitecture.setFunction((req, action, context) -> {
            if (json.has(Constants.FROM)) {
                return Message.builder().content("").role(architect.getName()).build();
            }

            ProjectReportHandler handler = new ProjectReportHandler();
            String res = handler.generateProjectReport(conversationContext, json);
            promptResult.setContent(res);
            return Message.builder().content(res).role(architect.getName()).build();
        });
        architect.setActions(Lists.newArrayList(analyzeArchitecture));
    }

}
