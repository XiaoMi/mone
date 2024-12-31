package run.mone.knowledge.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.knowledge.service.exceptions.ExCodes;

import java.util.*;

/**
 * @author wmin
 * @date 2024/2/6
 */
@Service
@Slf4j
public class EmbeddingService {

    public static Gson gson = new Gson();

    @Value("${token.server}")
    private String tokenServer;

    public String getEmbeddingStr(String input) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("content-type", "application/json");
            Map<String, String> post = new HashMap<>();
            post.put("data", input);
            return HttpClientV2.post(String.format("%s/embedding", tokenServer), gson.toJson(post), headers, 100000);
        } catch (Exception e) {
            log.error("getEmbedding error {}", e);
            return null;
        }
    }

    public double[] getEmbeddingArr(String input) {
        try {
            String inputEmbedding = getEmbeddingStr(input);
            if (StringUtils.isBlank(inputEmbedding)){
                log.error("http call error");
                return null;
            }
            return gson.fromJson(inputEmbedding, new TypeToken<double[]>() {}.getType());
        } catch (Exception e) {
            log.error("getEmbedding error {}", e);
            return null;
        }
    }

}
