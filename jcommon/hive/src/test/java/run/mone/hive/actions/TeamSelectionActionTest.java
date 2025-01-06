
package run.mone.hive.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class TeamSelectionActionTest {

    private TeamSelectionAction teamSelectionAction;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LLM llm = new LLM(LLMConfig.builder().build()); // 使用默认配置
        teamSelectionAction = new TeamSelectionAction(llm);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRun() throws ExecutionException, InterruptedException, JsonProcessingException {
        // 准备测试数据
        List<Role> roles = Arrays.asList(
            new Role("Developer", "Experienced in Java and Spring Boot"),
            new Role("Designer", "UI/UX specialist"),
            new Role("ProjectManager", "Agile methodologies expert")
        );
        String rolesJson = objectMapper.writeValueAsString(roles);

        // 创建 ActionReq
        ActionReq req = new ActionReq();
        req.setMessage(new Message(rolesJson, "user", "test"));

        // 执行 run 方法
        CompletableFuture<Message> future = teamSelectionAction.run(req);
        Message result = future.get();

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().contains("Developer"));
        assertTrue(result.getContent().contains("Designer"));
        assertTrue(result.getContent().contains("ProjectManager"));
    }
}
