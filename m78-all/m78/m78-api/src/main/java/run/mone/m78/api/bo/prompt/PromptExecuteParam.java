package run.mone.m78.api.bo.prompt;

import lombok.Data;
import run.mone.m78.api.constant.PromptActionTypeConstant;

import java.util.Map;

/**
 * @author wmin
 * @date 2024/2/23
 */
@Data
public class PromptExecuteParam {

    private String user;

    private String uuid;

    /**
     * @see PromptActionTypeConstant
     */
    private String action;

    private Map<String, String> params;

}
