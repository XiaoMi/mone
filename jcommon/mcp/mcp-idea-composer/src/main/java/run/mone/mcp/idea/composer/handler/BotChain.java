package run.mone.mcp.idea.composer.handler;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 10:43
 */
@Slf4j
public class BotChain {

    private final BotHandler firstHandler;

    private final ConversationContext context;

    public BotChain(BotHandler firstHandler) {
        this.firstHandler = firstHandler;
        this.context = new ConversationContext();
    }

    public CompletableFuture<PromptResult> execute(String initialPrompt) {
        return processChain(firstHandler, initialPrompt, null);
    }

    private CompletableFuture<PromptResult> processChain(BotHandler handler, String prompt, PromptResult previousResult) {
        if (handler == null) {
            return CompletableFuture.completedFuture(previousResult);
        }

        return handler.process(prompt, previousResult, context)
                .thenCompose(result -> {
                    if (!result.isSuccess()) {
                        log.error(result.getError());
                        return CompletableFuture.completedFuture(result);
                    }
                    if (handler instanceof AbstractBotHandler) {
                        return processChain(((AbstractBotHandler) handler).nextHandler, prompt, result);
                    }
                    return CompletableFuture.completedFuture(result);
                });
    }

    public ConversationContext getContext() {
        return context;
    }

}
