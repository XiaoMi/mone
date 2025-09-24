package run.mone.hive.actions;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import run.mone.hive.common.AiTemplate;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * McpAction初始化的时候会传入mcp server的配置列表，
 * 然后根据配置列表，通过llm确定是否需要使用配置列表中的某个mcp server
 */
public class McpJudgeAction extends Action {

    private static final String JUDGE = """
        You are an MCP Server Management Assistant. Your task is to determine whether an MCP server needs to be activated.

        I will provide you with a user request and a list of available MCP server configurations. Please:
        1. Analyze if the user request requires MCP server processing
        2. If required, select the most suitable server from the configuration list
        3. If not required, return a message indicating no server is needed, and needServer is false
        4. If the user request is not clear, return a message indicating no server is needed, and needServer is false
        5. If none of the available MCP server configurations are suitable, return a message indicating no server is needed, and needServer is false

        Please respond in the following JSON format:
        {
            "needServer": true/false,
            "selectedServer": "server configuration name or null",
            "reason": "your reasoning for the decision"
        }

        Note: Only return true when the user request explicitly requires scenarios that necessitate MCP server services. Otherwise, return false to indicate no server is needed.

        Here is the user request: ${userRequest}

        Here is the available MCP server configurations: ${serverParameters}
        """;

    private List<McpServerConf> serverParameters;
    private Map<String, McpServerConf> serverMap;

    public McpJudgeAction(List<McpServerConf> serverParameters) {
        super("McpJudgeAction", "This Action is used to judge whether to use mcp server");
        this.serverParameters = serverParameters;
        this.serverMap = serverParameters.stream().collect(Collectors.toMap(McpServerConf::getName, Function.identity()));
        this.function = this::execute;
    }

    private Message execute(ActionReq req, Action action, ActionContext context) {
        McpJudgeResult judgeResult = judgeMcpServer(req, action, context);
        try {
            String res = new ObjectMapper().writeValueAsString(judgeResult);
            return Message.builder()
                .role(req.getRole().getName())
                .content(res)
                .build();
        } catch (Exception e) {
            log.error("Error in McpAction execution", e);
            return Message.builder()
                .role(req.getRole().getName())
                .content("Error in McpAction execution, no server is needed")
                .build();
        }
    }

    private McpJudgeResult judgeMcpServer(ActionReq req, Action action, ActionContext context) {
        try {
            String prompt = AiTemplate.renderTemplate(JUDGE, ImmutableMap.of("userRequest", req.getMessage().getContent(), "serverParameters", new ObjectMapper().writeValueAsString(serverParameters)));
            String res = llm.chat(prompt);
            return new ObjectMapper().readValue(res, McpJudgeResult.class);
        } catch (Exception e) {
            log.error("Error in McpAction execution", e);
            return McpJudgeResult.builder()
                    .needServer(false)
                    .selectedServer(null)
                    .reason("no server is needed")
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class McpServerConf {
        private String name;
        private String description;
        private ServerParameters serverParameters;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class McpJudgeResult {
        private boolean needServer;
        private String selectedServer;
        private String reason;
    }
}
