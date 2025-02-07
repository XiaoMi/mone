package run.mone.m78.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 14:38
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class M78FlowReq {

    private String userName;

    private String flowId;

    private String flowRecordId;

    private String operateCmd;

    private String cmd;

    private Map<String, Object> inputs;
}
