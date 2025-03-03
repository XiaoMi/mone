package run.mone.mcp.chaos;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chaos.function.ChaosFunction;
import run.mone.mcp.chaos.function.CreateChaosFunction;

public class ChaosTest {
    @Test
    public void testGetPipeline() {
        ChaosFunction  chaosFunction = new ChaosFunction();
        String pipeline = chaosFunction.getPipeline("http://10.38.219.182:80", "zhangxiaowei6", "150918");
        System.out.println(pipeline);

    }

    @Test
    public void testGetLists() {
        ChaosFunction chaosFunction = new ChaosFunction();
        String lists = chaosFunction.getChaosList("http://10.38.219.182:80", "zhangxiaowei6", "150918");
        System.out.println(lists);
    }

    @Test
    public void testGetDetail() {
        ChaosFunction chaosFunction = new ChaosFunction();
        String lists = chaosFunction.getChaosDetail("http://10.38.219.182:80", "zhangxiaowei6", "67ad64f1ca15c869d932ec23");
        System.out.println(lists);
    }

    @Test
    public void testExecute() {
        ChaosFunction chaosFunction = new ChaosFunction();
        String lists = chaosFunction.executeChaos("http://10.38.219.182:80", "zhangxiaowei6", "67c1712f400fb75dfeebe717");
        System.out.println(lists);
    }

    @Test
    public void testRecover() {
        ChaosFunction chaosFunction = new ChaosFunction();
        String lists = chaosFunction.recoverChaos("http://10.38.219.182:80", "zhangxiaowei6", "67c1712f400fb75dfeebe717");
        System.out.println(lists);
    }

    @Test
    public void testCreateChaos() {
        CreateChaosFunction createChaosFunction = new CreateChaosFunction();
        Map<String, Object> params = new HashMap<>();
        params.put("taskType", 1);
        params.put("experimentName", "test-mcp-chaos");
        params.put("projectId", "666");
        params.put("duration", "666");
        params.put("pipelineId", "666");
        params.put("mode", "666");
        params.put("depth", "666");
        params.put("containerNum", "666");
        params.put("userName", "xxx");
        McpSchema.CallToolResult result = createChaosFunction.apply(params);
        System.out.println(result);
    }
}
