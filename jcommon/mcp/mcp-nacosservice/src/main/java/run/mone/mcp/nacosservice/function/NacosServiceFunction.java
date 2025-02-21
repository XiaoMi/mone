package run.mone.mcp.nacosservice.function;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Data
@Slf4j
public class NacosServiceFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "nacosservice_query";

    private String desc = "nacos service query";

    private NacosNamingService nacosNamingService;

    private Gson gson = new Gson();

    private String configToolSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["query"],
                        "description": "Type of  operation to execute"
                     },
                     "serviceName": {
                         "type": "string",
                         "description": "service name"
                     }   
                },
                "required": ["type", "serviceName"]
            }
            """;


    public NacosServiceFunction(Properties properties) {
        log.info("Initializing nacosServiceFunction...");
        this.nacosNamingService = new NacosNamingService(properties);

    }
    @SneakyThrows
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String type = (String) args.get("type");
        String serviceName = (String) args.get("serviceName");
        try {
            String result = null;
            switch (type.toLowerCase()) {
                case "query":
                    result = query(serviceName);
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

    private String query(String serviceName) {
        try {
            List<Instance> allInstances = nacosNamingService.getAllInstances(serviceName);
            log.info("allInstances:{}", gson.toJson(allInstances));
            return gson.toJson(allInstances);
        } catch (NacosException e) {
			throw new RuntimeException(e);
		}
	}
}
