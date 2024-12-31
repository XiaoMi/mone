package run.mone.mimeter.engine.agent.bo.task;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChangeQpsReq implements Serializable {

    Integer rpsRate;

    List<DagTaskRps> dagTaskRpsList;

    public ChangeQpsReq(List<DagTaskRps> dagTaskRpsList) {
        this.dagTaskRpsList = dagTaskRpsList;
    }
}
