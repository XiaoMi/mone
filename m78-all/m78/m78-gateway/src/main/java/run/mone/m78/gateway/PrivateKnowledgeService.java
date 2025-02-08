package run.mone.m78.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.knowledge.api.dto.KnowledgeReq;
import run.mone.m78.gateway.bo.*;
import run.mone.m78.server.ws.KnowledgeConsumer;


@Slf4j
@Service("knowledge")

/**
 * PrivateKnowledgeService类是一个服务类，实现了IKnowledgeService接口。
 * 该类主要负责处理与私有知识库相关的查询操作。
 *
 * 主要功能包括：
 * - 通过WebSocket连接私有知识库接口，发送查询请求并处理响应。
 *
 * 类中包含以下成员变量：
 * - CAR_CS_KNOWLEDGE_BASE_ID: 知识库的ID常量。
 * - privateKnowledgeUrl: 私有知识库的URL地址，从配置文件中读取。
 * - gson: 用于JSON序列化和反序列化的Gson实例。
 *
 * 主要方法：
 * - queryKnowledge: 接收请求对象和会话ID，通过WebSocket发送查询请求到私有知识库。
 *
 * 该类使用了@Slf4j注解进行日志记录，并通过@Service注解标识为Spring服务组件。
 */

public class PrivateKnowledgeService implements IKnowledgeService {

    public static final Long CAR_CS_KNOWLEDGE_BASE_ID = 20115L;

    @Value("${knowledge.private.address}")
    private String privateKnowledgeUrl;

    private final Gson gson = new Gson();

    /**
     * 查询知识库信息
     *
     * @param requestBO 请求对象，包含用户信息和查询内容
     * @param sessionId 会话ID，用于标识用户会话
     * @return 无返回值
     */
    @Override
    public void queryKnowledge(RequestBO requestBO, String sessionId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(privateKnowledgeUrl).build();
        WebSocketListener listener = new PrivateKnowledgeWebSocketListener(sessionId, new KnowledgeConsumer());
        WebSocket webSocket = client.newWebSocket(request, listener);
        // 调用private-knowledge接口
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("limit", 3);
        jsonObject.addProperty("knowledgeBaseId", CAR_CS_KNOWLEDGE_BASE_ID);
        jsonObject.addProperty("userName", requestBO.getUserName());
        jsonObject.addProperty("queryText", requestBO.getQuery());
        jsonObject.addProperty("requestId", requestBO.getRequestId());
        jsonObject.addProperty("history", gson.toJson(requestBO.getHistory()));
        webSocket.send(gson.toJson(jsonObject));

        // client.dispatcher().executorService().shutdown();
    }

}
