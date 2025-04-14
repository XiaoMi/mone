package run.mone.agentx.agent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.schema.Message;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2025/4/14 18:07
 */
@Service
@RequiredArgsConstructor
public class HiveManagerAgentService {

    private final LLM llm;

    private ConcurrentHashMap<String, ReactorRole> roleMap = new ConcurrentHashMap<>();

    public ReactorRole createRole(String owner, String clientId) {
        ReactorRole role = new ReactorRole("AgentManager", llm);
        role.getTools().add(new ChatTool());
        role.getTools().add(new AskTool());
        role.getTools().add(new AttemptCompletionTool());
        role.setOwner(owner);
        role.setClientId(clientId);
        role.run();
        return role;
    }


    //根据from进行隔离(比如Athena 不同 的project就是不同的from)
    public Flux<String> receiveMsg(Message message) {
        String from = message.getSentFrom().toString();
        if (!roleMap.containsKey(from)) {
            roleMap.putIfAbsent(from, createRole(from, message.getClientId()));
        }
        ReactorRole minzai = roleMap.get(from);
        return Flux.create(sink -> {
            message.setSink(sink);
            minzai.putMessage(message);
        });
    }

}
