package run.mone.local.docean.fsm.bo;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/2/23
 */
@Data
public class FlowRes<T> {

    private int code;

    public static final int SUCCESS = 0;

    public static final int FAILURE = 500;

    public static final int RETRY= 333;

    public static final int WAIT= 777;

    public static final int BREAK = 886;

    public static final int CANCEL = 999;

    public static final int GOTO = 222;

    public static final int SKIP = 221;

    public static final int GOTO_EXCEED_ERROR = 223;

    private String message;

    private Map<String,String> attachement;

    //记录运行时每个节点的input实际值  nodeId
    private Map<Integer, SyncFlowStatus.SyncNodeInput> inputs = new HashMap<>();

    //记录运行后每个节点的output实际值
    private Map<Integer, SyncFlowStatus.SyncNodeOutput> outputs = new HashMap<>();

    private T data;


    public static <T> FlowRes success(T data) {
        FlowRes<T> res = new FlowRes();
        res.setCode(SUCCESS);
        res.setMessage("success");
        res.setData(data);
        return res;
    }

    public static FlowRes failure(Throwable throwable) {
        FlowRes res = new FlowRes();
        res.setCode(FAILURE);
        res.setMessage(throwable.getMessage());
        return res;
    }

    public static FlowRes failure(String message) {
        FlowRes res = new FlowRes();
        res.setCode(FAILURE);
        res.setMessage(message);
        return res;
    }

    public static FlowRes retry() {
        FlowRes res = new FlowRes();
        res.setCode(RETRY);
        return res;
    }

    public static FlowRes cancel() {
        FlowRes res = new FlowRes();
        res.setCode(CANCEL);
        return res;
    }

    public FlowRes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public FlowRes() {
    }
}
