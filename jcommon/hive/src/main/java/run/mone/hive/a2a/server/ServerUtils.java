package com.google.a2a.common.server;

import com.google.a2a.common.types.JsonRpcErrors;
import com.google.a2a.common.types.JsonRpcTypes;

/**
 * 服务器工具类
 */
public class ServerUtils {
    
    /**
     * 创建未实现操作的错误响应
     * @param requestId 请求ID
     * @return 错误响应
     */
    public static JsonRpcTypes.JsonRpcResponse newNotImplementedError(String requestId) {
        JsonRpcErrors.UnsupportedOperationError error = new JsonRpcErrors.UnsupportedOperationError();
        JsonRpcTypes.JsonRpcResponse response = new JsonRpcTypes.JsonRpcResponse(requestId, new JsonRpcTypes.JsonRpcError(error.code(), error.message(), null));
        return response;
    }
    
    /**
     * 创建自定义错误响应
     * @param requestId 请求ID
     * @param error 错误信息
     * @return 错误响应
     */
    public static JsonRpcTypes.JsonRpcResponse newErrorResponse(String requestId, JsonRpcTypes.JsonRpcError error) {
        JsonRpcTypes.JsonRpcResponse response = new JsonRpcTypes.JsonRpcResponse(requestId, error);
        return response;
    }
} 