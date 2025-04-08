package run.mone.mcp.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private LLM llm;

    public Flux<String> chat(String message, List<Map<String, String>> context) {
        List<AiMessage> messages = new ArrayList<>();
        
        // Add context messages if available
        if (context != null) {
            for (Map<String, String> msg : context) {
                messages.add(new AiMessage(msg.get("role"), msg.get("content")));
            }
        }
        
        // Add current message
        messages.add(new AiMessage("user", message));

        return Flux.create(sink -> {
            llm.chat(messages, (content, jsonResponse) -> {
                sink.next(content);
                if ("[DONE]".equals(content.trim())) {
                    sink.complete();
                }
            });
        });
    }
}