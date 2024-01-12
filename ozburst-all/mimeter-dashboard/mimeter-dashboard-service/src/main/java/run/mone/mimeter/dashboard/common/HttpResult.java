package run.mone.mimeter.dashboard.common;

import java.util.Objects;

public class HttpResult {
    private int code;

    private String data;

    private String message;

    private String method;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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
