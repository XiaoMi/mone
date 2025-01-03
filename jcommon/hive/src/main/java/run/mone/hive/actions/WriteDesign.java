package run.mone.hive.actions;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

/**
 * Action for creating technical design documentation
 * @author goodjava@qq.com
 */
@Slf4j
public class WriteDesign extends Action {

    @Override
    public CompletableFuture<Message> run(ActionReq req) {
        log.info("WriteDesign");
        return CompletableFuture.supplyAsync(() -> Message.builder().role(req.getRole().getProfile()).sendTo(Lists.newArrayList("Engineer")).content(this.function.apply(req, this)).build());
    }
} 