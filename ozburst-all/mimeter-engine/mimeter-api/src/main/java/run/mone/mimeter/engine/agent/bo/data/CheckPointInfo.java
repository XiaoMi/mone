package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;
import java.io.Serializable;

@Data
public class CheckPointInfo implements Serializable {
    private Integer id;
    private Integer checkType;
    private String checkObj;
    private Integer checkCondition;
    private String checkContent;
}
