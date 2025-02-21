
package run.mone.mcp.hologres.function;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.utils.JsonUtils;
import run.mone.m78.client.util.GsonUtils;
import run.mone.mcp.hologres.HoloBootstrap;
import run.mone.mcp.hologres.server.DataSourceMcpServer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HoloBootstrap.class)
public class SqlFuncitonTest {

    @Resource
    DataSourceMcpServer dataSourceMcpServer;

    @Test
    public void tearDownTestDatabase() {
        HoloFunction holoFunction = new HoloFunction(dataSourceMcpServer.hologresCarDataSource());
        Map<String, Object> parameters = new HashMap<>();

        // 将参数放入 Map 中
        parameters.put("tableName", "dim_org");
        parameters.put("startTime", "2024-03-01");
        parameters.put("endTime", "2024-04-20");
        McpSchema.CallToolResult apply = holoFunction.apply(parameters);
        log.info("resp : {}", GsonUtils.GSON.toJson(apply));
    }
}
