package run.mone.mcp.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

@Service
public class ChatService {

    @Autowired
    private LLM llm;

    public Flux<String> chat(String message, String context) {
        List<AiMessage> messages = new ArrayList<>();
        String msg = """
                聊天记录:
                %s
                最新的问题:
                %s
                """.formatted(context, message);
        messages.add(new AiMessage("user", msg));
        return llm.call(messages);
    }
    
    public Flux<String> sendProactiveMessage(String context) {
        List<AiMessage> messages = new ArrayList<>();
        String prompt = """
                根据以下聊天记录, 主动给用户发一条消息, 可以是问题或者打招呼, 以保持对话活跃:
                %s
                注意: 回复要简短自然, 不要明显表现出是机器人在主动找话题。
                """.formatted(context);
        messages.add(new AiMessage("user", prompt));
        return llm.call(messages);
    }
}