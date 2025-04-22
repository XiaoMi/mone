package com.google.a2a.common.types;

public class A2AClientExceptions {
    /**
     * A2A客户端错误的基类
     */
    public class A2AClientError extends RuntimeException {
        public A2AClientError(String message) {
            super(message);
        }
        
        public A2AClientError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * A2A客户端HTTP错误
     */
    public class A2AClientHTTPError extends A2AClientError {
        private final int statusCode;
        
        public A2AClientHTTPError(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }
        
        public A2AClientHTTPError(int statusCode, String message, Throwable cause) {
            super(message, cause);
            this.statusCode = statusCode;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
    }

    /**
     * A2A客户端JSON错误
     */
    public class A2AClientJSONError extends A2AClientError {
        public A2AClientJSONError(String message) {
            super(message);
        }
        
        public A2AClientJSONError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 缺少API密钥错误
     */
    public class MissingAPIKeyError extends RuntimeException {
        public MissingAPIKeyError(String message) {
            super(message);
        }
    } 
}