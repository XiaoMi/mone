package run.mone.m78.api.bo.multiModal;

import lombok.Data;

/**
 * @author wmin
 * @date 2024/7/25
 */
@Data
public class MultiModalBaseSetting {

    /**
     * 用户输入的描述
     */
    private String desc;

    /**
     * 结果数量
     */
    private int rstCount;

}
