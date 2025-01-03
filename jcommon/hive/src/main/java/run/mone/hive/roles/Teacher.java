package run.mone.hive.roles;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.actions.TeachingPlanBlock;
import run.mone.hive.actions.UserRequirement;
import run.mone.hive.actions.WriteTeachingPlanPart;
import run.mone.hive.context.Context;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class Teacher extends Role {
    private static final String DEFAULT_NAME = "Lily";
    private static final String PROFILE_TEMPLATE = "{teaching_language} Teacher";
    private static final String GOAL_TEMPLATE = "writing a {language} teaching plan part by part";
    private static final String CONSTRAINTS_TEMPLATE = "writing in {language}";

    private String courseTitle;
    private final Path workspacePath;
    private String language;

    public Teacher(Context context) {
        super(
                formatValue(DEFAULT_NAME, context),
                formatValue(PROFILE_TEMPLATE, context),
                formatValue(GOAL_TEMPLATE, context),
                formatValue(CONSTRAINTS_TEMPLATE, context)
        );
        this.setLlm(context.getDefaultLLM());
        this.workspacePath = Paths.get("/tmp/plan/");
        this.language = context.getLanguage();
        init();
    }

    @Override
    protected void init() {
        super.init();
        watchActions(Collections.singletonList(UserRequirement.class));
    }


    @Override
    public CompletableFuture<Message> run() {
        Message message = this.rc.getPollNews();
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create teaching plan parts
                List<Action> actions = TeachingPlanBlock.TOPICS.stream()
                        .map(topic -> new WriteTeachingPlanPart(message.getContent(), topic, language, llm))
                        .collect(Collectors.toList());

                // Execute all parts
                StringBuilder fullPlan = new StringBuilder();
                for (Action action : actions) {
                    Message result = action.run(new ActionReq()).join();
                    if (result != null) {
                        if (!fullPlan.isEmpty()) {
                            fullPlan.append("\n\n\n");
                        }
                        fullPlan.append(result.getContent());
                    }
                }

                // Save the plan
                String content = fullPlan.toString();
                savePlan(content);

                return Message.builder()
                        .content(content)
                        .role(getProfile())
                        .causeBy(WriteTeachingPlanPart.class.getName())
                        .build();

            } catch (Exception e) {
                log.error("Error in teacher execution", e);
                throw new RuntimeException(e);
            }
        });
    }

    private void savePlan(String content) throws IOException {
        Files.createDirectories(workspacePath);
        Path filePath = workspacePath.resolve(createFileName());
        Files.writeString(filePath, content);
        log.info("Saved teaching plan to: {}", filePath);
    }

    private String createFileName() {
        String title = courseTitle != null ? courseTitle : "teaching_plan";
        String sanitized = title.replaceAll("[#@$%!*&\\\\/:*?\"<>|\\n\\t ']", "_");
        return sanitized.replaceAll("_+", "_") + ".md";
    }

    private static String formatValue(String template, Context context) {
        return template
                .replace("{teaching_language}", context.getTeachingLanguage())
                .replace("{language}", context.getLanguage());
    }
} 