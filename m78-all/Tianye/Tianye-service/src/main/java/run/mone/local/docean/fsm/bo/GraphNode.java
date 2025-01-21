package run.mone.local.docean.fsm.bo;

import com.google.common.collect.Lists;
import lombok.Data;
import run.mone.local.docean.fsm.BotFlow;

import java.util.List;

/**
 * @author wmin
 * @date 2024/8/19
 */
@Data
public class GraphNode {
    private int id;
    private BotFlow currentVertex;
    private List<Integer> dependList = Lists.newArrayList();
    private List<BotFlow> childList;
    private int status;
}
