
package run.mone.mcp.gateway.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import run.mone.mcp.gateway.http.HttpClient;
import run.mone.mcp.gateway.service.bo.ListApiInfoParam;

import java.lang.reflect.Type;
import java.util.Map;

@Service
public class GatewayService {

    private static final Gson gson = new Gson();

    private Map<String, String> urlConfig;

    @PostConstruct
    public void init() {
        String urlJson = System.getenv("GATEWAY_URL");
        if (StringUtils.isNotEmpty(urlJson)) {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            urlConfig = gson.fromJson(urlJson, type);
        }
    }

    private static HttpClient httpClient = new HttpClient();

    public String detailByUrl(String env, String url) {
        String path = "/open/v1/private/api/apiinfo/detailByUrl?url=" + url;
        String host = urlConfig.get(env);
        try {
            JsonObject jsonObject = httpClient.get(host + path);
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    public String listApiInfo(String env, ListApiInfoParam param) {
        String path = "/open/v1/private/api/apiinfo/list";
        String host = urlConfig.get(env);
        try {
            JsonObject jsonObject = httpClient.post(host + path, gson.toJson(param));
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }


}