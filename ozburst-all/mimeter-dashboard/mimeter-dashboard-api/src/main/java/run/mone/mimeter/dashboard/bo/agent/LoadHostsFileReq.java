package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoadHostsFileReq implements Serializable {
    @HttpApiDocClassDefine(value = "agentIp", required = true, description = "要获取发压机Ip", defaultValue = "127.0.0.1")
    private String agentIp;
}
