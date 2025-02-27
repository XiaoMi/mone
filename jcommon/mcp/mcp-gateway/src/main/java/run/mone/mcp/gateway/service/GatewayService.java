
package run.mone.mcp.gateway.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;
import run.mone.mcp.gateway.http.HttpClient;
import run.mone.mcp.gateway.service.bo.ListApiInfoParam;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Service
public class GatewayService {

    private static final Gson gson = new Gson();

    @Autowired
    private LLM llm;

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