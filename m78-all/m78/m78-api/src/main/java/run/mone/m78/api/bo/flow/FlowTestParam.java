package run.mone.m78.api.bo.flow;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowTestParam implements Serializable {
    private Integer flowId;

    private Map<String, JsonElement> inputs;

    private String userName;

    private int executeType;

    private NodeInfo nodeInfo;

    @Builder.Default
    private Map<String, String> meta = new HashMap<>();

}
