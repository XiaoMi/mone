package run.mone.ultraman.state.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/12/24 19:15
 */
@Data
@Builder
public class StateInfo implements Serializable {

    private int step;

    private List<String> addonList;

}
