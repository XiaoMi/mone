package run.mone.mcp.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;

import java.util.ArrayList;
import java.util.List;

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
}