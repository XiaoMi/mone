package run.mone.mcp.idea.composer.handler;


import run.mone.mcp.idea.composer.handler.biz.BotChainCallContext;
import run.mone.mcp.idea.composer.handler.biz.ComposerImagePo;
import run.mone.mcp.idea.composer.handler.biz.Const;
import run.mone.mcp.idea.composer.handler.prompt.Prompt;

import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 10:59
 * <p>
 * 本质是执行一个bot
 */
public class CodeGenerationHandler extends AbstractBotHandler {

    private final BotChainCallContext botChainCallContext;

    public CodeGenerationHandler(BotChainCallContext botChainCallContext) {
        super("CodeGenerationBot");
        this.botChainCallContext = botChainCallContext;
    }

    @Override
    public CompletableFuture<PromptResult> process(String prompt, PromptResult previousResult, ConversationContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = getBotResponse(prompt, previousResult, context);
                return new PromptResult(formatCodeResponse(response));
            } catch (Exception e) {
                return new PromptResult("Code generation failed: " + e.getMessage(), false);
            }
        });
    }

    public String getBotResponse(String prompt, PromptResult previousResult, ConversationContext context) {
        String enhancedPrompt = buildCodePrompt(prompt, previousResult, context);
        CodeGeneratePromptHolder.lastPrompt = enhancedPrompt;
        String displayPrompt = getDisplayPrompt(prompt);
        addAiChatMessage(displayPrompt, enhancedPrompt, Role.user, context);
        String response = botChainCallContext.getBotClient().sendPrompt(enhancedPrompt, Prompt.CODE_GENERATE_SYSTEM_PROMPT, buildComposerImagePo(), true);
        return response;
    }

    public String getDisplayPrompt(String prompt) {
        String error = "";
        if (botChainCallContext.bugfix()) {
            error = botChainCallContext.getParams().get(Const.FIX_BUG_ERROR_INFO).toString();
            error = error.substring(0, Math.min(error.length(), 40)) + "...";
        }
        return botChainCallContext.bugfix() ? "Try fixing the bug:" + error : "Start implementing this feature:" + prompt;
    }

    private String buildCodePrompt(String prompt, PromptResult previousResult, ConversationContext context) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("项目报告: \n");
        String projectReport = context.getMessagesByHandler(ProjectReportHandler.name).stream().filter(it -> it.getRole().equals(Role.assistant.name())).findFirst().get().getContent();
        promptBuilder.append(projectReport).append("\n");

        if (previousResult != null) {
            promptBuilder.append("\n修改分析: \n").append(previousResult.getContent());
        }

        if (botChainCallContext.bugfix()) {
            promptBuilder.append("\n对于如下代码片段: \n").append(botChainCallContext.getParams().get(Const.FIX_BUG_CODE_CONTEXT));
            promptBuilder.append("\n我们遇到了这个错误: \n").append(botChainCallContext.getParams().get(Const.FIX_BUG_ERROR_INFO).toString());
        }

        String shellPrompt = "";

        String systemPrompt = CodePrompt.SR_DIFF_PROMPT;

        promptBuilder.append("\n").append(shellPrompt).append("\n").append(systemPrompt).append("\n\n<user_query>").append(prompt).append("</user_query>\n");
        return promptBuilder.toString();
    }

    private String formatCodeResponse(String response) {
        return response;
    }

    private ComposerImagePo buildComposerImagePo() {
        Object image = botChainCallContext.getParams().get(Const.COMPOSER_IMAGE_CONTEXT);
        if (image != null) {
            return (ComposerImagePo) image;
        }
        return null;
    }
}
