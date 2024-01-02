package run.mone.mimeter.dashboard.bo.scene;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SceneTaskAppsBo implements Serializable {
    private String sceneTask;
    private Long fromTime;
    private Long toTime;

    private List<LinkTaskAppsBo> serialLinks;

    @Data
    public static class LinkTaskAppsBo implements Serializable {
        private String serialLinkName;
        private String serialLinkId;
        private List<ApiTaskAppsBo> apis;
    }

    @Data
    public static class ApiTaskAppsBo implements Serializable {
        private String apiName;
        private String apiId;
        private List<String> apps;
    }



}

