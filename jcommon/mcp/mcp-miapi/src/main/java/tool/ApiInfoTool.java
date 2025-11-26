package tool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.miapi.bo.ReqParamsBo;
import run.mone.mcp.miapi.enums.ApiTypeEnum;
import run.mone.mcp.miapi.http.HttpClient;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.util.List;

public class ApiInfoTool implements ITool {

    private static final Gson gson = new Gson();

    private static HttpClient httpClient = new HttpClient();

    @Override
    public String getName() {
        return "get_api_detail";
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
            This is a tool for querying interface information.

            **When to use:** When the user querys the interface information, choose to use this tool.

            **Output:** This tool will return the query interface information.
            """;
    }

    @Override
    public String parameters() {
        return """
                - name: (optional) The name of the interface to be queryed.
                - url: (optional) The interface path or service name to query.
                """;
    }

    @Override
    public String usage() {
        return """
            (Attention: If you are using this tool, you must return the api information within the json):

            Example 1: Get system overview
            ```json
            
            api info detail
            
            ```
            """;
    }

    @Override
    public JsonObject execute(JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            String apiName = inputJson.has("name") ? inputJson.get("name").getAsString() : "";
            String apiUrl = inputJson.has("url") ? inputJson.get("url").getAsString() : "";
            
            StringBuilder infoBuilder = new StringBuilder();

            result.addProperty("result", getApiDetail("http", apiUrl));
            return result;
        } catch (Exception e) {
            result.addProperty("error", "获取接口信息失败: " + e.getMessage());
            return result;
        }
    }

    public String getApiDetail(String type, String url) {
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(url) || !ApiTypeEnum.getNames().contains(type.toLowerCase())) {
            return "error: Parameters cannot be empty";
        }
        ReqParamsBo reqParamsBo = new ReqParamsBo();
        reqParamsBo.setPath(url);
        reqParamsBo.setProtocol(ApiTypeEnum.getCode(type.toLowerCase()));
        return getApiDetailMiApi(reqParamsBo);
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

    private String getHost () {
        return System.getenv().getOrDefault("API_HOST", "http://127.0.0.1:8999");
    }

} 