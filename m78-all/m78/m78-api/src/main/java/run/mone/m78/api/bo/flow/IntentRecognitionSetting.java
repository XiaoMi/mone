package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 意图识别核心配置
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class IntentRecognitionSetting extends LLMBaseSetting implements Serializable {

    private static final long serialVersionUID = 1102544143508934316L;

    private String promptContent;

    /**
     *
     * if从0开始计数
     * else用-1表示
     *
     * e.g.
     * [{0:"售前问题"},{1:"售后问题"},{-1:"其他"}]
     */
    private List<Map<String, String>> intentMatch;

}
