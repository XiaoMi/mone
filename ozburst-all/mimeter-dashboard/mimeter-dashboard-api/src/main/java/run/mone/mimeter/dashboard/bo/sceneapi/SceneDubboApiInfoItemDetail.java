package run.mone.mimeter.dashboard.bo.sceneapi;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * for get dubbo api detail resp
 */
@Data
public class SceneDubboApiInfoItemDetail extends SceneDubboApiInfoItemBasic implements Serializable {
    @HttpApiDocClassDefine(value = "dubboEnv", required = true, description = "dubbo服务的nacos环境", defaultValue = "staging")
    private String dubboEnv;

    @HttpApiDocClassDefine(value = "requestParamTypeList", required = true, description = "请求参数类型列表", defaultValue = "[]")
    private List<String> requestParamTypeList;

    @HttpApiDocClassDefine(value = "requestBody", required = true, description = "请求数据，json格式字符串", defaultValue = "[{}]")
    private String requestBody;

    @HttpApiDocClassDefine(value = "attachments", description = "dubbo携带的attachements，等同于http的header", defaultValue = "{}")
    private String attachments;

}
