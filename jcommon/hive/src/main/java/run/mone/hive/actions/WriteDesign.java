package run.mone.hive.actions;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.Message;

/**
 * Action for creating technical design documentation
 */
@Slf4j
public class WriteDesign extends Action {

    public WriteDesign() {
        super("Create technical design documentation");
    }

    @Override
    public CompletableFuture<Message> run(Map<String, Object> map) {
        return CompletableFuture.supplyAsync(() -> {
            Message message = (Message) map.get("message");
            log.info("Creating technical design from message: {}", message);
            return new Message("Design");
        });
    }
} 