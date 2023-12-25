package run.mone.mimeter.dashboard.bo.sceneapi;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiHeader implements Serializable {
    private String headerName;
    private String headerValue;

    public ApiHeader(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }
}
