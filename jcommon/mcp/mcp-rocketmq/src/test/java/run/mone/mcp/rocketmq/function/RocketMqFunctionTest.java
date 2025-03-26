
package run.mone.mcp.rocketmq.function;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class RocketMqFunctionTest {
    @Test
    void sendMessage() {
        RocketMqFunction rocketMqFunction = new RocketMqFunction("staging-cnbj2-rocketmq.namesrv.api.xiaomi.net:9876", "jinqiu",
                "AKBWKSNVFYKRCKLILC", "7kTjIojftvyhuKXbGeKbYhYAG9lF9vuGXMaMMaht");
        Map<String, Object> args = Maps.newHashMap();
        args.put("topic", "callCenterVoiceAsrMq");
        args.put("message", "{\"content\":\"this is the test message send from mvp\"}");
        McpSchema.CallToolResult result = rocketMqFunction.apply(args);
        log.info("result:{}", result);
    }
}
