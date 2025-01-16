package run.mone.m78.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.m78.gateway.bo.*;
import run.mone.m78.server.ws.KnowledgeConsumer;
import run.mone.m78.service.common.GsonUtils;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("kbs")

/**
 * KBSKnowledgeService类是一个实现IKnowledgeService接口的服务类，
 * 主要负责与知识库系统（KBS）进行交互，处理知识查询请求。
 *
 * 该类通过注入的配置参数（如KBS地址、应用ID、应用密钥等）来配置与KBS的连接，
 * 并使用Gson库进行JSON数据的处理。
 *
 * 主要功能包括：
 * - 接收查询请求并构建KBS请求对象。
 * - 调用KBS接口并处理返回的事件流数据。
 * - 解析KBS返回的数据并将结果传递给相应的消费者。
 *
 * 该类使用了OkHttpClient进行HTTP请求，并通过EventSourceListener监听KBS返回的事件流。
 *
 * 注：该类使用了@Slf4j注解进行日志记录。
 */

public class KBSKnowledgeService implements IKnowledgeService {

    @Value("${knowledge.kbs.address}")
    private String kbsUrl;

    @Value("${knowledge.kbs.appId}")
    private String appId;

    @Value("${knowledge.kbs.appSecret}")
    private String appSecret;

    @Value("${knowledge.kbs.link}")
    private String link;

    private final Gson gson = new Gson();


    /**
     * 查询知识库
     *
     * @param requestBO 请求对象，包含查询信息和用户历史记录
     * @param sessionId 会话ID，用于标识当前会话
     */

    @Override
    public void queryKnowledge(RequestBO requestBO, String sessionId) {

        KBSRequestBO kbsRequestBO = new KBSRequestBO();
        kbsRequestBO.setQuery(requestBO.getQuery());
        kbsRequestBO.setRegion("16");
        kbsRequestBO.setUserId(requestBO.getUserId());
        List<RecordBO> history = requestBO.getHistory() == null ? new ArrayList<>() : requestBO.getHistory();
        kbsRequestBO.setHistory(history.size() > 100 ? history.subList(0, 100) : history);
        String query = x5(gson.toJson(kbsRequestBO));
        callKBS(query, sessionId, requestBO.getRequestId());
    }

    private void callKBS(String req, String sessionId, String requestId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .build();

        MediaType mediaType = MediaType.parse("text/event-stream");
        RequestBody body = RequestBody.create(mediaType, req);
        Request request = new Request.Builder()
                .url(kbsUrl)
                .method("POST", body)
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "text/event-stream")
                .addHeader("accept", "text/event-stream")
                .build();

        KnowledgeConsumer knowledgeConsumer = new KnowledgeConsumer();

        StringBuffer sb = new StringBuffer();

        List<RelationBO> relations = new ArrayList<>();

        final boolean[] hasEnd = {false};

        EventSourceListener listener = new EventSourceListener() {

            @Override
            public void onOpen(EventSource eventSource, Response response) {
                log.info("onOpen");
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if (hasEnd[0]) {
                    return;
                }
                JsonObject raw = GsonUtils.gson.fromJson(data, JsonObject.class);
                sb.append(raw.get("answer").getAsString());
                raw.addProperty("answer", sb.toString());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("sessionId", sessionId);
                ResponseBO responseBO = new ResponseBO();
                responseBO.setCode(200);
                responseBO.setMsg("success");
                responseBO.setOriginalAction("query");
                responseBO.setGatewayType("kbs");
                responseBO.setRequestId(requestId);
                ResponseBodyBO responseBodyBO = new ResponseBodyBO();
                if (raw.get("hasDocs") != null && raw.get("hasDocs").getAsBoolean() && raw.get("docs") != null && !raw.get("docs").isJsonNull()) {
                    JsonArray docs = raw.get("docs").getAsJsonArray();
                    if (docs != null && !docs.isEmpty()) {
                        for (JsonElement doc : docs) {
                            RelationBO relationBO = new RelationBO();
                            relationBO.setDocName(((JsonObject) doc).get("name").getAsString());
                            relationBO.setSource("");
                            relationBO.setParts(List.of(PartBO.builder().title(((JsonObject) doc).get("name").getAsString()).answer("").contentLink(String.format(link, ((JsonObject) doc).get("sourceId").getAsString())).build()));
                            relations.add(relationBO);
                        }
                    }
                }
                if (raw.get("streamEnd").getAsBoolean()) {
                    responseBodyBO.setStatus(200);
                    responseBodyBO.setRelations(relations);
                    hasEnd[0] = true;
                } else {
                    responseBodyBO.setStatus(100);
                }
                responseBodyBO.setAnswer(sb.toString());
                responseBodyBO.setMarkdownAnswer(sb.toString());

                responseBO.setData(responseBodyBO);
                jsonObject.addProperty("msg", GsonUtils.gson.toJson(responseBO));
                knowledgeConsumer.accept(jsonObject);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("onClosed");
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                log.error("kbs onFailure,", t);
            }
        };

        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, listener);
    }

    private String x5(String query) {
        Map<String, Object> header = new HashMap<>();
        header.put("appid", appId);
        header.put("method", "searchCarStream");
        header.put("apitype", 1);

        String signSb = appId + query + appSecret;
        String sign = DigestUtils.md5Hex(signSb).toUpperCase();

        header.put("sign", sign);
        header.put("key", null);

        Map<String, Object> dataNode = new HashMap<>();
        dataNode.put("header", header);
        dataNode.put("body", query);

        String dataStr = GsonUtils.gson.toJson(dataNode);
        String base64Str = Base64.encodeBase64String(dataStr.getBytes());

        return base64Str;
    }


}
