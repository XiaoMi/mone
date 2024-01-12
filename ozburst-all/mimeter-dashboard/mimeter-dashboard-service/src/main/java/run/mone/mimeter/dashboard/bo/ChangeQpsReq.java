package run.mone.mimeter.dashboard.bo;

import lombok.Data;
import run.mone.mimeter.dashboard.bo.task.DagTaskRps;

import java.io.Serializable;
import java.util.List;

@Data
public class ChangeQpsReq implements Serializable {

    Integer rpsRate;

    List<DagTaskRps> dagTaskRpsList;

    public ChangeQpsReq() {
    }

    public ChangeQpsReq(List<DagTaskRps> dagTaskRpsList) {
        this.dagTaskRpsList = dagTaskRpsList;
    }
}
