package run.mone.mcp.idea.composer.handler;

import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 10:41
 */
public interface BotHandler {

    CompletableFuture<PromptResult> process(String prompt, PromptResult previousResult,ConversationContext context);

}
