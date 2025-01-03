package run.mone.hive.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.CodingContext;
import run.mone.hive.schema.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Action for planning code changes and implementing them
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class WriteCodePlanAndChange extends Action {
    
    private static final String PROMPT_TEMPLATE = """
            NOTICE
            Role: You are a professional software engineer. Your task is to:
            1. Analyze the current code and requirements
            2. Plan necessary code changes
            3. Implement the changes
            
            Requirements:
            %s
            
            Current Code:
            %s
            
            Please provide:
            1. Analysis of current code
            2. Planned changes with rationale
            3. Implementation details
            4. Test considerations
            """;

    private CodingContext context;

    public WriteCodePlanAndChange() {
        super("WriteCodePlanAndChange", "Plan and implement code changes");
    }

    @Override
    public CompletableFuture<Message> run(Map<String, Object> map) {
        Message message = (Message) map.get("message");
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Planning and implementing code changes for: {}", message);
                
                // Format prompt with requirements and current code
                String prompt = String.format(PROMPT_TEMPLATE, 
                    context.getRequirements(),
                    context.getCurrentCode());

                // Get plan and changes from LLM
                String response = this.getLlm().ask(prompt).get();
                
                // Parse and apply changes
                applyChanges(response);
                
                return Message.builder()
                    .content(response)
                    .role("Engineer")
                    .causeBy(this.getClass().getName())
                    .build();
                    
            } catch (Exception e) {
                log.error("Failed to plan and implement code changes", e);
                throw new RuntimeException("Code planning and implementation failed", e);
            }
        });
    }

    private void applyChanges(String planAndChanges) {
        // TODO: Parse the LLM response and apply the changes
        // This could include:
        // 1. Extracting planned changes
        // 2. Validating changes
        // 3. Applying changes to files
        // 4. Running tests
        log.info("Applying planned changes: {}", planAndChanges);
    }

    public void setContext(CodingContext context) {
        this.context = context;
    }
} 