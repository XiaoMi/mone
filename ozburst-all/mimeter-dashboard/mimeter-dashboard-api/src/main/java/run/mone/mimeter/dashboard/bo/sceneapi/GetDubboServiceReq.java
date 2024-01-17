package run.mone.mimeter.dashboard.bo.sceneapi;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetDubboServiceReq implements Serializable {
    String env;
    String serviceName;
}
