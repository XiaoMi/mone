package run.mone.mimeter.engine.agent.bo.data;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dongzhenxing
 * 用于单独调试的接口信息
 */
@Data
public class DebugSceneApiInfoReq implements Serializable {

    private Integer apiType;

    private String apiUrl;

    private Integer requestMethod;

    private Integer requestTimeout;

    private String contentType;

    private String serviceName;

    private String methodName;

    private String attachments;

    private String paramTypeList;

    private String dubboGroup;

    private String dubboVersion;

    private String dubboMavenVersion;

    private String apiHeader;

    private TspAuthInfoDTO apiTspAuth;

    private String requestParamInfo;

    private String requestBody;

    private String dubboParamJson;

    private String checkPointInfoListStr;

    private String outputParamInfosStr;

}
