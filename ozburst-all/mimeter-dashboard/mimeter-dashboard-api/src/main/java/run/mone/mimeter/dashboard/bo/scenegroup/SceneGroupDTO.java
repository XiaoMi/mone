package run.mone.mimeter.dashboard.bo.scenegroup;

import lombok.Data;

import java.io.Serializable;

@Data
public class SceneGroupDTO implements Serializable {
    private Integer id;

    private String groupName;

    private String groupDesc;

    private String creator;

    private String tenant;
}
