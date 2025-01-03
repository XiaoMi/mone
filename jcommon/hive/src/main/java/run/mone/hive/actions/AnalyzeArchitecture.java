package run.mone.hive.actions;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2025/1/3 11:39
 */
@Slf4j
public class AnalyzeArchitecture extends Action{

    @Override
    public CompletableFuture<Message> run(Map<String, Object> map) {
        log.info("AnalyzeArchitecture");
        return CompletableFuture.completedFuture(Message.builder().content("AnalyzeArchitecture").build());
    }
}
