package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/3/13
 */
@Data
public class ConditionEdge implements Serializable {

    //sourceId <-> targetIds
    private Map<Integer, List<Integer>> ifEdgeMap;

    private Map<Integer, List<Integer>> elseEdgeMap;

}
