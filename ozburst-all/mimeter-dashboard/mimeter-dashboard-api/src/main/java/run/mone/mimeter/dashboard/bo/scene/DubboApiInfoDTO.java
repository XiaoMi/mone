package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;
import run.mone.mimeter.dashboard.bo.sceneapi.SceneDubboApiInfoItemDetail;

import java.io.Serializable;

@Data
public class DubboApiInfoDTO extends SceneDubboApiInfoItemDetail implements Serializable,Comparable<DubboApiInfoDTO> {

    @HttpApiDocClassDefine(value = "sourceType", required = true, description = "接口来源类型 1:mi-api 2:手动添加", defaultValue = "1")
    private Integer sourceType;

    @HttpApiDocClassDefine(value = "requestTimeout", description = "dubbo接口即的请求超时时间 ms", defaultValue = "500")
    private Integer requestTimeout;

    @Override
    public int compareTo(DubboApiInfoDTO dubboApiInfoDTO) {
        return getApiOrder().compareTo(dubboApiInfoDTO.getApiOrder());
    }
}
