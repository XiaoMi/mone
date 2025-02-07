package run.mone.m78.api.bo.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowTestRes implements Serializable {
    private String flowRecordId;
    private Map<String, Object> outputs;
}
