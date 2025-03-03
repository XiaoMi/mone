package run.mone.mcp.miapi.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.miapi.bo.ReqParamsBo;
import run.mone.mcp.miapi.enums.ApiTypeEnum;
import run.mone.mcp.miapi.http.HttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Component
public class MiApiFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "apiInformation";
    private String desc = "Query the interface information of the MiAPI interface platform";

    private static final Gson gson = new Gson();

    private static HttpClient httpClient = new HttpClient();

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["listApiInfo", "detailByUrl"],
                        "description": "The api operation to perform"
                    },
                    "type": {
                        "type": "string",
                        "enum": ["http", "dubbo"],
                        "description": "api type"
                    },
                    "keyword": {
                        "type": "string",
                        "description": "fuzzy search keyword"
                    },
                    "url": {
                        "type": "string",
                        "description": "api url"
                    }
                },
                "required": ["command", "type"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String command = (String) args.get("command");
            switch (command) {
                case "listApiInfo":
                    return getApiList((String) args.get("type"), (String) args.get("keyword"));
                case "detailByUrl":
                    return getApiDetail((String) args.get("type"), (String) args.get("url"));
                default:
                    return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Unknown command: " + command)),
                            true
                    );
            }
        } catch (Exception e) {
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }

    private String getHost () {
        return System.getenv().getOrDefault("API_HOST", "http://127.0.0.1:8999");
    }

    public McpSchema.CallToolResult getApiList(String type, String keyword) {
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(keyword) || !ApiTypeEnum.getNames().contains(type.toLowerCase())) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("error: Parameters cannot be empty")), true);
        }
        ReqParamsBo reqParamsBo = new ReqParamsBo();
        reqParamsBo.setKeyword(keyword);
        reqParamsBo.setProtocol(ApiTypeEnum.getCode(type.toLowerCase()));
        String result = getApiListMiApi(reqParamsBo);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(result)),
                false
        );
    }

    public McpSchema.CallToolResult getApiDetail(String type, String url) {
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(url) || !ApiTypeEnum.getNames().contains(type.toLowerCase())) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("error: Parameters cannot be empty")), true);
        }
        ReqParamsBo reqParamsBo = new ReqParamsBo();
        reqParamsBo.setPath(url);
        reqParamsBo.setProtocol(ApiTypeEnum.getCode(type.toLowerCase()));
        String apiDetail = getApiDetailMiApi(reqParamsBo);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(apiDetail)),
                false
        );
    }

    public String getApiListMiApi(ReqParamsBo reqParamsBo) {
        String result = "";
        try {
            JsonObject list = httpClient.post(getHost() + "/OpenApi/mcp/searchApi", gson.toJson(reqParamsBo));
            result = gson.toJson(list);
        } catch (IOException e) {
            result = "error: " + e.getMessage();
        }
        return result;
    }

    public String getApiDetailMiApi(ReqParamsBo reqParamsBo) {
        String result = "";
        try {
            JsonObject detail = httpClient.post(getHost() + "/OpenApi/mcp/detail", gson.toJson(reqParamsBo));
            result = gson.toJson(detail);
        } catch (IOException e) {
            result = "error: " + e.getMessage();
        }
        return result;
    }
}