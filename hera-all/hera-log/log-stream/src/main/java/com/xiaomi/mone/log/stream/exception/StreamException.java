package com.xiaomi.mone.log.stream.exception;

public class StreamException extends RuntimeException {
    public StreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamException(Throwable cause) {
        super(cause);
    }
}
