package run.mone.mimeter.dashboard.bo.sceneapi;

import lombok.Data;

import java.io.Serializable;

@Data
public class FormParamValue implements Serializable {
    String paramKey;
    Object paramValue;

    public FormParamValue(String paramKey, Object paramValue) {
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }
}
