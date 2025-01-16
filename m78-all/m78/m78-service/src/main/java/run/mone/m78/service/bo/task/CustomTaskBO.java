package run.mone.m78.service.bo.task;


import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class CustomTaskBO implements Serializable {

    @HttpApiDocClassDefine(value = "id", required = false, description = "任务id", defaultValue = "")
    private Long id;

    @HttpApiDocClassDefine(value = "taskName", required = true, description = "任务名称", defaultValue = "")
    private String taskName;

    /**
     * 任务类型：0-corn，1-固定频率，2-单次执行
     */
    @HttpApiDocClassDefine(value = "taskType", required = true, description = "任务类型，0-corn，1-固定频率，2-单次执行", defaultValue = "")
    private Integer taskType;

    @HttpApiDocClassDefine(value = "scheduledTime", required = true, description = "执行时间", defaultValue = "")
    private String scheduledTime;

    @HttpApiDocClassDefine(value = "taskDetail", required = true, description = "任务详情", defaultValue = "")
    private TaskDetail taskDetail;

    @HttpApiDocClassDefine(value = "status", required = true, description = "任务状态", defaultValue = "1")
    private Integer status;

    private String userName;

    @HttpApiDocClassDefine(value = "input", required = true, description = "用户任务的输入参数", defaultValue = "")
    private String input;

    private Long ctime;

    private Long utime;

    @HttpApiDocClassDefine(value = "botId", required = true, description = "botId", defaultValue = "")
    private Long botId;

    @HttpApiDocClassDefine(value = "moonId", required = false, description = "moonId", defaultValue = "")
    private Long moonId;

    @HttpApiDocClassDefine(value = "coreType", required = false, description = "cron任务时，传day,week,month", defaultValue = "")
    private String coreType;

}
