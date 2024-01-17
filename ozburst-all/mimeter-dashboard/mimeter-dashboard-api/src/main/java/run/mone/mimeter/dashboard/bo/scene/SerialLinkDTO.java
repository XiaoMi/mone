package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SerialLinkDTO implements Serializable {

    @HttpApiDocClassDefine(value = "httpApiInfoDTOList", description = "绑定的接口列表,http场景")
    private List<HttpApiInfoDTO> httpApiInfoDTOList;

    @HttpApiDocClassDefine(value = "dubboApiInfoDTOList", description = "绑定的接口列表,dubbo场景")
    private List<DubboApiInfoDTO> dubboApiInfoDTOList;

    @HttpApiDocClassDefine(value = "serialLinkID",required = false, description = "接口所属链路id,更新时使用，新增不需要", defaultValue = "12")
    private Integer serialLinkID;

    @HttpApiDocClassDefine(value = "serialLinkName",required = true, description = "接口所属链路名", defaultValue = "串联链路1")
    private String serialLinkName;

    @HttpApiDocClassDefine(value = "enable",required = true, description = "压测时是否启用该接口", defaultValue = "true")
    private Boolean enable;
}
