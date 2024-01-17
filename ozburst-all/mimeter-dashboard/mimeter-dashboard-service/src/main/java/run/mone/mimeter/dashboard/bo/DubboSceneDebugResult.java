package run.mone.mimeter.dashboard.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DubboSceneDebugResult implements Serializable {

    private boolean parentTask;

    private Integer id;

    private Integer sceneId;

    private String apiName;

    private String serviceName;

    private String methodName;

    private String group;

    private String version;

    private Integer apiOrder;

    private Integer requestTimeout;

    private String paramsTypeList;

    private String requestBody;

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
