package run.mone.mimeter.dashboard.bo.task;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class DagTaskRps implements Serializable {
    @HttpApiDocClassDefine(value = "linkId", required = true, description = "链路id", defaultValue = "120221")
    Integer linkId;
    @HttpApiDocClassDefine(value = "taskId", required = true, description = "该链路当前任务id", defaultValue = "12123")
    Integer taskId;

    @HttpApiDocClassDefine(value = "rps", required = true, description = "该链路当前任务rps", defaultValue = "10000")
    Integer rps;

    public DagTaskRps() {
    }

    public DagTaskRps(Integer linkId, Integer taskId, Integer rps) {
        this.linkId = linkId;
        this.taskId = taskId;
        this.rps = rps;
    }
}
