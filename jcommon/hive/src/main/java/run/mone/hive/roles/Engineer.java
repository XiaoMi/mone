package run.mone.hive.roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.actions.WriteCode;
import run.mone.hive.actions.WriteCodePlanAndChange;
import run.mone.hive.actions.WriteCodeReview;
import run.mone.hive.actions.WritePRD;
import run.mone.hive.actions.WriteTest;
import run.mone.hive.schema.CodingContext;
import run.mone.hive.schema.Document;
import run.mone.hive.schema.Message;


@Slf4j
public class Engineer extends Role {

    private static final String TASK_FILE = "task.md";
    private static final String BUGFIX_FILE = "bugfix.md";
    private static final String CODE_FILE = "code.py";
    private static final String CODE_REVIEW_FILE = "code_review.md";

    private boolean useCodeReview = true;

    private String nextTodoAction;

    public Engineer(String name, String profile, String goal, String constraints) {
        super(name, profile, goal, constraints);
        init();
    }

    @Override
    protected void init() {
        super.init();
        // 设置工程师可以执行的动作
        List<Action> actions = Arrays.asList(
                new WriteCode(),
                new WriteCodeReview(),
                new WritePRD(),
                new WriteTest(),
                new WriteCodePlanAndChange()
        );
        setActions(actions);

        // 设置要监听的消息类型
        watchActions(Arrays.asList(
                WriteCode.class,
                WriteCodeReview.class,
                WritePRD.class,
                WriteTest.class,
                WriteCodePlanAndChange.class
        ));
    }


    public CompletableFuture<Message> writePRD(Message message) {
        WritePRD action = new WritePRD();
        initAction(action);
        return action.run(ImmutableMap.of());
    }

    public CompletableFuture<Message> writeCode(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> changedFiles = new ArrayList<>();
                List<CodingContext> todos = parseTasks();

                for (CodingContext codingContext : todos) {
                    try {
                        WriteCode writeCode = new WriteCode(codingContext, llm);
                        initAction(writeCode);
                        Message result = writeCode.run(ImmutableMap.of()).join();

                        if (result != null) {
                            Message msg = Message.builder()
                                    .content(codingContext.toJson())
                                    .instructContent(codingContext)
                                    .role("")
                                    .causeBy(WriteCode.class.getName())
                                    .build();

                            rc.getMemory().add(msg);
                            changedFiles.add(codingContext.getCodeDoc().getFilename());
                        }
                    } catch (Exception e) {
                        log.error("Failed to process todo: {}", e.getMessage());
                    }
                }

                return Message.builder()
                        .content(String.join("\n", changedFiles))
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
        return action.run(ImmutableMap.of());
    }

    public CompletableFuture<Message> reviewCode(Message message) {
        WriteCodeReview action = new WriteCodeReview();
        initAction(action);
        return action.run(ImmutableMap.of());
    }

    private List<CodingContext> parseTasks() {
        List<CodingContext> todos = new ArrayList<>();
        try {
            Document taskDoc = projectRepo.get(TASK_FILE).join();
            if (taskDoc != null && !taskDoc.getContent().isEmpty()) {
                // 这里需要实现任务解析逻辑
                // 将任务文档解析为多个CodingContext
                CodingContext context = new CodingContext();
                context.setTaskDoc(taskDoc);
                todos.add(context);
            }
        } catch (Exception e) {
            log.error("Error parsing tasks", e);
        }
        return todos;
    }

    private CompletableFuture<Boolean> isFixBug() {
        return projectRepo.get(BUGFIX_FILE)
                .thenApply(doc -> doc != null && !doc.getContent().isEmpty());
    }

    @Override
    public String toString() {
        return "Engineer{" +
                "name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", goal='" + goal + '\'' +
                ", useCodeReview=" + useCodeReview +
                '}';
    }
} 