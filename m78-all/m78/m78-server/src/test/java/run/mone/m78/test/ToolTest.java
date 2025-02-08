package run.mone.m78.test;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.mybatisflex.core.row.Row;
import com.xiaomi.data.push.client.HttpClientV6;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;
import run.mone.m78.service.common.GsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

import static run.mone.m78.service.common.M78StringUtils.stripStr;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/20/24 15:10
 */
@Slf4j
public class ToolTest {

    @Test
    public void testStreamFilter() {
        List<Row> rows = new ArrayList<>();
        rows.add(null);
        List<HashMap<String, Object>> collect = rows.stream()
                .filter(row -> row != null && !row.isEmpty())
                .map(HashMap::new)
                .collect(Collectors.toList());
        System.out.println(collect);
    }

    public static final String GET_APIS_BY_PROJECT_ID_URL= "http://127.0.0.1/mtop/miapi/getApiDetail";

    @Test
    public void getApisByProjectId() {
        Result result = new Result();
        int projectId = 123467;
        int pageNum = 1;
        int pageSize = 1000;
        log.info("getApisByProjectId: {}", projectId);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json;charset=UTF-8");
            List<Integer> req = new ArrayList<>();
            req.add(projectId);
            req.add(pageNum);
            req.add(pageSize);
            String data = HttpClientV6.post(GET_APIS_BY_PROJECT_ID_URL, GsonUtils.gson.toJson(req), headers, 120000);
            JsonElement dataElement = JsonParser.parseString(data);
            String resData = "";
            if (dataElement != null
                    && dataElement.getAsJsonObject() != null
                    && dataElement.getAsJsonObject().get("data") != null) {
                resData = processData(GsonUtils.gson.fromJson(dataElement.getAsJsonObject().get("data"),  new TypeToken<List<Map<String, Object>>>() {}.getType()));
                log.info("resData:{}", resData);
            }
            result.setCode(200);
            result.setData(resData);
            result.setMessage("ok");
            GsonUtils.gson.toJsonTree(result).getAsJsonObject();
            return;
        } catch (JsonSyntaxException e) {
            log.error("get miapi list error: ", e);
            result.setCode(500);
            result.setMessage("miapi list error");
            GsonUtils.gson.toJsonTree(result).getAsJsonObject();
            return;
        }
    }

    private static JsonObject getApiRequestRaw(JsonObject baseInfo, String infoStr) {
        if (StringUtils.isBlank(infoStr)
                || StringUtils.isBlank(baseInfo.get(infoStr).getAsString())
                || StringUtils.isBlank(stripStr(baseInfo.get(infoStr).getAsString(), "\""))) {
            return new JsonObject();
        }
        try {
            return JsonParser.parseString(baseInfo.get(infoStr).getAsString()).getAsJsonObject();
        } catch (Throwable e) {
            return new JsonObject();
        }
    }

    private String getHttpMethodFromCode(JsonObject baseInfo) {
        JsonElement apiRequestType = baseInfo.get("apiRequestType");
        if (apiRequestType == null) {
            return "";
        }
        int code = apiRequestType.getAsInt();
        switch (code) {
            case 0:
                return "POST";
            case 1:
                return "GET";
            case 2:
                return "PUT";
            case 3:
                return "DELETE";
            case 4:
                return "HEAD";
            case 5:
                return "OPTIONS";
            case 6:
                return "PATCH";
            default:
                return "";
        }
    }

    private String processData(List<Map<String, Object>> data) {
        if (CollectionUtils.isEmpty(data)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        List<MiApiInfo> list = data.stream()
                .filter(Objects::nonNull)
                .map(d -> {
                    JsonElement apiJson = GsonUtils.gson.toJsonTree(d);
                    if (apiJson == null) {
                        return null;
                    }
                    JsonObject apiInfo = apiJson.getAsJsonObject();

                    JsonElement baseJson = apiInfo.get("baseInfo");
                    if (baseJson == null) {
                        return null;
                    }
                    JsonObject baseInfo = baseJson.getAsJsonObject();

                    JsonArray headerInfo = null;
                    if (apiInfo.get("headerInfo") != null) {
                        headerInfo = apiInfo.get("headerInfo").getAsJsonArray();
                    }
                    return MiApiInfo.builder()
                            .apiUrl(baseInfo.get("apiURI").getAsString())
                            .apiName(baseInfo.get("apiName").getAsString())
                            .apiDesc(baseInfo.get("apiDesc").getAsString())
                            .apiRequestRaw(getApiRequestRaw(baseInfo, "apiRequestRaw"))
                            .apiResponseRaw(getApiRequestRaw(baseInfo, "apiResponseRaw"))
                            .httpMethod(getHttpMethodFromCode(baseInfo))
                            .headerInfo(headerInfo)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            MiApiInfo miApiInfo = list.get(i);
            JsonArray h = miApiInfo.getHeaderInfo();
            JsonObject req = miApiInfo.getApiRequestRaw();
            JsonObject res = miApiInfo.getApiResponseRaw();
            sb.append(i + 1).append(". ").append(miApiInfo.getApiName()).append("\n")
                    .append("描述：").append(miApiInfo.getApiDesc()).append("\n")
                    .append("url：").append(miApiInfo.getApiUrl()).append("\n")
                    .append("接口参数样例：").append(req == null ? "没有" : "\n" + GsonUtils.gson.toJson(req)).append("\n")
                    .append("接口返回值样例：").append(res == null ? "没有" : "\n" + GsonUtils.gson.toJson(res)).append("\n")
                    .append("请求方法：").append(miApiInfo.getHttpMethod()).append("\n")
                    .append("额外header信息：").append((h == null || h.size() < 1) ? "没有" : GsonUtils.gson.toJson(h.get(0))).append("\n");
        }
        return sb.toString();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class MiApiInfo {
        private String apiUrl;
        private String apiName;
        private String apiDesc;
        private JsonObject apiRequestRaw;
        private JsonObject apiResponseRaw;
        private String httpMethod;
        private JsonArray headerInfo;
    }


    @Test
    public void testMurmurHash() {
        HashCode hasCode = Hashing.murmur3_32_fixed().hashString("dingtao12", StandardCharsets.UTF_8);
        int agentIndex = Hashing.consistentHash(hasCode, 2);
        System.out.println(agentIndex);
    }
}
