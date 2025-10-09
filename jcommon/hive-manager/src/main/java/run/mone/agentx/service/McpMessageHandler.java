package run.mone.agentx.service;

import lombok.extern.slf4j.Slf4j;
import run.mone.agentx.websocket.WebSocketHolder;

import java.util.Map;

/**
 * MCP消息处理器
 * 负责处理从MCP Hub接收到的消息并转发到前端WebSocket
 */
@Slf4j
public class McpMessageHandler {

    /**
     * 处理接收到的消息
     * 
     * @param msg 接收到的消息对象
     */
    public static void handleMessage(Object msg) {
        log.info("msg:{}", msg);
        
        if (msg instanceof Map m) {
            Object id = m.get("clientId");
            if (null != id && m.getOrDefault("cmd", "").equals("notify_hive_manager")) {
                if (WebSocketHolder.getSession(id.toString()) != null) {
                    //发到前端页面(必须用websocket连接过来的)
                    WebSocketHolder.sendMessageSafely(
                        WebSocketHolder.getSession(id.toString()), 
                        m.getOrDefault("data", "").toString()
                    );
                }
            }
        }
    }
}
