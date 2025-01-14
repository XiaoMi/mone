package run.mone.hive.actions.teacher;

import run.mone.hive.actions.Action;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import lombok.extern.slf4j.Slf4j;

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

    public WriteTeachingPlanPart(String context, String topic, String language, LLM llm) {
        super();
        this.context = context;
        this.topic = topic;
        this.language = language;
        this.setLlm(llm);
    }

    @Override
    public CompletableFuture<Message> run(ActionReq map, ActionContext context) {
        String prompt = PROMPT_TEMPLATE
                .replace("{topic}", topic)
                .replace("{context}", this.context)
                .replace("{language}", language);

        return llm.ask(prompt)
                .thenApply(response -> Message.builder()
                        .content(response)
                        .role("assistant")
                        .causeBy(WriteTeachingPlanPart.class.getName())
                        .build());
    }
} 