package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * 子工作流核心配置
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class SubFlowSetting implements Serializable {

    private static final long serialVersionUID = 1102544143508934316L;

    private String flowId;

}
