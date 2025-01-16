package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * 风格重绘
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class StyleRepaintSetting extends LLMBaseSetting implements Serializable {

    private Integer styleIndex;//$$TY_STYLE_INDEX$$

    private String baseImageUrl;//$$TY_BASE_IMAGE_URL$$

}
