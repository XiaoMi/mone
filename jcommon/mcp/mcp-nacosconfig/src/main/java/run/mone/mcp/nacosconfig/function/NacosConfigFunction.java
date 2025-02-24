package run.mone.mcp.nacosconfig.function;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Data
@Slf4j
public class NacosConfigFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "nacosconfig_query";

    private String desc = "nacos config query";

    private NacosConfigService nacosConfigService;

    private String configToolSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["query"],
                        "description": "Type of  operation to execute"
                     },
                     "namespace": {
                         "type": "string",
                         "description": "nacos namespace"
                     },
                     "group": {
                         "type": "string",
                         "description": "nacos group"
                     },
                     "dataId": {
                         "type": "string",
                         "description": "nacos dataId"
                     }    
                },
                "required": ["type", "namespace", "group", "dataId"]
            }
            """;


    public NacosConfigFunction(Properties properties) {
        log.info("Initializing NacosConfigFunction...");
        try {
            this.nacosConfigService = new NacosConfigService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
    @SneakyThrows
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String type = (String) args.get("type");
        String namespace = (String) args.get("namespace");
        String group = (String) args.get("group");
        String dataId = (String) args.get("dataId");
        try {
            String result = null;
            switch (type.toLowerCase()) {
                case "query":
                    result = query(namespace,group,dataId);
                    break;
                default:
                    throw new RuntimeException("Unsupported type: " + type);
            };
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result)),
                    false
            );
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private String query(String namespace, String group, String dataId) {
        try {
            String content = nacosConfigService.getConfig(dataId, group, 5000);
            return content;
        }   catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
