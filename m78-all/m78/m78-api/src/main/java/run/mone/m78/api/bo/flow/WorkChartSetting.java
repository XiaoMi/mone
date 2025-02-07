package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class WorkChartSetting extends LLMBaseSetting implements Serializable {

    /**
     * @see run.mone.m78.api.enums.WorkChartTypeEnum
     */
    private String chartType;//$$TY_WORK_CHART_TYPE$$

    private String input;//$$TY_INPUT$$

}
