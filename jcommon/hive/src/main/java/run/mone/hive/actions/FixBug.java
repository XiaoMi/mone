package run.mone.hive.actions;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2024/12/25 17:19
 */
@Slf4j
public class FixBug extends Action {
    private static final String PROMPT_TEMPLATE = """
            ## BACKGROUND
            You are an expert software engineer tasked with fixing a bug in the code.
            
            ## CODE CONTEXT
            %s
            
            ## BUG DESCRIPTION
            %s
            
            ## TASK
            Analyze the code and the bug description. Provide a detailed explanation of the bug, its potential causes, and a step-by-step solution to fix it. Include any necessary code changes.
            
            Your response should include:
            1. Bug analysis
            2. Potential causes
            3. Step-by-step solution
            4. Code changes (if necessary)
            
            Please provide your response in Chinese.
            """;

    public FixBug() {
        super("FixBug", "Fix bug action to analyze and resolve software issues");
    }

    @Override
    public CompletableFuture<Message> run(ActionReq map) {
        String codeContext = (String) map.getOrDefault("codeContext", "No code context provided");
        String bugDescription = (String) map.getOrDefault("bugDescription", "No bug description provided");

        String prompt = String.format(PROMPT_TEMPLATE, codeContext, bugDescription);
        String content = this.llm.ask(prompt).join();
        log.info("FixBug Action Result: {}", content);
        return CompletableFuture.completedFuture(Message.builder().id(UUID.randomUUID().toString()).content(content).build());
    }

}