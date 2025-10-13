package run.mone.agentx.service;

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import run.mone.agentx.websocket.WebSocketHolder;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.grpc.StreamRequest;
import run.mone.hive.mcp.grpc.StreamResponse;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * MCP消息处理器
 * 负责处理从MCP Hub接收到的消息并转发到前端WebSocket
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class McpMessageHandler {


    private final AgentConfigService agentConfigService;


    /**
     * 处理接收到的消息,其实这边是client,利用了一条建立好的双向流连接
     * observer 用来回传信息
     * @param msg 接收到的消息对象
     */
    public void handleMessage(Object msg) {
        log.info("msg:{}", msg);
        if (msg instanceof Pair<?, ?> pair
                && pair.getKey() instanceof StreamResponse response
                && pair.getValue() instanceof StreamObserver) {

            @SuppressWarnings("unchecked")
            StreamObserver<StreamRequest> observer =
                    (StreamObserver<StreamRequest>) pair.getValue();

            String reqId = response.getRequestId();

            if (response.getCmd().equals(Const.NOTIFY_MSG)) {
                String data = response.getData();
                Type typeOfT = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> m = GsonUtils.gson.fromJson(data, typeOfT);
                Object id = m.get("clientId");
                String cmd = m.getOrDefault("cmd","").toString();

                //通知过来信息,需要发送到用户的界面
                if (null != id && cmd.equals("notify_hive_manager")) {
                    if (WebSocketHolder.getSession(id.toString()) != null) {
                        //发到前端页面(必须用websocket连接过来的)
                        WebSocketHolder.sendMessageSafely(
                                WebSocketHolder.getSession(id.toString()),
                                m.getOrDefault("data", "").toString()
                        );
                    }
                }

                //获取配置
                if (cmd.equals("getConfig")) {
                    Long agentId = Long.valueOf(Long.parseLong(m.get("agetnId").toString()));
                    Long userId = Long.valueOf(Long.parseLong(m.get("userId").toString()));
                    Map<String, String> map = agentConfigService.getUserConfigsAsMap(agentId, userId).block();
                    String jsonData = GsonUtils.gson.toJson(map);
                    observer.onNext(StreamRequest.newBuilder().setJsonData(jsonData).putAllData(ImmutableMap.of(Const.REPLY,"")).setRequestId(reqId).setName("getConfig").build());
                }

                //测试用
                if (cmd.equals("ping")) {
                    observer.onNext(StreamRequest.newBuilder().setJsonData("PONG^").putAllData(ImmutableMap.of(Const.REPLY,"")).setRequestId(reqId).setName("ping").build());
                }

                return;
            }








        }

       
    }
}
