package run.mone.mimeter.dashboard.bo.task;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class SceneRpsRateReq implements Serializable {
    @HttpApiDocClassDefine(value = "sceneID", required = true, description = "场景id", defaultValue = "66312")
    Integer sceneID;
    @HttpApiDocClassDefine(value = "reportID", required = true, description = "报告id", defaultValue = "66124")
    String reportID;
    @HttpApiDocClassDefine(value = "rpsRate", required = true, description = "发压比例", defaultValue = "10")
    Integer rpsRate;

    @HttpApiDocClassDefine(value = "linkToTaskMaps", required = true, description = "链路id与任务id映射", defaultValue = "")
    Map<Integer,Integer> linkToTaskMaps;
}
