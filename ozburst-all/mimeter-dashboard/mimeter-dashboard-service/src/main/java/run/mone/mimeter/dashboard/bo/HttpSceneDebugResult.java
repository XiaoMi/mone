package run.mone.mimeter.dashboard.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HttpSceneDebugResult implements Serializable {

    private boolean parentTask;

    private Integer id;

    private Integer sceneId;

    private String apiName;

    private String apiUrl;

    private Integer apiOrder;

    private Integer requestMethod;

    private Integer requestTimeout;

    private String contentType;

    private String apiHeader;

    private Integer reqParamType;

    private String requestParamInfo;

    private String outputParamInfo;

    private String requestBody;

    private String respHeader;

    private String triggerCpInfo;

    private String debugTriggerFilterCondition;

    private String debugResult;

    private Integer taskStatus;

    private String realParam;

    private boolean ok;

    private long rt;

    //bytes
    private long size;

}
