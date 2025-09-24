package run.mone.hive.actions;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.ActionContext;
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
    public CompletableFuture<Message> run(ActionReq req, ActionContext context) {
        log.info("AnalyzeArchitecture");
        return CompletableFuture.supplyAsync(
                () -> Message.builder()
                        .sendTo(Lists.newArrayList("Design"))
                        .role(req.getRole().getProfile())
                        .content(this.function.apply(req, this, context).getContent())
                        .build()
        );
    }
}
