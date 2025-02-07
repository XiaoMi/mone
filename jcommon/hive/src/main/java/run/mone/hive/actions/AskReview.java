package run.mone.hive.actions;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.Plan;
import run.mone.hive.utils.LLMProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class AskReview extends Action {

    private static final String SYSTEM_PROMPT = """
        You are a code reviewer and project manager assistant. Your task is to:
        1. Review the current plan or task result
        2. Provide constructive feedback
        3. Make a clear decision whether to:
           - Confirm the current result ("confirm")
           - Request changes ("redo")
           - Suggest plan modifications ("modify")
        
        Format your response in two parts:
        1. Detailed review comments
        2. Single word decision: "confirm", "redo", or "modify"
        
        Separate the two parts with "---"
        """;

    private static final String PLAN_REVIEW_PROMPT = """
        Review the following plan:
        
        %s
        
        Consider:
        1. Are the tasks well-defined and clear?
        2. Is the task breakdown logical and complete?
        3. Are there any missing dependencies?
        4. Is the scope appropriate?
        
        Provide your review and decision.
        """;

    private static final String TASK_REVIEW_PROMPT = """
        Review the following task result:
        
        Context:
        %s
        
        Current Task:
        %s
        
        Task Result:
        %s
        
        Consider:
        1. Does the result meet the task requirements?
        2. Is the implementation correct and complete?
        3. Are there any potential issues or improvements needed?
        
        Provide your review and decision.
        """;

    public AskReview() {
        this.setPrompt(SYSTEM_PROMPT);
    }

    public CompletableFuture<Message[]> run(List<Message> context, Plan plan, String trigger) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prompt;
                if ("PLAN_REVIEW".equals(trigger)) {
                    prompt = String.format(PLAN_REVIEW_PROMPT, plan.toString());
                } else {
                    String contextStr = context.stream()
                            .map(Message::getContent)
                            .reduce("", (a, b) -> a + "\n" + b);
                    
                    String currentTask = plan.getCurrentTask() != null ? 
                            plan.getCurrentTask().toString() : "No current task";
                    
                    String taskResult = plan.getCurrentTask() != null && 
                            plan.getCurrentTask().getResult() != null ? 
                            plan.getCurrentTask().getResult() : "No result";
                    
                    prompt = String.format(TASK_REVIEW_PROMPT, contextStr, currentTask, taskResult);
                }

                Message userMessage = new Message(prompt, "user");
                String response = LLMProvider.getInstance().chat(List.of(
                        new Message(SYSTEM_PROMPT, "system"),
                        userMessage
                ));

                // Split response into review comments and decision
                String[] parts = response.split("---");
                if (parts.length != 2) {
                    throw new RuntimeException("Invalid review response format");
                }

                String review = parts[0].trim();
                String decision = parts[1].trim().toLowerCase();

                return new Message[]{
                    new Message(review, "assistant", AskReview.class.getName()),
                    new Message(decision, "system")
                };
            } catch (Exception e) {
                log.error("Error in AskReview execution", e);
                throw new RuntimeException("Failed to generate review", e);
            }
        });
    }
} 