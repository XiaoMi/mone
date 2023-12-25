package run.mone.mimeter.dashboard.bo.scenegroup;

import lombok.Data;
import run.mone.mimeter.dashboard.bo.scene.BasicSceneDTO;

import java.io.Serializable;
import java.util.List;

@Data
public class GroupSceneDTO implements Serializable {

    private Integer sceneGroupID;

    private String groupName;

    private String groupDesc;

    private Long ctime;
    
    private List<BasicSceneDTO> list;

}
