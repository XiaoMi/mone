package run.mone.hive.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.common.Prompts;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.CodingContext;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.AiMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class WriteCode extends Action {

    private CodingContext iContext;

    private static final String SYSTEM_PROMPT = """
            You are an expert software developer with extensive knowledge of best practices, design patterns, and clean code principles.
            Your task is to write elegant, readable, extensible, and efficient code based on the given requirements and context.
            Ensure that your code follows the specified language's conventions and is well-documented.
            """;

    private static final String USER_PROMPT = """
            Based on the following context and requirements, write code that meets the specified criteria:
            
            Context:
            %s
            
            Requirements:
            %s
            
            Please provide the code implementation, ensuring it is:
            1. Well-structured and organized
            2. Properly commented and documented
            3. Follows best practices and design principles
            4. Handles potential edge cases and errors
            5. Is efficient and optimized where possible
            """;

    public WriteCode() {
        super("WriteCode", "Write elegant, readable, extensible, efficient code");
    }

    public WriteCode(CodingContext context, LLM llm) {
        this();
        this.iContext = context;
        this.llm = llm;
    }

    @Override
    public CompletableFuture<Message> run(Map<String, Object> map) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String generatedCode = generateCode();
                log.info("write code:{}", generatedCode);
                return Message.builder()
                        .data(new CodingContext())
                        .content(generatedCode)
                        .role(WriteCode.class.getName())
                        .causeBy(this.getClass().getName())
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate code", e);
            }
        });
    }

    private String generateCode() {
        if (true) {
            return "Code";
        }
        String context = iContext.getContext();
        String requirements = iContext.getRequirements();
        String prompt = String.format(USER_PROMPT, context, requirements);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().role("system").content(SYSTEM_PROMPT).build());
        messages.add(AiMessage.builder().role("user").content(prompt).build());

        return llm.chat(messages, Prompts.SYSTEM_PROMPT);
    }
}

