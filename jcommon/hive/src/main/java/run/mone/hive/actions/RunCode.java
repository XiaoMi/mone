package run.mone.hive.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RunCodeContext;
import run.mone.hive.utils.JavaCodeExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
public class RunCode extends Action {
    private RunCodeContext context;

    private static final String PROMPT_TEMPLATE = """
            ## TASK
            You are a Java code execution engine. Your task is to compile and run the provided Java code, and return the execution result.
            
            ## CODE
            %s
            
            ## TEST CODE (if any)
            %s
            
            ## INSTRUCTIONS
            1. Compile the provided Java code.
            2. Execute the main method or any specified entry point.
            3. If test code is provided, run the tests after executing the main code.
            4. Capture and return all output, including any compilation errors, runtime exceptions, or test results.
            5. Provide a summary of the execution, including whether the code compiled successfully, any errors encountered, and test results if applicable.
            
            Please execute the code and provide the results.
            """;

    public RunCode() {
        super("RunCode", "Run the code and return the execution result");
    }


    public RunCode(RunCodeContext context) {
        this();
        this.context = context;
    }

    @Override
    public CompletableFuture<Message> run() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt = String.format(PROMPT_TEMPLATE, context.getCode(), context.getTestCode());
                String executionResult = this.llm.ask(prompt).join();
                String javaExecutionResult = JavaCodeExecutor.execute(context.getCode(), context.getTestCode());
                String finalResult = "LLM Execution Result:\n" + executionResult + "\n\nActual Java Execution Result:\n" + javaExecutionResult;

                return Message.builder()
                        .content(finalResult)
                        .role("RunCode")
                        .id(UUID.randomUUID().toString())
                        .causeBy(this.getClass().getName())
                        .build();
            } catch (Exception e) {

                throw new RuntimeException("Failed to execute code", e);
            }
        });
    }

}