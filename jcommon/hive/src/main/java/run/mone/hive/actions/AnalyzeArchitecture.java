package run.mone.hive.actions;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2025/1/3 11:39
 */
@Slf4j
public class AnalyzeArchitecture extends Action {

    @Override
    public CompletableFuture<Message> run(ActionReq req) {
        log.info("AnalyzeArchitecture");
        return CompletableFuture.supplyAsync(() -> Message.builder().content(this.function.apply(req, this)).build());
    }
}
