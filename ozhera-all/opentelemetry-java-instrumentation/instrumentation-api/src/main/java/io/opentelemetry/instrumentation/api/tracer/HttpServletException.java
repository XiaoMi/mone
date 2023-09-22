package io.opentelemetry.instrumentation.api.tracer;

public class HttpServletException extends RuntimeException{
    public HttpServletException(String message){
        super(message);
    }
}
