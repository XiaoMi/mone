package run.mone.mcp.cursor.miapi.tool;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.mone.hive.http.HttpClient;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;

@Component
public class ApiInfoTool implements ITool {

    @Value("${miapi.host}")
    private String host;

    private static HttpClient httpClient = new HttpClient();

    private static final Gson gson = new Gson();

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
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            JsonElement apiUrl = inputJson.has("url") ? inputJson.get("url") : null;
            result.addProperty("apiDetail", getApiDetail("http", apiUrl));
            return result;
        } catch (Exception e) {
            result.addProperty("error", "获取接口信息失败: " + e.getMessage());
            return result;
        }
    }

    public String getApiDetail(String type, JsonElement url) {
        if (StringUtils.isEmpty(type) || url == null || StringUtils.isEmpty(url.getAsString())) {
            return "error: Parameters cannot be empty";
        }
        JsonObject requestParams = new JsonObject();
        requestParams.add("path", url);
        if (type.equalsIgnoreCase("dubbo")) {
            requestParams.add("protocol", new JsonPrimitive(3));
        } else {
            requestParams.add("protocol", new JsonPrimitive(1));
        }
        return getApiDetailMiApi(requestParams);
    }

    public String getApiDetailMiApi(JsonObject reqParamsBo) {
        String result = "";
        try {
            JsonObject detail = httpClient.post(host + "/OpenApi/mcp/detail", reqParamsBo);
            result = gson.toJson(detail);
        } catch (IOException e) {
            result = "error: " + e.getMessage();
        }
        return result;
    }
}
