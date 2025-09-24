package run.mone.hive.actions.programmer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.ActionContext;
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
    public CompletableFuture<Message> run(ActionReq req, ActionContext context) {
        log.info("WriteCode");
        return CompletableFuture.supplyAsync(() -> {
            Message msg = this.function.apply(req, this, context);
            msg.setRole(req.getRole().getName());
            return msg;
        });
    }

}

