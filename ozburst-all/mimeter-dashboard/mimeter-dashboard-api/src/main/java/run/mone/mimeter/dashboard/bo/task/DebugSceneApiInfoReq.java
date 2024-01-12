package run.mone.mimeter.dashboard.bo.task;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;
import run.mone.mimeter.dashboard.bo.scene.TspAuthInfo;
import run.mone.mimeter.dashboard.bo.sceneapi.CheckPointInfoDTO;
import run.mone.mimeter.dashboard.bo.sceneapi.SceneApiOutputParam;

import java.io.Serializable;
import java.util.List;

/**
 * @author dongzhenxing
 * 用于单独调试的接口信息
 */
@Data
public class DebugSceneApiInfoReq implements Serializable {

    @HttpApiDocClassDefine(value = "apiType", description = "接口类型 1:http 3:dubbo")
    private Integer apiType;

    @HttpApiDocClassDefine(value = "apiUrl", description = "http接口 url",defaultValue = "http://www.baidu.com")
    private String apiUrl;

    @HttpApiDocClassDefine(value = "requestMethod", description = "请求方式  0:get,1:post",defaultValue = "1")
    private Integer requestMethod;

    @HttpApiDocClassDefine(value = "apiHeader", description = "http请求头",defaultValue = "json格式字符串")
    private String apiHeader;

    @HttpApiDocClassDefine(value = "requestParamInfo",ignore = true, description = "http请求参数信息，带格式",defaultValue = "")
    private String requestParamInfo;

    @HttpApiDocClassDefine(value = "requestBody", description = "http请求参数信息，raw",defaultValue = "")
    private String requestBody;

    @HttpApiDocClassDefine(value = "requestTimeout", description = "请求超时时间",defaultValue = "3000")
    private Integer requestTimeout = 1000;

    @HttpApiDocClassDefine(value = "contentType", description = "http接口参数格式类型",defaultValue = "application/json")
    private String contentType;

    @HttpApiDocClassDefine(value = "api_tsp_auth", description = "单接口汽车部tsp验权信息，若配置将覆盖全局", defaultValue = "")
    private TspAuthInfo apiTspAuth;

    @HttpApiDocClassDefine(value = "checkPointInfoList", description = "检查点信息列表", defaultValue = "")
    private List<CheckPointInfoDTO> checkPointInfoList;

    @HttpApiDocClassDefine(value = "outputParamInfo", description = "出参定义", defaultValue = "")
    private List<SceneApiOutputParam> outputParamInfos;

    private String checkPointInfoListStr;

    private String outputParamInfosStr;

    @HttpApiDocClassDefine(value = "serviceName", description = "dubbo服务名",defaultValue = "test0930.api.HealthService")
    private String serviceName;

    @HttpApiDocClassDefine(value = "methodName", description = "dubbo方法名",defaultValue = "ping")
    private String methodName;

    @HttpApiDocClassDefine(value = "paramTypeList", description = "参数类型列表",defaultValue = "")
    private String paramTypeList;

    @HttpApiDocClassDefine(value = "dubboGroup", description = "dubbo服务分组",defaultValue = "staging")
    private String dubboGroup;

    @HttpApiDocClassDefine(value = "dubboVersion", description = "dubbo服务版本",defaultValue = "1.0")
    private String dubboVersion;

    private String dubboParamJson;

    private String attachments;

}
