package run.mone.hive.roles;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.DebugError;
import run.mone.hive.actions.RunCode;
import run.mone.hive.actions.SummarizeCode;
import run.mone.hive.actions.WriteTest;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RunCodeContext;
import run.mone.hive.schema.TestingContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class QaEngineer extends Role {
    private String name = "Edward";
    private String profile = "QaEngineer";
    private String goal = "Write comprehensive and robust tests to ensure codes will work as expected without bugs";
    private String constraints = "The test code should conform to code standards, be modular, easy to read and maintain. " +
            "Use same language as user requirement";
    private int testRoundAllowed = 5;
    private int testRound = 0;

    public QaEngineer() {
        super();
        init();
    }

    @Override
    protected void init() {
        super.init();
        setActions(Collections.singletonList(new WriteTest()));
        watchActions(Arrays.asList(
            SummarizeCode.class,
            WriteTest.class,
            RunCode.class,
            DebugError.class
        ));
        this.testRound = 0;
    }

    private CompletableFuture<Void> writeTest(Message message) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Implementation for writing tests
                TestingContext context = new TestingContext();
                context.setMessage(message);
                WriteTest writeTest = new WriteTest();
                writeTest.run(ImmutableMap.of());
            } catch (Exception e) {
                log.error("Error writing test: ", e);
            }
        });
    }

    private CompletableFuture<Void> runCode(Message message) {
        return CompletableFuture.runAsync(() -> {
            try {
                RunCodeContext context = RunCodeContext.fromJson(message.getContent());
                RunCode runCode = new RunCode(context);
                runCode.run(ImmutableMap.of());
            } catch (Exception e) {
                log.error("Error running code: ", e);
            }
        });
    }

    private CompletableFuture<Void> debugError(Message message) {
        return CompletableFuture.runAsync(() -> {
            try {
                RunCodeContext context = RunCodeContext.fromJson(message.getContent());
                DebugError debugError = new DebugError(context);
                debugError.run(ImmutableMap.of());
            } catch (Exception e) {
                log.error("Error debugging: ", e);
            }
        });
    }

    @Override
    protected CompletableFuture<Message> act() {
        if (testRound > testRoundAllowed) {
            return CompletableFuture.completedFuture(Message.builder()
                .content("Exceeding " + testRoundAllowed + " rounds of tests, skip")
                .role(profile)
                .causeBy(WriteTest.class.getName())
                .build());
        }

        return CompletableFuture.supplyAsync(() -> {
            for (Message msg : getRc().news) {
                try {
                    String causeBy = msg.getCauseBy();
                    if (SummarizeCode.class.getName().equals(causeBy)) {
                        writeTest(msg).join();
                    } else if (WriteTest.class.getName().equals(causeBy) || 
                             DebugError.class.getName().equals(causeBy)) {
                        runCode(msg).join();
                    } else if (RunCode.class.getName().equals(causeBy)) {
                        debugError(msg).join();
                    }
                } catch (Exception e) {
                    log.error("Error processing message: ", e);
                }
            }
            testRound++;
            return Message.builder()
                .content("Round " + testRound + " of tests done")
                .role(profile)
                .causeBy(WriteTest.class.getName())
                .build();
        });
    }
} 