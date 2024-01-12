package run.mone.mimeter.agent.manager.bo;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class SlaContext {

    /**
     * 所属任务标识
     */
    private volatile String reportId;

    /**
     * 场景id
     */
    private volatile int sceneId;

    /**
     * 场景类型
     */
    private volatile String sceneType;

    /**
     * 完成
     */
    private volatile boolean finish;

    /**
     * 被取消掉
     */
    private volatile boolean cancel;

    /**
     * 指标名，对应次数触发规则<P99ResponseTime_Warn,3> or <P99ResponseTime_Err,3>
     */
    private final ConcurrentHashMap<String, AtomicInteger> labelCount = new ConcurrentHashMap<>();

    public SlaContext(String reportId, int sceneId, boolean finish, boolean cancel) {
        this.reportId = reportId;
        this.sceneId = sceneId;
        this.finish = finish;
        this.cancel = cancel;
    }

}
