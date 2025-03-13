package run.mone.mcp.dayu.function;

import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.service.GenericService;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Data
@Slf4j
public class DayuFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "dayu_query";

    private String desc = "dayu query";

    private GenericService dubboSearchService;

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
                     "application": {
                         "type": "string",
                         "description": "application name"
                     },
                     "serviceName": {
                         "type": "string",
                         "description": "serviceName name"
                     }
                },
                "required": ["type","application","serviceName"]
            }
            """;


    public DayuFunction() {
        log.info("Initializing dayuFunction...");
    }

    private void initDubboSearchService() {
        ReferenceConfig<GenericService> dubboSearchServiceReferenceConfig = new ReferenceConfig<>();
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("dayu-mcp");
        dubboSearchServiceReferenceConfig.setApplication(applicationConfig);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(System.getProperty("nacos.serverAddr"));
        dubboSearchServiceReferenceConfig.setRegistry(registryConfig);
        dubboSearchServiceReferenceConfig.setGeneric("true");
        dubboSearchServiceReferenceConfig.setInterface("com.xiaomi.dayu.api.service.DubboSearchService");
        dubboSearchServiceReferenceConfig.setGroup(System.getProperty("dubbo.searchService.group"));
        dubboSearchServiceReferenceConfig.setVersion(System.getProperty("dubbo.searchService.version"));
        this.dubboSearchService = dubboSearchServiceReferenceConfig.get();
    }

    @SneakyThrows
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        if(this.dubboSearchService == null){
            initDubboSearchService();
        }
        String type = (String) args.get("type");
        try {
            String result = null;
            switch (type.toLowerCase()) {
                case "query":
                    result = query(args);
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

    private String query(Map<String, Object> args) {
        try {
            String[] paramTypes={"com.xiaomi.dayu.api.bo.DubboSearchReq"};
            HashMap<String, Object> params = new HashMap<>();
            params.put("application",args.get("application"));
            params.put("side","provider");
            params.put("includeInstance",false);
            params.put("includeMetadata",false);
            Object[] paramValues = { params};
            HashMap result = (HashMap) dubboSearchService.$invoke("searchService", paramTypes, paramValues);
            if(result != null && (Integer) result.get("code") == 0){
                return gson.toJson(((HashMap)result.get("data")).get("data"));
            }
            return null;
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
