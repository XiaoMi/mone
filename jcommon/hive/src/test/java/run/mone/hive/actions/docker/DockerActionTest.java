
package run.mone.hive.actions.docker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DockerActionTest {

    private DockerAction dockerAction;
    private ActionContext context;
    private ActionReq req;

    @BeforeEach
    void setUp() {
        dockerAction = new DockerAction();
        LLM baseLLM = new LLM(LLMConfig.builder().json(false).build());
        dockerAction.setLlm(baseLLM);
        context = new ActionContext();
        req = new ActionReq();
        req.setRole(new Role("TestRole"));
        req.setMessage(Message.builder()
                .role("user")
                .content("拉取最新的 Ubuntu 镜像并运行一个交互式终端")
                .build());
    }

    @Test
    void testExecuteDockerCommand() throws ExecutionException, InterruptedException {
        CompletableFuture<Message> future = dockerAction.run(req, context);
        Message result = future.get();

        System.out.println(result);

        assertNotNull(result);
        assertTrue(result.getContent().contains("Docker"));
        assertTrue(result.getContent().contains("pull"));
        assertTrue(result.getContent().contains("run"));
        assertTrue(result.getContent().contains("ubuntu"));
    }
}
