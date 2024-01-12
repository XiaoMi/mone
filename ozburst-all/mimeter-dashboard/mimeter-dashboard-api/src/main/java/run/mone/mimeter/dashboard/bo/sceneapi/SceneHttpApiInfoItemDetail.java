package run.mone.mimeter.dashboard.bo.sceneapi;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * for get http api detail resp
 */
@Data
public class SceneHttpApiInfoItemDetail extends SceneHttpApiInfoItemBasic implements Serializable {
    @HttpApiDocClassDefine(value = "requestInfo", description = "请求数据，json格式字符串,含格式", defaultValue = "{}")
    private String requestInfo;

    @HttpApiDocClassDefine(value = "apiRequestParamType", description = "请求数据格式", defaultValue = "0")
    private Integer apiRequestParamType;

    @HttpApiDocClassDefine(value = "requestRaw", description = "请求数据，json raw格式", defaultValue = "{}")
    private String requestInfoRaw;

    @HttpApiDocClassDefine(value = "headerInfo", required = true, description = "请求头，json格式字符串", defaultValue = "{}")
    private String headerInfo;

}
