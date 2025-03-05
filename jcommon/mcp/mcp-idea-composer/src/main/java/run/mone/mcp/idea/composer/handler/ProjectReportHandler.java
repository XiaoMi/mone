package run.mone.mcp.idea.composer.handler;

import com.google.gson.JsonObject;
import run.mone.mcp.idea.composer.service.ComposerService;

import java.util.concurrent.CompletableFuture;


/**
 * @author goodjava@qq.com
 * @date 2024/11/24 10:57
 * 生成项目分析报告和biz jar 的报告
 */
public class ProjectReportHandler extends AbstractBotHandler {

    public static final String name = "ProjectReportBot";

    public ProjectReportHandler() {
        super(name);
    }



    @Override
    public CompletableFuture<PromptResult> process(String prompt, PromptResult previousResult, ConversationContext context) {
        return null;
    }

    public String generateProjectReport(ConversationContext context, JsonObject json) {
        String displayPrompt = getDisplayPrompt();
        addAiChatMessage(displayPrompt, displayPrompt, Role.user, context);
        String res = ComposerService.getProjectReport(json);
        addAiChatMessage(getResDisplayPrompt(), res, Role.assistant, context);
        return res;
    }

    public String getDisplayPrompt() {
        return "Please help me analyze the project";
    }

    public String getResDisplayPrompt() {
        return "Analysis complete";
    }


}
