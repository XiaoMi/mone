package run.mone.m78.api.bo.plugins;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DubboTestReq implements Serializable {

    private Long pluginId;

    private List<Object> params;

    private Map<String, String> rpcContext;

}
