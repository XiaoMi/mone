package run.mone.mimeter.dashboard.bo.sceneapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CheckPointInfoDTO implements Serializable {
    private Integer id;

    @JsonProperty(value = "type")
    private Integer checkType = 3;
    private String checkObj;
    private Integer checkCondition;
    private String checkContent;
}
