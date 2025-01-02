package run.mone.hive.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.mone.hive.schema.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@EqualsAndHashCode(callSuper = true)
public class WriteCodeReview extends Action {

    public WriteCodeReview() {
        super("WriteCodeReview", "Review and improve the code");
    }


    @Override
    public CompletableFuture<Message> run() {
        return null;
    }

    @Override
    public CompletableFuture<Message> run(List<Message> history) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String review = generateCodeReview(history);
                return Message.builder()
                        .content(review)
                        .role("WriteCodeReview")
                        .causeBy(this.getClass().getName())
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate code review", e);
            }
        });
    }

    private String generateCodeReview(List<Message> history) {
        String codeContent = extractCodeFromHistory(history);
        String prompt = String.format("""
                Review the following code and check for:
                1. Code style and best practices
                2. Potential bugs
                3. Security issues
                4. Performance considerations
                
                Code:
                %s
                """, codeContent);

        return llm.ask(prompt).join();
    }

    private String extractCodeFromHistory(List<Message> history) {
        // Implement logic to extract code content from history
        // This is a placeholder implementation
        return history.stream()
                .filter(message -> message.getRole().equals("user"))
                .map(Message::getContent)
                .findFirst()
                .orElse("");
    }

} 