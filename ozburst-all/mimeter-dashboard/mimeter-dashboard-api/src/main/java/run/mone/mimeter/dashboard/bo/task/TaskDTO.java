package run.mone.mimeter.dashboard.bo.task;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TaskDTO implements Serializable {

    /**
     * 任务id列表
     */
    @HttpApiDocClassDefine(value = "ids", ignore = true, description = "该任务id列表", defaultValue = "")
    private List<Integer> ids;

    /**
     * 场景定义id
     */
    @HttpApiDocClassDefine(value = "sceneId", required = true, description = "任务所属的场景id", defaultValue = "12")
    private Integer sceneId;

    /**
     * 0:单接口调试
     * 1:场景调试
     * 2:场景压测
     */
    @HttpApiDocClassDefine(value = "submitTaskType", ignore = true)
    private Integer submitTaskType;

    /**
     * 每次压测任务绑定一个唯一报告id
     */
    @HttpApiDocClassDefine(value = "reportId", ignore = true)
    private String reportId;

    /**
     * 用于调试的接口信息
     */
    @HttpApiDocClassDefine(value = "apiInfo", required = true, description = "具体的被调试接口信息，只有单接口调试时需要直接传")
    private DebugSceneApiInfoReq apiInfo;

    private String opUser;

    private String tenant;

}
