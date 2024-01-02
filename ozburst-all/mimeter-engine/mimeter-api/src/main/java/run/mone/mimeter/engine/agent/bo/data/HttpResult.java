package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.util.Objects;

@Data
public class HttpResult {
    private int code;

    private String data;

    private String message;

    private String method;

    public HttpResult(int code, String data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static HttpResult fail(int errorCode, String message,String data) {
        return new HttpResult(errorCode, message,data);
    }

    public static HttpResult success(String data) {
        return new HttpResult(200, data,"ok");
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", data='" + data + '\'' +
                ", message='" + message + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResult that = (HttpResult) o;
        return code == that.code &&
                Objects.equals(data, that.data) &&
                Objects.equals(message, that.message) &&
                Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, data, message, method);
    }
}
