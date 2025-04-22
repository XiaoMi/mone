package com.google.a2a.common.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonRpcErrors {
    /**
     * JSON解析错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record JsonParseError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public JsonParseError() {
            this(-32700, "Invalid JSON payload", null);
        }
    }

    /**
     * 请求无效错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InvalidRequestError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public InvalidRequestError() {
            this(-32600, "Request payload validation error", null);
        }
        
        public InvalidRequestError(Object data) {
            this(-32600, "Request payload validation error", data);
        }
    }

    /**
     * 方法未找到错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record MethodNotFoundError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public MethodNotFoundError() {
            this(-32601, "Method not found", null);
        }
    }

    /**
     * 参数无效错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InvalidParamsError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public InvalidParamsError() {
            this(-32602, "Invalid parameters", null);
        }
        
        public InvalidParamsError(Object data) {
            this(-32602, "Invalid parameters", data);
        }
    }

    /**
     * 内部错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InternalError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public InternalError() {
            this(-32603, "Internal error", null);
        }
        
        public InternalError(String message) {
            this(-32603, message != null ? message : "Internal error", null);
        }
        
        public InternalError(Object data) {
            this(-32603, "Internal error", data);
        }
    }

    /**
     * 任务未找到错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TaskNotFoundError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public TaskNotFoundError() {
            this(-32001, "Task not found", null);
        }
    }

    /**
     * 任务无法取消错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TaskNotCancelableError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public TaskNotCancelableError() {
            this(-32002, "Task cannot be canceled", null);
        }
    }

    /**
     * 推送通知不支持错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PushNotificationNotSupportedError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public PushNotificationNotSupportedError() {
            this(-32003, "Push Notification is not supported", null);
        }
    }

    /**
     * 不支持的操作错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UnsupportedOperationError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public UnsupportedOperationError() {
            this(-32004, "This operation is not supported", null);
        }
    }

    /**
     * 内容类型不支持错误
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ContentTypeNotSupportedError(
        @JsonProperty("code") int code,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
    ) {
        public ContentTypeNotSupportedError() {
            this(-32005, "Incompatible content types", null);
        }
    } 
}