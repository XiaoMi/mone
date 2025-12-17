package run.mone.hive.spring.starter;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * WebSocket 同步调用器（单例）
 * 用于向客户端发送请求并同步等待响应
 *
 * @author goodjava@qq.com
 */
@Slf4j
public class WebSocketCaller {

    private static final WebSocketCaller INSTANCE = new WebSocketCaller();

    // 存储等待响应的请求，key 为 reqId
    private final ConcurrentHashMap<String, CompletableFuture<Map<String, Object>>> pendingRequests = new ConcurrentHashMap<>();

    // 默认超时时间（秒）
    private static final long DEFAULT_TIMEOUT_SECONDS = 30;

    private WebSocketCaller() {
    }

    public static WebSocketCaller getInstance() {
        return INSTANCE;
    }

    /**
     * 同步调用客户端，阻塞等待响应
     *
     * @param clientId 客户端ID
     * @param action   调用的动作/方法名
     * @param data     请求数据
     * @return 响应数据
     * @throws TimeoutException 超时异常
     */
    public Map<String, Object> call(String clientId, String action, Map<String, Object> data) throws TimeoutException {
        return call(clientId, action, data, DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 同步调用客户端，阻塞等待响应（可指定超时时间）
     *
     * @param clientId 客户端ID
     * @param action   调用的动作/方法名
     * @param data     请求数据
     * @param timeout  超时时间
     * @param unit     时间单位
     * @return 响应数据
     * @throws TimeoutException 超时异常
     */
    public Map<String, Object> call(String clientId, String action, Map<String, Object> data, long timeout, TimeUnit unit) throws TimeoutException {
        // 生成唯一请求ID
        String reqId = generateReqId();

        // 创建 CompletableFuture 用于等待响应
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        pendingRequests.put(reqId, future);

        try {
            // 构造请求消息
            Map<String, Object> request = Map.of(
                    "type", "call",
                    "reqId", reqId,
                    "action", action,
                    "data", data != null ? data : Map.of(),
                    "timestamp", System.currentTimeMillis()
            );

            log.info("Sending call request to client {}, reqId: {}, action: {}", clientId, reqId, action);

            // 发送请求到客户端
            WebSocketSessionManager.getInstance().sendMessage(clientId, request);

            // 阻塞等待响应
            Map<String, Object> response = future.get(timeout, unit);
            log.info("Received response for reqId: {}", reqId);
            return response;

        } catch (TimeoutException e) {
            log.error("Call timeout for client {}, reqId: {}, action: {}", clientId, reqId, action);
            throw e;
        } catch (InterruptedException e) {
            log.error("Call interrupted for client {}, reqId: {}", clientId, reqId);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Call interrupted", e);
        } catch (ExecutionException e) {
            log.error("Call execution error for client {}, reqId: {}", clientId, reqId, e);
            throw new RuntimeException("Call execution error", e.getCause());
        } finally {
            // 清理等待的请求
            pendingRequests.remove(reqId);
        }
    }

    /**
     * 同步调用 Android 客户端，阻塞等待响应
     * 与 call 方法不同，此方法将 action 和 params 放在消息根级别
     *
     * 发送的消息格式:
     * {
     *   "type": "call",
     *   "reqId": "xxx",
     *   "action": "click",
     *   "params": { "x": 500, "y": 800 },
     *   "timestamp": 1234567890
     * }
     *
     * @param clientId 客户端ID
     * @param action   操作类型 (click, type, scroll, etc.)
     * @param params   操作参数
     * @return 响应数据
     * @throws TimeoutException 超时异常
     */
    public Map<String, Object> callAndroid(String clientId, String action, Map<String, Object> params) throws TimeoutException {
        return callAndroid(clientId, action, params, DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 同步调用 Android 客户端，阻塞等待响应（可指定超时时间）
     * 与 call 方法不同，此方法将 action 和 params 放在消息根级别
     *
     * @param clientId 客户端ID
     * @param action   操作类型 (click, type, scroll, etc.)
     * @param params   操作参数
     * @param timeout  超时时间
     * @param unit     时间单位
     * @return 响应数据
     * @throws TimeoutException 超时异常
     */
    public Map<String, Object> callAndroid(String clientId, String action, Map<String, Object> params, long timeout, TimeUnit unit) throws TimeoutException {
        // 生成唯一请求ID
        String reqId = generateReqId();

        // 创建 CompletableFuture 用于等待响应
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        pendingRequests.put(reqId, future);

        try {
            // 构造请求消息 - Android 客户端期望 action 和 params 在根级别
            java.util.HashMap<String, Object> request = new java.util.HashMap<>();
            request.put("type", "call");
            request.put("reqId", reqId);
            request.put("action", action);
            if (params != null && !params.isEmpty()) {
                request.put("params", params);
            }
            request.put("timestamp", System.currentTimeMillis());

            log.info("Sending Android call request to client {}, reqId: {}, action: {}", clientId, reqId, action);

            // 发送请求到客户端
            WebSocketSessionManager.getInstance().sendMessage(clientId, request);

            // 阻塞等待响应
            Map<String, Object> response = future.get(timeout, unit);
            log.info("Received Android response for reqId: {}", reqId);
            return response;

        } catch (TimeoutException e) {
            log.error("Android call timeout for client {}, reqId: {}, action: {}", clientId, reqId, action);
            throw e;
        } catch (InterruptedException e) {
            log.error("Android call interrupted for client {}, reqId: {}", clientId, reqId);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Android call interrupted", e);
        } catch (ExecutionException e) {
            log.error("Android call execution error for client {}, reqId: {}", clientId, reqId, e);
            throw new RuntimeException("Android call execution error", e.getCause());
        } finally {
            // 清理等待的请求
            pendingRequests.remove(reqId);
        }
    }

    /**
     * 异步调用客户端，返回 CompletableFuture
     *
     * @param clientId 客户端ID
     * @param action   调用的动作/方法名
     * @param data     请求数据
     * @return CompletableFuture 响应
     */
    public CompletableFuture<Map<String, Object>> callAsync(String clientId, String action, Map<String, Object> data) {
        // 生成唯一请求ID
        String reqId = generateReqId();

        // 创建 CompletableFuture 用于等待响应
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        pendingRequests.put(reqId, future);

        // 构造请求消息
        Map<String, Object> request = Map.of(
                "type", "call",
                "reqId", reqId,
                "action", action,
                "data", data != null ? data : Map.of(),
                "timestamp", System.currentTimeMillis()
        );

        log.info("Sending async call request to client {}, reqId: {}, action: {}", clientId, reqId, action);

        // 发送请求到客户端
        WebSocketSessionManager.getInstance().sendMessage(clientId, request);

        // 设置超时自动清理
        future.orTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .whenComplete((result, throwable) -> {
                    pendingRequests.remove(reqId);
                    if (throwable != null) {
                        log.error("Async call failed for reqId: {}", reqId, throwable);
                    }
                });

        return future;
    }

    /**
     * 注册等待响应的请求（由外部调用者使用）
     *
     * @param reqId  请求ID
     * @param future 用于等待响应的 CompletableFuture
     */
    public void registerPendingRequest(String reqId, CompletableFuture<Map<String, Object>> future) {
        pendingRequests.put(reqId, future);
        log.debug("Registered pending request: {}", reqId);
    }

    /**
     * 移除等待响应的请求（由外部调用者使用）
     *
     * @param reqId 请求ID
     */
    public void removePendingRequest(String reqId) {
        pendingRequests.remove(reqId);
        log.debug("Removed pending request: {}", reqId);
    }

    /**
     * 处理客户端响应（由 WebSocketHandler 调用）
     *
     * @param resId    响应ID（对应请求的 reqId）
     * @param response 响应数据
     */
    public void handleResponse(String resId, Map<String, Object> response) {
        CompletableFuture<Map<String, Object>> future = pendingRequests.get(resId);
        if (future != null) {
            log.info("Completing request for resId: {}", resId);
            future.complete(response);
        } else {
            log.warn("No pending request found for resId: {}", resId);
        }
    }

    /**
     * 处理客户端错误响应（由 WebSocketHandler 调用）
     *
     * @param resId        响应ID（对应请求的 reqId）
     * @param errorMessage 错误信息
     */
    public void handleError(String resId, String errorMessage) {
        CompletableFuture<Map<String, Object>> future = pendingRequests.get(resId);
        if (future != null) {
            log.error("Request failed for resId: {}, error: {}", resId, errorMessage);
            future.completeExceptionally(new RuntimeException(errorMessage));
        } else {
            log.warn("No pending request found for error resId: {}", resId);
        }
    }

    /**
     * 生成唯一请求ID
     */
    private String generateReqId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取当前等待响应的请求数
     */
    public int getPendingRequestCount() {
        return pendingRequests.size();
    }

    /**
     * 取消所有等待的请求
     */
    public void cancelAllPendingRequests() {
        log.info("Cancelling {} pending requests", pendingRequests.size());
        pendingRequests.forEach((reqId, future) -> {
            future.cancel(true);
        });
        pendingRequests.clear();
    }
}