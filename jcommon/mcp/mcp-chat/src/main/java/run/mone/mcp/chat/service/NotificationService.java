package run.mone.mcp.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.server.transport.streamable.HttpServletStreamableServerTransport;
import run.mone.hive.mcp.spec.McpSchema;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending notifications to MCP clients.
 *
 * This service uses the HttpServletStreamableServerTransport to send
 * JSON-RPC notifications through the SSE listening stream established
 * when clients connect via GET requests.
 *
 * @author goodjava@qq.com
 * @date 2025/12/08
 */
@Slf4j
@Service
public class NotificationService {

    @Resource
    private HttpServletStreamableServerTransport transport;

    /**
     * Send a notification to a specific client.
     *
     * The notification will only be sent if the client has an active
     * listening stream (established via GET SSE connection).
     *
     * @param clientId The client ID to send the notification to
     * @param method The JSON-RPC method name
     * @param params The notification parameters
     */
    public void sendNotification(String clientId, String method, Map<String, Object> params) {
        try {
            // Ensure CLIENT_ID is in params for targeted delivery
            if (clientId != null && !clientId.isEmpty()) {
                params.put(Const.CLIENT_ID, clientId);
            }

            McpSchema.JSONRPCNotification notification = new McpSchema.JSONRPCNotification(
                McpSchema.JSONRPC_VERSION,
                method,
                params
            );

            log.info("Sending notification to client: {}, method: {}, params: {}",
                    clientId, method, params);

            // Send through the transport (will use listeningStreamRef)
            transport.sendMessage(notification).block();

            log.debug("Notification sent successfully to client: {}", clientId);
        } catch (Exception e) {
            log.error("Failed to send notification to client: {}, error: {}",
                    clientId, e.getMessage(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    /**
     * Send a simple message notification to a client.
     * Convenience method that wraps the message in standard format.
     *
     * @param clientId The client ID
     * @param message The message content
     */
    public void sendMessage(String clientId, String message) {
        Map<String, Object> params = new HashMap<>();
        params.put("message", message);
        params.put("timestamp", System.currentTimeMillis());

        sendNotification(clientId, "notification/message", params);
    }

    /**
     * Send a notification to Athena (similar to ChatAgentTask implementation).
     *
     * @param clientId The client ID
     * @param data The data to send
     * @param messageId The message ID
     * @param ownerId The owner ID
     */
    public void sendNotificationToAthena(String clientId, String data, String messageId, String ownerId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.OWNER_ID, ownerId);
        params.put("cmd", "notify_athena");
        params.put("data", data);
        params.put("id", messageId);

        sendNotification(clientId, "msg", params);
    }

    /**
     * Broadcast a notification to all connected clients.
     * Only clients with active listening streams will receive the notification.
     *
     * @param method The JSON-RPC method name
     * @param params The notification parameters (should NOT include CLIENT_ID for broadcast)
     */
    public void broadcastNotification(String method, Map<String, Object> params) {
        try {
            // Don't set CLIENT_ID to broadcast to all active sessions
            McpSchema.JSONRPCNotification notification = new McpSchema.JSONRPCNotification(
                McpSchema.JSONRPC_VERSION,
                method,
                params
            );

            log.info("Broadcasting notification, method: {}, params: {}", method, params);

            transport.sendMessage(notification).block();

            log.debug("Broadcast notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to broadcast notification, error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to broadcast notification", e);
        }
    }

    /**
     * Send a custom event notification.
     *
     * @param clientId The client ID
     * @param eventType The event type
     * @param eventData The event data
     */
    public void sendEvent(String clientId, String eventType, Object eventData) {
        Map<String, Object> params = new HashMap<>();
        params.put("eventType", eventType);
        params.put("eventData", eventData);
        params.put("timestamp", System.currentTimeMillis());

        sendNotification(clientId, "notification/event", params);
    }

    /**
     * Get the number of active sessions with listening streams.
     *
     * @return The number of active sessions
     */
    public int getActiveSessionCount() {
        return transport.getActiveSessionCount();
    }
}
