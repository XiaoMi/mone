package run.mone.mimeter.dashboard.bo.sceneapi;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class SceneHttpApiInfoItemBasic extends SceneApiInfoItemBasic implements Serializable {
    @HttpApiDocClassDefine(value = "apiUrl", required = true, description = "接口的url", defaultValue = "/api/getUserInfo")
    private String apiUrl;

    @HttpApiDocClassDefine(value = "apiRequestType", required = true, description = "http接口请求类型 0:post 1:get", defaultValue = "0")
    private Integer apiRequestType;
}
