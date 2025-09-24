
package run.mone.hive.actions.chat;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.common.Prompts;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.MetaKey;
import run.mone.hive.schema.MetaValue;

/**
 * @author goodjava@qq.com
 * @date 2025/1/5 14:30
 */
public class ChatAction extends Action {

    private final String prompt = """
            You are an AI assistant. Your task is to respond to the user's question or message.
            Please provide a helpful and informative response.
            
            User's message: ${userMessage}
            
            Please respond to the user's message without any additional explanations or XML tags.
            """ + Prompts.PROMPT_FORMAT;


    public ChatAction() {
        setName("ChatAction");
        setDescription("Responds to user's chat messages");
        setFunction((req, action, context) -> {
            String userMessage = req.getMessage().getContent();
            String promptContent = AiTemplate.renderTemplate(prompt, ImmutableMap.of("userMessage", userMessage));
            String response = llm.syncChat(req.getRole(), promptContent);

            context.getCtx().addProperty("lastResponse", response);

            return Message.builder()
                    .content(response)
                    .meta(ImmutableMap.of(
                            MetaKey.builder().key("lastResponse").build(),
                            MetaValue.builder().value(response).build()
                    ))
                    .build();
        });
    }
}
