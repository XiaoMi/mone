package run.mone.mimeter.dashboard.bo.sceneapi;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * todo 目前尚未支持dubbo的流量录制后续支持后续另加 dubbo 相关
 */
@Data
public class ApiTrafficInfo implements Serializable {
    @HttpApiDocClassDefine(value = "enableTraffic", description = "是否使用录制的流量", defaultValue = "false")
    private boolean enableTraffic;

    @HttpApiDocClassDefine(value = "url", description = "录制的网关url", defaultValue = "/mtop/test/api")
    private String url;

    @HttpApiDocClassDefine(value = "fromTime", description = "录制参数的时间范围起点", defaultValue = "141242112")
    private long fromTime;

    @HttpApiDocClassDefine(value = "toTime", description = "录制参数的的时间范围终点", defaultValue = "141242112")
    private long toTime;

    @HttpApiDocClassDefine(value = "recordingConfigId", description = "录制配置id", defaultValue = "31")
    private Integer recordingConfigId;

    public ApiTrafficInfo() {
    }

    public ApiTrafficInfo(boolean enableTraffic) {
        this.enableTraffic = enableTraffic;
    }
}
