package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/3/6
 */
@Data
public class FlowInfo implements Serializable {

    private FlowBaseInfo flowBaseInfo;

    private FlowSettingInfo flowSettingInfo;

    private boolean hasPermission;

}
