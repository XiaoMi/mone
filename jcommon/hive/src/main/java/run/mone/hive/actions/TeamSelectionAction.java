package run.mone.hive.actions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.AiMessage;
import run.mone.hive.schema.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 * 通过ai来组件团队
 * 这个系统,力争每个地方都可以定制,同时每个地方都可以被ai所抉择
 */
@Slf4j
public class TeamSelectionAction extends Action {

    private static final String SYSTEM_PROMPT = """
            You are a helpful AI assistant that selects and orders team members based on their roles and profiles.
            Given a JSON list of roles, you will determine the most effective order for these roles in a team.
            Consider the strengths, weaknesses, and synergies between different roles when determining the order.
            Your goal is to create a team order that maximizes efficiency and collaboration.
            Return the roles in a JSON array, ordered from first to last in the team structure.
            """;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public TeamSelectionAction(LLM llm) {
        super(SYSTEM_PROMPT);
        this.llm = llm;
    }

    public TeamSelectionAction() {
        super(SYSTEM_PROMPT);
    }

    @Override
    public CompletableFuture<Message> run(ActionReq req) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String rolesJson = req.getMessage().getContent();
                List<Role> roles = parseRoles(rolesJson);
                List<Role> selectedTeam = selectTeam(roles);
                String response = formatTeamResponse(selectedTeam);
                return new Message(response, "assistant", TeamSelectionAction.class.getName());
            } catch (Exception e) {
                log.error("Error in TeamSelectionAction execution", e);
                throw new RuntimeException("Failed to select team", e);
            }
        });
    }

    private List<Role> parseRoles(String rolesJson) throws Exception {
        return objectMapper.readValue(rolesJson, new TypeReference<>() {
        });
    }

    private List<Role> selectTeam(List<Role> roles) throws Exception {
        String rolesJson = objectMapper.writeValueAsString(roles);
        String prompt = "Given these roles, determine the most effective order for a team:\n" + rolesJson +
                "\nProvide your answer as a JSON array of role names in the optimal order.";

        String content = llm.chat(List.of(new AiMessage("user", prompt)), LLMConfig.builder().json(true).build());

        List<String> orderedRoleNames = objectMapper.readValue(content, new TypeReference<>() {
        });

        // Create a map for quick role lookup
        Map<String, Role> roleMap = roles.stream()
                .collect(Collectors.toMap(Role::getName, Function.identity()));

        // Create the ordered list of roles
        List<Role> orderedRoles = new ArrayList<>();
        for (String roleName : orderedRoleNames) {
            orderedRoles.add(roleMap.get(roleName));
        }

        return orderedRoles;
    }

    private String formatTeamResponse(List<Role> team) {

        StringBuilder response = new StringBuilder("Selected team members in execution order:\n\n");
        for (int i = 0; i < team.size(); i++) {
            Role role = team.get(i);
            response.append(i + 1).append(". ")
                    .append(role.getName())
                    .append(" (").append(role.getProfile()).append(")")
                    .append("\n");
        }
        return response.toString();

    }
}