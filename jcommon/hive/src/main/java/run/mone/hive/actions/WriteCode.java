package run.mone.hive.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class WriteCode extends Action {

    @Override
    public CompletableFuture<Message> run(ActionReq req) {
        log.info("WriteCode");
        return CompletableFuture.supplyAsync(() -> Message.builder().content(this.function.apply(req, this)).build());
    }

}

