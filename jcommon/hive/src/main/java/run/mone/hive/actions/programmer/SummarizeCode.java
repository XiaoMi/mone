package run.mone.hive.actions.programmer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.CodeSummarizeContext;
import run.mone.hive.schema.Message;
import run.mone.hive.repository.Repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
public class SummarizeCode extends Action {
    private static final String PROMPT_TEMPLATE = """
            NOTICE
            Role: You are a professional software engineer, and your main task is to review the code.
            Language: Please use the same language as the user requirement, but the title and code should be still in English. For example, if the user speaks Chinese, the specific text of your answer should also be in Chinese.
            ATTENTION: Use '##' to SPLIT SECTIONS, not '#'. Output format carefully referenced "Format example".
            
            -----
            # System Design
            ```text
            %s
            ```
            -----
            # Task
            ```text
            %s
            ```
            -----
            %s
            
            ## Code Review All: Please read all historical files and find possible bugs in the files, such as unimplemented functions, calling errors, unreferences, etc.
            
            ## Call flow: mermaid code, based on the implemented function, use mermaid to draw a complete call chain
            
            ## Summary: Summary based on the implementation of historical files
            
            ## TODOs: Python dict[str, str], write down the list of files that need to be modified and the reasons. We will modify them later.
            """;

    private static final String FORMAT_EXAMPLE = """
            ## Code Review All
            
            ### a.py
            - It fulfills less of xxx requirements...
            - Field yyy is not given...
            -...
            
            ### b.py
            ...
            
            ### c.py
            ...
            
            ## Call flow
            ```mermaid
            flowchart TB
                c1-->a2
                subgraph one
                a1-->a2
                end
                subgraph two
                b1-->b2
                end
                subgraph three
                c1-->c2
                end
            ```
            
            ## Summary
            - a.py:...
            - b.py:...
            - c.py:...
            - ...
            
            ## TODOs
            {
                "a.py": "implement requirement xxx...",
            }
            """;

    private CodeSummarizeContext context;
    private Repository repository;

    public SummarizeCode() {
        super("SummarizeCode", "Summarize and review code implementation");
        this.context = new CodeSummarizeContext();
    }

    private CompletableFuture<String> summarizeCode(String prompt) {
        // 使用重试机制包装LLM调用
        try {
            return this.getLlm().ask(prompt);
        } catch (Exception e) {
            // 实现重试逻辑
            throw new RuntimeException("Failed to summarize code after retries", e);
        }
    }

    @Override
    public CompletableFuture<Message> run(ActionReq map, ActionContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path designPathname = Path.of(this.context.getDesignFilename());
                String designDoc = repository.getSystemDesign(designPathname.getFileName().toString());

                Path taskPathname = Path.of(this.context.getTaskFilename());
                String taskDoc = repository.getTask(taskPathname.getFileName().toString());

                List<String> codeBlocks = new ArrayList<>();
                for (String filename : this.context.getCodesFilenames()) {
                    String codeDoc = repository.getSourceCode(filename);
                    String codeBlock = String.format("```python\n%s\n```\n-----", codeDoc);
                    codeBlocks.add(codeBlock);
                }

                String prompt = String.format(PROMPT_TEMPLATE,
                        designDoc,
                        taskDoc,
                        String.join("\n", codeBlocks));

                return summarizeCode(prompt)
                        .thenApply(response -> new Message("summary", response))
                        .get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to run code summarization", e);
            }
        });
    }
}