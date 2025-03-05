package run.mone.mcp.idea.composer.handler.biz;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.mcp.idea.composer.handler.CodeGeneratorTeam;
import run.mone.mcp.idea.composer.handler.ConversationContext;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 20:06
 */
@Slf4j
@Data
public class BotChainCall {


    public void executeProjectBotChain(BotChainCallContext botChainCallContext, JsonObject json) {
        try {
            log.info("use agent mode");
            ConversationContext conversationContext = new ConversationContext();
            CodeGeneratorTeam.generateCode(botChainCallContext.getPrompt(), botChainCallContext, conversationContext, json);
        }catch (Exception e){
            e.printStackTrace();
            log.error("exeute project bot chain error", e);
        }
    }
}