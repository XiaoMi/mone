package run.mone.mimeter.dashboard.bo.sceneapi;

import lombok.Data;

import java.io.Serializable;

@Data
public class SceneDubboApiInfoItemBasic extends SceneApiInfoItemBasic implements Serializable {
    private String serviceName;
    private String methodName;
    private String group;
    private String version;
    private String path;
}
