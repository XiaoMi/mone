package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class LLMSetting extends LLMBaseSetting implements Serializable {

    private String temperature;

    private String promptContent;

    private String timeout;

    /**
     * 每批次时间间隔，单位毫秒
     */
    private String batchTimeInterval;
}
