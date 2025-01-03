package run.mone.hive.actions;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.memory.Memory;
import run.mone.hive.roles.Debator;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/12/26 13:59
 */
@Slf4j
public class SpeakAloud extends Action {

    String PROMPT_TEMPLATE = """
            ## BACKGROUND
            Suppose you are %s, you are in a debate with %s.
            ## DEBATE HISTORY
            Previous rounds:
            %s
            ## YOUR TURN
            Now it's your turn, you should closely respond to your opponent's latest argument, state your position, defend your arguments, and attack your opponent's arguments,
            craft a strong and emotional response in 80 words, in %s's rhetoric and viewpoints, your will argue(用中文回答):
            """;


    @SneakyThrows
    @Override
    public CompletableFuture<Message> run(ActionReq map) {
        String opponentName = "";
        if (this.getRole() instanceof Debator debator) {
            opponentName = debator.getOpponentName();
        }

        String previous = "";
        if (map.get("memory") instanceof Memory memory) {
            previous = memory.getStorage().stream().map(it -> it.getSentFrom() + ":" + it.getContent()).collect(Collectors.joining("\n\n"));
        }

        String name = map.get("name").toString();

        String prompt = PROMPT_TEMPLATE.formatted(name, opponentName, previous, name);
        String content = this.llm.ask(prompt).join();
        log.info("{}:{}", name, content);
        TimeUnit.SECONDS.sleep(4);
        return CompletableFuture.completedFuture(Message.builder().id(UUID.randomUUID().toString()).content(content).build());
    }
}
