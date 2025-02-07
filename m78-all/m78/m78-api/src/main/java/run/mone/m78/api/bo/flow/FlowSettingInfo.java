package run.mone.m78.api.bo.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowSettingInfo implements Serializable {

    private Integer id;

    private Integer flowBaseId;

    private List<NodeInfo> nodes;

    private List<Edge> edges;

}
