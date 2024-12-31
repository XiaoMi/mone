package run.mone.mimeter.dashboard.bo.scene;

import lombok.Data;

import java.io.Serializable;

@Data
public class DefaultSceneInfo implements Serializable {

    String defaultSceneName;
    String sceneType;
    Object apiInfo;
}
