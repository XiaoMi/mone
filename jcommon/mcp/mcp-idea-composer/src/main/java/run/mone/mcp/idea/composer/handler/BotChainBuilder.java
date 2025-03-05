package run.mone.mcp.idea.composer.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 10:43
 */
public class BotChainBuilder {

    private List<BotHandler> handlers = new ArrayList<>();

    public BotChainBuilder addHandler(BotHandler handler) {
        handlers.add(handler);
        return this;
    }

    public BotChain build() {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("At least one handler is required");
        }

        // 链接所有处理器
        for (int i = 0; i < handlers.size() - 1; i++) {
            ((AbstractBotHandler) handlers.get(i)).setNext(handlers.get(i + 1));
        }

        return new BotChain(handlers.get(0));
    }

}
