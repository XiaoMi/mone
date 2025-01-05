
package run.mone.hive.actions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.AiMessage;
import run.mone.hive.schema.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ActionSelectionAction extends Action {

    private static final String SYSTEM_PROMPT = """
            You are an AI assistant specialized in selecting the most minimal yet effective set of Actions for a given Role. \s
            Based on the Role's profile, goal, and constraints, you will determine the smallest possible set of Actions that can achieve the desired outcome. \s
            Consider the dependencies, sequences, and critical path when selecting and ordering Actions. \s
            Your goal is to create a streamlined Action set that achieves the Role's objectives with maximum efficiency and minimal redundancy. \s
            The Actions in the JSON array will be ordered in their exact execution sequence - each Action should be completed before moving to the next one. \s
            Return only the essential Actions in a JSON array, representing both priority and execution order.
            
            example: 
            {"content":["WriteCode"]}
            """;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ActionSelectionAction(LLM llm) {
        this();
        this.llm = llm;
    }

    public ActionSelectionAction() {
        super("Action选择", "为Role选择合适的Action集合");
        this.prompt = SYSTEM_PROMPT;
    }

    @Override
    public CompletableFuture<Message> run(ActionReq req) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String roleJson = req.getMessage().getContent();
                Map<String, String> roleInfo = parseRoleInfo(roleJson);
                List<String> selectedActions = selectActions(roleInfo);
                String response = formatActionResponse(selectedActions);
                return new Message(response, "assistant", ActionSelectionAction.class.getName());
            } catch (Exception e) {
                log.error("Error in ActionSelectionAction execution", e);
                throw new RuntimeException("Failed to select actions", e);
            }
        });
    }

    private Map<String, String> parseRoleInfo(String roleJson) throws Exception {
        return objectMapper.readValue(roleJson, new TypeReference<>() {});
    }

    private List<String> selectActions(Map<String, String> roleInfo) throws Exception {
        String prompt = "Given this role information, determine the most effective set of Actions:\n" +
                objectMapper.writeValueAsString(roleInfo) +
                "\nProvide your answer as a JSON array of action names in order of priority.";

        String content = llm.chat(List.of(new AiMessage("user", prompt)), LLMConfig.builder().json(true).build());

        return objectMapper.readValue(content, new TypeReference<>() {});
    }

    private String formatActionResponse(List<String> actions) {
        StringBuilder response = new StringBuilder("Selected actions for the role in priority order:\n\n");
        for (int i = 0; i < actions.size(); i++) {
            response.append(i + 1).append(". ").append(actions.get(i)).append("\n");
        }
        return response.toString();
    }
}
