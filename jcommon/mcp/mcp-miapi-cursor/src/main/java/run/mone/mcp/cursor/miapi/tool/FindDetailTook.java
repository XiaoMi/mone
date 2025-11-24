package run.mone.mcp.cursor.miapi.tool;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.mone.hive.http.HttpClient;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;

@Component
public class FindDetailTook implements ITool {

    private static HttpClient httpClient = new HttpClient();

    private static final Gson gson = new Gson();
    @Value("${miapi.host}")
    private String host;

    @Override
    public String getName() {
        return "find_detail";
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
                1. 只查询接口的基本信息，用于接口测试。
                2.不做其他功能回答和调用。
                """;
    }

    @Override
    public String parameters() {
        return """
                - apiType: (Required) The type of interface to be tested. For HTTP-type interfaces, use 1 (default value). For Dubbo-type interfaces, use 3.
                - url: (Required) The interface address or interface path to be tested.
                """;
    }

    @Override
    public String usage() {
        return """
            (Attention: If you are using this tool, you must return the api information within the json):

            Example 1: Get system overview
            ```json
            
            api base info detail
            
            ```
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            String apiUrl = inputJson.has("url") ? inputJson.get("url").getAsString() : null;
            String apiDetail = getApiDetail("apiType", apiUrl);
            result.addProperty("apiBaseInfo", apiDetail);
            return result;
        } catch (Exception e) {
            result.addProperty("error", "获取接口信息失败: " + e.getMessage());
            return result;
        }
    }

    private String getApiDetail(String type, String url) {
        if (StringUtils.isEmpty(type) || url == null || StringUtils.isEmpty(url)) {
            return "error: Parameters cannot be empty";
        }
        JsonObject requestParams = new JsonObject();
        requestParams.add("path", new JsonPrimitive(url));
        if (type.equalsIgnoreCase("3")) {
            requestParams.add("protocol", new JsonPrimitive(3));
        } else {
            requestParams.add("protocol", new JsonPrimitive(1));
        }
        return getApiDetailMiApi(requestParams);
    }

    private JsonElement getRequestType(Integer type) {
        String prefix = "post";
        switch (type){
            case 0:
                prefix = "post";
                break;
            case 2:
                prefix = "put";
                break;
            case 3:
                prefix = "delete";
                break;
            case 4:
                prefix = "head";
                break;
            case 5:
                prefix = "opts";
                break;
            case 6:
                prefix = "patch";
                break;
            default:
                prefix = "get";
                break;
        }
        return new JsonPrimitive(prefix);
    }

    private String getApiDetailMiApi(JsonObject reqParamsBo) {
        String result = "";
        try {
            JsonObject detail = httpClient.post(host + "/OpenApi/mcp/detail", reqParamsBo);
            JsonObject dataObject = detail.getAsJsonObject("data");
            JsonArray headerInfoArray = dataObject.getAsJsonArray("headerInfo");
            JsonObject baseInfoObject = dataObject.getAsJsonObject("baseInfo");

            JsonObject apiInfo = new JsonObject();
            apiInfo.add("headers", headerInfoArray);
            apiInfo.add("url", baseInfoObject.get("apiURI"));
//            apiInfo.add("apiRequestRaw", baseInfoObject.get("apiRequestRaw"));
            apiInfo.add("method", getRequestType(baseInfoObject.get("apiRequestType").getAsInt()));
            result = gson.toJson(apiInfo);
        } catch (IOException e) {
            result = "error: " + e.getMessage();
        }
        return result;
    }
}
