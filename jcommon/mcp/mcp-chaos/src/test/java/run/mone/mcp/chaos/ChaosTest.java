package run.mone.mcp.chaos;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chaos.function.ChaosFunction;
import run.mone.mcp.chaos.function.CreateChaosFunction;

public class ChaosTest {
    @Test
    public void testGetPipeline() {
        ChaosFunction  chaosFunction = new ChaosFunction();
        String pipeline = chaosFunction.getPipeline("http://1.1.1.1:80", "xxx", "1111");
        System.out.println(pipeline);

    }

    @Test
    public void testGetLists() {
        ChaosFunction chaosFunction = new ChaosFunction();
        String lists = chaosFunction.getChaosList("http://1.1.1.1:80", "xxx", "1111");
        System.out.println(lists);
    }

    @Test
    public void testGetDetail() {
        ChaosFunction chaosFunction = new ChaosFunction();
        String lists = chaosFunction.getChaosDetail("http://1.1.1.1:80", "xxx", "1111");
        System.out.println(lists);
    }

    @Test
    public void testExecute() {
        ChaosFunction chaosFunction = new ChaosFunction();
        String lists = chaosFunction.executeChaos("http://1.1.1.1:80", "xxx", "1111");
        System.out.println(lists);
    }

    @Test
    public void testRecover() {
        ChaosFunction chaosFunction = new ChaosFunction();
        String lists = chaosFunction.recoverChaos("http://1.1.1.1:80", "xxx", "1111");
        System.out.println(lists);
    }

    @Test
    public void testCreateChaos() {
        CreateChaosFunction createChaosFunction = new CreateChaosFunction();
        Map<String, Object> params = new HashMap<>();
        params.put("taskType", "1");
        params.put("experimentName", "1");
        params.put("projectId", "1");
        params.put("duration", "1");
        params.put("pipelineId", "1");
        params.put("mode", "1");
        params.put("depth", "1");
        params.put("containerNum", "1");
        params.put("userName", "1");
        Flux<McpSchema.CallToolResult> result = createChaosFunction.apply(params);
    }
}
