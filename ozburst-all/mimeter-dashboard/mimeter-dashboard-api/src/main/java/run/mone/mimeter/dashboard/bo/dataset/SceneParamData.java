package run.mone.mimeter.dashboard.bo.dataset;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SceneParamData implements Serializable {

    @HttpApiDocClassDefine(value = "paramDataType", required = true, description = "参数类型 0:全局 1:链路", defaultValue = "1")
    Integer paramDataType;

    @HttpApiDocClassDefine(value = "paramDataList", required = true, description = "参数列表")
    List<ParamData> paramDataList;
}
