package com.xiaomi.youpin.gateway.exception;

public class ApiNotFoundException extends GatewayException{
    public ApiNotFoundException(Throwable cause) {
        super(cause);
    }

    public ApiNotFoundException(String message) {
        super(message);
    }
}
