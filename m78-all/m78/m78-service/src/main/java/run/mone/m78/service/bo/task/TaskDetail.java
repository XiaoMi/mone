package run.mone.m78.service.bo.task;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskDetail implements Serializable {

    /**
     * 秒,0-59
     */
    @HttpApiDocClassDefine(value = "second", required = false, description = "秒,0-59", defaultValue = "")
    private String second;
    /**
     * 分,0-59
     */
    @HttpApiDocClassDefine(value = "minute", required = false, description = "分,0-59", defaultValue = "")
    private String minute;
    /**
     * 时,0-23
     */
    @HttpApiDocClassDefine(value = "hour", required = false, description = "时,0-23", defaultValue = "")
    private String hour;
    /**
     * 日,1-31
     */
    @HttpApiDocClassDefine(value = "day", required = false, description = "日,1-31", defaultValue = "")
    private String day;
    /**
     * 月,1-12
     */
    @HttpApiDocClassDefine(value = "month", required = false, description = "月,1-12", defaultValue = "")
    private String month;
    /**
     * 周,0-7
     */
    @HttpApiDocClassDefine(value = "week", required = false, description = "周,0-7", defaultValue = "")
    private String week;
}
