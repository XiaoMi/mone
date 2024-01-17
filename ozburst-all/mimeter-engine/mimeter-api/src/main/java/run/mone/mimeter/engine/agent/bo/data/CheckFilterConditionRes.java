package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckFilterConditionRes implements Serializable {
    private boolean match;
    private String triggerFilterCondition;
}
