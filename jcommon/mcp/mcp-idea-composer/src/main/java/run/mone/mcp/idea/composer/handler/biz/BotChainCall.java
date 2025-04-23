package run.mone.mcp.idea.composer.handler.biz;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.mcp.idea.composer.handler.CodeGeneratePromptHolder;
import run.mone.mcp.idea.composer.handler.CodeGeneratorTeam;
import run.mone.mcp.idea.composer.handler.prompt.CodePrompt;
import run.mone.mcp.idea.composer.handler.ConversationContext;
import run.mone.mcp.idea.composer.handler.prompt.Prompt;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 20:06
 */
@Slf4j
@Data
public class BotChainCall {


    public void executeProjectBotChain(BotChainCallContext botChainCallContext, JsonObject json, String isFull) {
        try {
            log.info("use agent mode");
            if(StringUtils.isEmpty(isFull)) {
                ConversationContext conversationContext = new ConversationContext();
                conversationContext.setAdditionalData(json);
                CodeGeneratorTeam.generateCode(botChainCallContext.getPrompt(), botChainCallContext, conversationContext, json);
            }else{
                botChainCallContext.getBotClient().sendPrompt(getRetryPrompt(Boolean.valueOf(isFull)), Prompt.CODE_GENERATE_SYSTEM_PROMPT, buildComposerImagePo(botChainCallContext), true);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("exeute project bot chain error", e);
        }
    }

    private ComposerImagePo buildComposerImagePo(BotChainCallContext botChainCallContext) {
        Object image = botChainCallContext.getParams().get(Const.COMPOSER_IMAGE_CONTEXT);
        if (image != null) {
            return (ComposerImagePo) image;
        }
        return null;
    }

    private String getRetryPrompt(boolean full){
        String input = CodeGeneratePromptHolder.lastPrompt;
        if (full) {
            if (!input.endsWith(CodePrompt.SR_FULL_PROMPT)) {
                input = input + "\n" + CodePrompt.SR_FULL_PROMPT;
            }
        } else {
            if (input.endsWith(CodePrompt.SR_FULL_PROMPT)) {
                input = input.substring(0, input.length() - ("\n" + CodePrompt.SR_FULL_PROMPT).length());
            }
        }
        return input;
    }
}