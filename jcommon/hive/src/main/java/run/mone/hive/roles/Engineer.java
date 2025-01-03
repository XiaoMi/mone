package run.mone.hive.roles;

import com.google.common.collect.ImmutableMap;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.actions.WriteCode;
import run.mone.hive.actions.WriteCodeReview;
import run.mone.hive.actions.WriteTest;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @author goodjava@qq.com
 * 工程师(编写代码)
 */
@Slf4j
public class Engineer extends Role {


    public Engineer() {
        super("Engineer", "Engineer", "", "");
        // 设置工程师可以执行的动作
        setActions(List.of(new WriteCode()));
    }


    public CompletableFuture<Message> writeCode(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Message.builder()
                        .content("Code")
                        .role(getProfile())
                        .causeBy(WriteCode.class.getName())
                        .build();
            } catch (Exception e) {
                log.error("Error in writeCode", e);
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Message> writeTest(Message message) {
        WriteTest action = new WriteTest();
        initAction(action);
        return action.run(new ActionReq());
    }

    public CompletableFuture<Message> reviewCode(Message message) {
        WriteCodeReview action = new WriteCodeReview();
        initAction(action);
        return action.run(new ActionReq());
    }

}