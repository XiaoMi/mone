package run.mone.mimeter.dashboard.bo.agent;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class DomainIpAndRate implements Serializable {
    @HttpApiDocClassDefine(value = "ip",required = true,description = "要绑定的ip")
    private String ip;
    @HttpApiDocClassDefine(value = "rate",required = true,description = "比例，只能整数 10、20..100")
    private Integer rate;
}
