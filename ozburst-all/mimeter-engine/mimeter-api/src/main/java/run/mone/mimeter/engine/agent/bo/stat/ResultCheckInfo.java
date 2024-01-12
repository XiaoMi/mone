package run.mone.mimeter.engine.agent.bo.stat;

import lombok.Data;

@Data
public class ResultCheckInfo {
    /**
     * 是否成功
     */
    private boolean ok;

    /**
     * 对应的检查点id
     */
    private Integer checkPointId;

}
