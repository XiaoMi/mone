package run.mone.hive.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import run.mone.hive.actions.McpJudgeAction.McpServerConf;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class McpActionTest {

    private McpJudgeAction mcpJudgeAction;

    @BeforeEach
    public void setUp() {
        // 准备ServerParameters
        List<McpServerConf> serverConfs = new ArrayList<>();
        ServerParameters param = ServerParameters.builder("docker")
            .args("run", "-i", "--rm", "mcp/fetch", "--ignore-robots-txt")
            .build();
        serverConfs.add(McpServerConf.builder()
            .name("fetch")
            .description("Web content fetching and conversion for efficient LLM usage")
            .serverParameters(param)
            .build());

        mcpJudgeAction = new McpJudgeAction(serverConfs);
        mcpJudgeAction.setLlm(new LLM(LLMConfig.builder().json(true).build()));
    }

    @Test
    public void testMcpAction() throws InterruptedException, ExecutionException {
        // 准备请求
        ActionReq req = new ActionReq();
        req.setMessage(Message.builder()
            .content("需要从网络获取'今天的天气怎么样?'")
            .build());
        req.setRole(Role.builder()
            .name("user")
            .build());
        Message result = mcpJudgeAction.run(req, new ActionContext()).get();

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getContent());
    }

    @Test
    public void testMcpActionNoServer() throws InterruptedException, ExecutionException {
        // 准备请求
        ActionReq req = new ActionReq();
        req.setMessage(Message.builder()
            .content("需要进行大规模计算")
            .build());
        req.setRole(Role.builder()
            .name("user")
            .build());
        Message result = mcpJudgeAction.run(req, new ActionContext()).get();

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getContent());
    }
}
