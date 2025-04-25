package run.mone.hive.a2a.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import run.mone.hive.a2a.types.TaskEvents.TaskArtifactUpdateEvent;
import run.mone.hive.a2a.types.TaskEvents.TaskStatusUpdateEvent;
import run.mone.hive.a2a.types.TaskParams.TaskPushNotificationConfig;

import java.util.Map;
import java.util.UUID;

public class JsonRpcTypes {
    /**
     * 表示JSON-RPC消息的基类
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JsonRpcMessage(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id
    ) {
        public JsonRpcMessage() {
            this("2.0", UUID.randomUUID().toString().replace("-", ""));
        }
    }

    /**
     * 表示JSON-RPC请求
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "method", visible = true)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SendTaskRequest.class, name = "tasks/send"),
            @JsonSubTypes.Type(value = GetTaskRequest.class, name = "tasks/get"),
            @JsonSubTypes.Type(value = CancelTaskRequest.class, name = "tasks/cancel"),
            @JsonSubTypes.Type(value = SetTaskPushNotificationRequest.class, name = "tasks/pushNotification/set"),
            @JsonSubTypes.Type(value = GetTaskPushNotificationRequest.class, name = "tasks/pushNotification/get"),
            @JsonSubTypes.Type(value = TaskResubscriptionRequest.class, name = "tasks/resubscribe"),
            @JsonSubTypes.Type(value = SendTaskStreamingRequest.class, name = "tasks/sendSubscribe")
    })
    public record JsonRpcRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("method") String method,
        @JsonProperty("params") Object params
    ) {
        public JsonRpcRequest(String method, Object params) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), method, params);
        }
        
        public JsonRpcRequest(String method) {
            this(method, null);
        }
    }

    /**
     * 表示JSON-RPC错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JsonRpcError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public JsonRpcError() {
            this(0, null, null);
        }
    }

    /**
     * 表示JSON-RPC响应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JsonRpcResponse(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("result") Object result,
        @JsonProperty("error") JsonRpcError error
    ) {
        public JsonRpcResponse() {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), null, null);
        }

        public JsonRpcResponse(String id, JsonRpcError error) {
            this("2.0", id, null, error);
        }
        
        public JsonRpcResponse(Object result) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), result, null);
        }
        
        public JsonRpcResponse(JsonRpcError error) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), null, error);
        }
    }

    /**
     * 发送任务请求
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SendTaskRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("method") String method,
        @JsonProperty("params") Object params
    ) {
        public SendTaskRequest(Object params) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), "tasks/send", params);
        }
        
        public SendTaskRequest() {
            this(null);
        }
    }

    /**
     * 发送任务响应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SendTaskResponse(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("result") Task result,
        @JsonProperty("error") JsonRpcError error
    ) {
        public SendTaskResponse(Task result) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), result, null);
        }
        
        public SendTaskResponse() {
            this(null);
        }
    }

    /**
     * 流式发送任务请求
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SendTaskStreamingRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("method") String method,
        @JsonProperty("params") Object params
    ) {
        public SendTaskStreamingRequest(Object params) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), "tasks/sendSubscribe", params);
        }
        
        public SendTaskStreamingRequest() {
            this(null);
        }
    }

    /**
     * 流式发送任务响应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SendTaskStreamingResponse(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("result") Object result,
        @JsonProperty("error") JsonRpcError error
    ) {
        public SendTaskStreamingResponse(Object result) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), result, null);
        }
        
        public SendTaskStreamingResponse() {
            this(null);
        }
        
        public TaskStatusUpdateEvent getStatusResult() {
            return (result instanceof TaskStatusUpdateEvent) ? (TaskStatusUpdateEvent) result : null;
        }
        
        public TaskArtifactUpdateEvent getArtifactResult() {
            return (result instanceof TaskEvents.TaskArtifactUpdateEvent) ? (TaskEvents.TaskArtifactUpdateEvent) result : null;
        }
    }

    /**
     * 获取任务请求
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GetTaskRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("method") String method,
        @JsonProperty("params") Object params
    ) {
        public GetTaskRequest(Object params) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), "tasks/get", params);
        }
        
        public GetTaskRequest() {
            this(null);
        }
    }

    /**
     * 获取任务响应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GetTaskResponse(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("result") Task result,
        @JsonProperty("error") JsonRpcError error
    ) {
        public GetTaskResponse(String id, Task result, JsonRpcError error) {
            this("2.0", id, result, error);
        }

        public GetTaskResponse(Task result) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), result, null);
        }
        
        public GetTaskResponse() {
            this(null);
        }
    }

    /**
     * 取消任务请求
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CancelTaskRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("method") String method,
        @JsonProperty("params") Object params
    ) {
        public CancelTaskRequest(Object params) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), "tasks/cancel", params);
        }
        
        public CancelTaskRequest() {
            this(null);
        }
    }

    /**
     * 取消任务响应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CancelTaskResponse(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("result") Task result,
        @JsonProperty("error") JsonRpcError error
    ) {
        public CancelTaskResponse(Task result) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), result, null);
        }
        
        public CancelTaskResponse() {
            this(null);
        }
    }

    /**
     * 设置任务推送通知请求
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SetTaskPushNotificationRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("method") String method,
        @JsonProperty("params") Object params
    ) {
        public SetTaskPushNotificationRequest(Object params) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), "tasks/pushNotification/set", params);
        }
        
        public SetTaskPushNotificationRequest() {
            this(null);
        }
    }

    /**
     * 设置任务推送通知响应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SetTaskPushNotificationResponse(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("result") TaskPushNotificationConfig result,
        @JsonProperty("error") JsonRpcError error
    ) {
        public SetTaskPushNotificationResponse(String id, TaskParams.TaskPushNotificationConfig result, JsonRpcError error) {
            this("2.0", id, result, error);
        }

        public SetTaskPushNotificationResponse(TaskParams.TaskPushNotificationConfig result) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), result, null);
        }
        
        public SetTaskPushNotificationResponse() {
            this(null);
        }
    }

    /**
     * 获取任务推送通知请求
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GetTaskPushNotificationRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("method") String method,
        @JsonProperty("params") Object params
    ) {
        public GetTaskPushNotificationRequest(Object params) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), "tasks/pushNotification/get", params);
        }
        
        public GetTaskPushNotificationRequest() {
            this(null);
        }
    }

    /**
     * 获取任务推送通知响应
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GetTaskPushNotificationResponse(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("result") TaskPushNotificationConfig result,
        @JsonProperty("error") JsonRpcError error
    ) {
        public GetTaskPushNotificationResponse(String id, TaskParams.TaskPushNotificationConfig result, JsonRpcError error) {
            this("2.0", id, result, error);
        }

        public GetTaskPushNotificationResponse(TaskParams.TaskPushNotificationConfig result) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), result, null);
        }
        
        public GetTaskPushNotificationResponse() {
            this(null);
        }
    }

    /**
     * 任务重新订阅请求
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TaskResubscriptionRequest(
        @JsonProperty("jsonrpc") String jsonrpc,
        @JsonProperty("id") String id,
        @JsonProperty("method") String method,
        @JsonProperty("params") Object params
    ) {
        public TaskResubscriptionRequest(Object params) {
            this("2.0", UUID.randomUUID().toString().replace("-", ""), "tasks/resubscribe", params);
        }
        
        public TaskResubscriptionRequest() {
            this(null);
        }
    }
}