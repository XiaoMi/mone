package run.mone.mimeter.engine.agent.bo.stat;

import lombok.Data;

@Data
public class HttpResultCheckInfo extends ResultCheckInfo {
    /**
     * http状态码
     */
    private String httpStatusCode;

    private String triggerCpInfo;

}
