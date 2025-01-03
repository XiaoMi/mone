package run.mone.hive;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.*;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.CodingContext;
import run.mone.hive.schema.Document;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.TaskList;
import run.mone.hive.utils.CommonUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class Engineer extends Role {
    private static final Logger logger = Logger.getLogger(Engineer.class.getName());
    private static final String IS_PASS_PROMPT = """
            {context}
            
            ----
            Does the above log indicate anything that needs to be done?
            If there are any tasks to be completed, please answer 'NO' along with the to-do list in JSON format;
            otherwise, answer 'YES' in JSON format.
            """;
    private static final String BUGFIX_FILENAME = "";

    // 角色属性
    private String name = "Alex";
    private String profile = "Engineer";
    private String goal = "write elegant, readable, extensible, efficient code";
    private String constraints = "the code should conform to standards like google-style and be modular and maintainable. " +
            "Use same language as user requirement";
    private int nBorg = 1;
    private boolean useCodeReview = false;
    private List<WriteCode> codeTodos = new ArrayList<>();
    private List<SummarizeCode> summarizeTodos = new ArrayList<>();
    private String nextTodoAction = "";
    private int nSummarize = 0;

    public Engineer() {
        super();
        setActions(Collections.singletonList(new WriteCode()));
        watchActions(Arrays.asList(
            WriteAction.class, SummarizeCode.class, WriteCode.class,
            WriteCodeReview.class, FixBug.class, WriteCodePlanAndChange.class
        ));
        this.nextTodoAction = CommonUtils.anyToName(WriteCode.class);
    }

    private static List<String> parseTasks(Document taskMsg) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> m = mapper.readValue(taskMsg.getContent(), Map.class);
            List<String> tasks = (List<String>) m.get(TaskList.KEY);
            return tasks;
        } catch (Exception e) {
            logger.warning("Failed to parse tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private CompletableFuture<Set<String>> actSpWithCr(boolean review) {
        Set<String> changedFiles = new HashSet<>();
        return CompletableFuture.supplyAsync(() -> {
            for (WriteCode todo : codeTodos) {
                try {
                    CodingContext codingContext = (CodingContext) todo.run(ImmutableMap.of()).join().getData();
                    if (review) {
                        WriteCodeReview action = new WriteCodeReview();
                        initAction(action);
                        codingContext = (CodingContext) action.run(ImmutableMap.of()).join().getData();
                    }

                    Set<String> dependencies = new HashSet<>();
                    dependencies.add(codingContext.getDesignDoc().getRootRelativePath());
                    dependencies.add(codingContext.getTaskDoc().getRootRelativePath());
                    
                    if (Engineer.this.getConfg().isInc()) {
                        dependencies.add(codingContext.getCodePlanAndChangeDoc().getRootRelativePath());
                    }

                    getProjectRepo().getSrcs().save(
                        codingContext.getFilename(),
                        new ArrayList<>(dependencies),
                        codingContext.getCodeDoc().getContent()
                    ).join();

                    Message msg = Message.builder()
                        .content(codingContext.toJson())
                        .instructContent(codingContext)
                        .role(getProfile())
                        .causeBy(WriteCode.class.getName())
                        .build();
                    
                    getRc().getMemory().add(msg);
                    changedFiles.add(codingContext.getCodeDoc().getFilename());
                } catch (Exception e) {
                    logger.warning("Failed to process todo: " + e.getMessage());
                }
            }
            if (changedFiles.isEmpty()) {
                logger.info("Nothing has changed.");
            }
            return changedFiles;
        });
    }


    private CompletableFuture<Message> actWriteCode() {
        return actSpWithCr(useCodeReview).thenApply(changedFiles -> 
            Message.builder()
                .content(String.join("\n", changedFiles))
                .role(getProfile())
                .causeBy(useCodeReview ? WriteCodeReview.class.getName() : WriteCode.class.getName())
                .sentFrom(this)
                .build()
        );
    }

    private CompletableFuture<Boolean> isFixbug() {
        return getProjectRepo().getDocs().get(BUGFIX_FILENAME)
            .thenApply(doc -> doc != null && !doc.getContent().isEmpty());
    }

}