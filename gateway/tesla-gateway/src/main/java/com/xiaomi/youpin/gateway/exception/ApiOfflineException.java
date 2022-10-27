package com.xiaomi.youpin.gateway.exception;

public class ApiOfflineException extends GatewayException{
    public ApiOfflineException(Throwable cause) {
        super(cause);
    }

    public ApiOfflineException(String message) {
        super(message);
    }
}
