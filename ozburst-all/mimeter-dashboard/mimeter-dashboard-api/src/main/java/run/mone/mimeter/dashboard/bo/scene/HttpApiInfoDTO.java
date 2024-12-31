package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;
import run.mone.mimeter.dashboard.bo.sceneapi.ApiTrafficInfo;
import run.mone.mimeter.dashboard.bo.sceneapi.ApiX5Info;
import run.mone.mimeter.dashboard.bo.sceneapi.SceneHttpApiInfoItemDetail;

import java.io.Serializable;

@Data
public class HttpApiInfoDTO extends SceneHttpApiInfoItemDetail implements Serializable,Comparable<HttpApiInfoDTO>{

    @HttpApiDocClassDefine(value = "sourceType", required = true, description = "接口来源类型 1:mi-api 2:手动添加", defaultValue = "1")
    private Integer sourceType;

    @HttpApiDocClassDefine(value = "requestTimeout", description = "接口即的请求超时时间 ms", defaultValue = "500")
    private Integer requestTimeout;

    @HttpApiDocClassDefine(value = "needLogin",ignore = true, description = "该接口是否需要登录", defaultValue = "false")
    private Boolean needLogin;

    @HttpApiDocClassDefine(value = "apiTspAuth", description = "单接口汽车部tsp验权信息，若配置将覆盖全局", defaultValue = "")
    private TspAuthInfo apiTspAuth;

    @HttpApiDocClassDefine(value = "apiTrafficInfo", description = "该接口的流量录制配置", defaultValue = "")
    private ApiTrafficInfo apiTrafficInfo;

    @HttpApiDocClassDefine(value = "apiX5Info", description = "该接口使用x5鉴权的信息", defaultValue = "")
    private ApiX5Info apiX5Info;

    @HttpApiDocClassDefine(value = "tokenType", ignore = true,description = "该接口使用的token业务类别", defaultValue = "1")
    private Integer tokenType;

    @HttpApiDocClassDefine(value = "contentType", description = "post请求数据格式类型", defaultValue = "application/json")
    private String contentType;

    @Override
    public int compareTo(HttpApiInfoDTO httpApiInfoDTO) {
        return getApiOrder().compareTo(httpApiInfoDTO.getApiOrder());
    }
}
