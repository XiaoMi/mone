package run.mone.mcp.idea.composer.handler;


import java.util.UUID;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 10:42
 */
public abstract class AbstractBotHandler implements BotHandler {

    protected BotHandler nextHandler;
    protected String name;

    public AbstractBotHandler(String name) {
        this.name = name;
    }

    public AbstractBotHandler setNext(BotHandler handler) {
        this.nextHandler = handler;
        return (AbstractBotHandler) handler;
    }

    protected void addAiChatMessage(String displayPrompt, String enhancedPrompt, Role role, ConversationContext context) {
        context.addMessage(new ChatMessage(role.name(), enhancedPrompt, name));
    }

    protected void recordPrompt(String prompt, ConversationContext context) {
        context.addMessage(new ChatMessage("user", prompt, name));
    }

    protected void recordResponse(String response, ConversationContext context) {
        context.addMessage(new ChatMessage("assistant", response, name));
    }

}
