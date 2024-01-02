package run.mone.mimeter.dashboard.bo.scenegroup;

import lombok.Data;
import run.mone.mimeter.dashboard.bo.common.PageBase;

import java.io.Serializable;
import java.util.List;

@Data
public class SceneGroupList extends PageBase implements Serializable {

    private List<GroupSceneDTO> list;
}
