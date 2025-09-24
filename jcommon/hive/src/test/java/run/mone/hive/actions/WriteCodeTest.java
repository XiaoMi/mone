package run.mone.hive.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.actions.programmer.WriteCode;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.CodingContext;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WriteCodeTest {

    private WriteCode writeCode;
    private CodingContext context;

    @BeforeEach
    void setUp() {
        context = new CodingContext();
        context.setContext("用java生成代码");
        context.setRequirements("计算两数和");
    }

    @Test
    void testRun() throws ExecutionException, InterruptedException {
        writeCode = new WriteCode();

        CompletableFuture<Message> future = writeCode.run(new ActionReq(), new ActionContext());
        Message result = future.get();

        System.out.println(result);

        assertNotNull(result);
    }

}

