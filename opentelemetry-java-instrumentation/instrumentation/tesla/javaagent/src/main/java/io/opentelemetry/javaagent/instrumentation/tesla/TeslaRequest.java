package io.opentelemetry.javaagent.instrumentation.tesla;

public class TeslaRequest {

    private String uri;

    private boolean apiInfoIsNull;

    public boolean isApiInfoIsNull() {
        return apiInfoIsNull;
    }

    public void setApiInfoIsNull(boolean apiInfoIsNull) {
        this.apiInfoIsNull = apiInfoIsNull;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
