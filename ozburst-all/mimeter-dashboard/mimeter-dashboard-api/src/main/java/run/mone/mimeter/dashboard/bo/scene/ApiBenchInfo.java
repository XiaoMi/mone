package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
@Data
public class ApiBenchInfo implements Serializable,Comparable<ApiBenchInfo> {

    @HttpApiDocClassDefine(value = "apiOrder",required = true,description = "接口顺序",defaultValue = "1")
    private Integer apiOrder;

    @HttpApiDocClassDefine(value = "serialName",required = true,description = "链路名",defaultValue = "串联链路1")
    private String serialName;

    @HttpApiDocClassDefine(value = "apiName",description = "接口名称",defaultValue = "压测百度")
    private String apiName;

    @HttpApiDocClassDefine(value = "maxRps",description = "最大接口Rps",defaultValue = "2300")
    private Integer maxRps;

    @HttpApiDocClassDefine(value = "originRps",description = "起始接口Rps",defaultValue = "10")
    private Integer originRps;

    @HttpApiDocClassDefine(value = "linkOriRps",description = "链路起始rps",defaultValue = "10")
    private Integer linkOriRps;

    @HttpApiDocClassDefine(value = "linkTps",description = "链路最大rps",defaultValue = "100")
    private Integer linkTps;

    @HttpApiDocClassDefine(value = "linkBenchTime",description = "链路施压时间",defaultValue = "30")
    private Integer linkBenchTime;

    @Override
    public int compareTo(ApiBenchInfo apiBenchInfo) {
        return this.apiOrder.compareTo(apiBenchInfo.apiOrder);
    }
}
