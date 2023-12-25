package run.mone.mimeter.dashboard.bo.scene;

import lombok.Data;
import run.mone.mimeter.dashboard.bo.common.PageBase;

import java.io.Serializable;
import java.util.List;

@Data
public class SceneList extends PageBase implements Serializable {

    private List<BasicSceneDTO> list;

}
