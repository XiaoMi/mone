package run.mone.m78.api.bo.flow;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/3/13
 */
@Data
@Builder
public class NewConditionSetting implements Serializable {

    /**
     *
     * if从1开始计数
     * 最后一个else用-1表示
     */
    private Map<String, List<ConditionSetting>> conditionExpress;

}
