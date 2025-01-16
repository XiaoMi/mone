package run.mone.m78.api.bo.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author wmin
 * @date 2024/1/24
 */
@Builder
@Data
public class ModelConfig {
    private String chatModel;
    private String codeModel;
    private String documentModel;
    private String translateModel;
}
