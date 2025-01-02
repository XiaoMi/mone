package run.mone.hive.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.schema.CodingContext;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WriteCodeTest {

    private WriteCode writeCode;
    private CodingContext context;

    private BaseLLM baseLLM = new BaseLLM(LLMConfig.builder().json(false).build());

    @BeforeEach
    void setUp() {
        context = new CodingContext();
        context.setContext("用java生成代码");
        context.setRequirements("计算两数和");
    }

    @Test
    void testRun() throws ExecutionException, InterruptedException {
        writeCode = new WriteCode(context, baseLLM);

        CompletableFuture<Message> future = writeCode.run();
        Message result = future.get();

        System.out.println(result);

        assertNotNull(result);
    }

}

