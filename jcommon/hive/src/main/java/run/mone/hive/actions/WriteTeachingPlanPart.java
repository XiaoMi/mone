package run.mone.hive.actions;

import run.mone.hive.llm.BaseLLM;
import run.mone.hive.schema.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WriteTeachingPlanPart extends Action {
    private String topic;
    private String context;
    private String language;

    private static final String PROMPT_TEMPLATE = """
            As a professional teacher, please write a teaching plan for the following lesson.
            Focus on the {topic} part.
            
            Lesson content:
            {context}
            
            Requirements:
            1. Write in {language}
            2. Be specific and practical
            3. Follow standard teaching methodology
            4. Output in markdown format
            """;

    public WriteTeachingPlanPart(String context, String topic, String language, BaseLLM llm) {
        super();
        this.context = context;
        this.topic = topic;
        this.language = language;
        this.setLlm(llm);
    }

    @Override
    public CompletableFuture<Message> run(Map<String, Object> map) {
        String prompt = PROMPT_TEMPLATE
                .replace("{topic}", topic)
                .replace("{context}", context)
                .replace("{language}", language);

        return llm.ask(prompt)
                .thenApply(response -> Message.builder()
                        .content(response)
                        .role("assistant")
                        .causeBy(WriteTeachingPlanPart.class.getName())
                        .build());
    }
} 