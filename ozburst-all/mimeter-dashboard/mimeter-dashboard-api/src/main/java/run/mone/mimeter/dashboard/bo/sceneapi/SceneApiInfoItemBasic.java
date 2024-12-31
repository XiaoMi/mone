package run.mone.mimeter.dashboard.bo.sceneapi;

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
public class SceneApiInfoItemBasic implements Serializable {
    @HttpApiDocClassDefine(value = "apiName", required = true, description = "接口名称", defaultValue = "测试接口")
    private String apiName;

    @HttpApiDocClassDefine(value = "apiOrder", required = true, description = "该场景于该链路下的接口顺序", defaultValue = "1")
    private Integer apiOrder;

    @HttpApiDocClassDefine(value = "projectID", ignore = true, required = true, description = "接口在mi-api中的项目id", defaultValue = "666")
    private Integer projectID;

    @HttpApiDocClassDefine(value = "apiID", ignore = true, required = true, description = "接口在mi-api中的id", defaultValue = "1411")
    private Integer apiID;

    @HttpApiDocClassDefine(value = "apiProtocol", required = true, description = "该接口的协议类型 1:http 3:dubbo 4:mione gateway", defaultValue = "1")
    private Integer apiProtocol;

    @HttpApiDocClassDefine(value = "outputParamInfo", description = "出参定义", defaultValue = "")
    private List<SceneApiOutputParam> outputParamInfos;

    @HttpApiDocClassDefine(value = "checkPointInfoList", description = "检查点信息列表", defaultValue = "")
    private List<CheckPointInfoDTO> checkPointInfoList;

    @HttpApiDocClassDefine(value = "filterCondition", description = "过滤条件列表", defaultValue = "")
    private List<CheckPointInfoDTO> filterCondition;


}
