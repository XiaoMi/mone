package run.mone.m78.api.bo.user;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.api.bo.model.ModelConfig;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/1/24 11:54
 */
@Data
@Builder
public class UserConfig implements Serializable {


    @Builder.Default
    private Integer id = -1;

    private ModelConfig modelConfig;


    public String getTranslateModel(String defaultValue) {
        return StringUtils.isNotBlank(modelConfig.getTranslateModel())
                ? modelConfig.getTranslateModel()
                : defaultValue;
    }

    public String getCodeModel(String defaultValue) {
        return StringUtils.isNotBlank(modelConfig.getCodeModel())
                ? modelConfig.getCodeModel()
                : defaultValue;
    }

    public String getChatModel(String defaultValue) {
        return StringUtils.isNotBlank(modelConfig.getChatModel())
                ? modelConfig.getChatModel()
                : defaultValue;
    }

    public String getDocumentModel(String defaultValue) {
        return StringUtils.isNotBlank(modelConfig.getDocumentModel())
                ? modelConfig.getDocumentModel()
                : defaultValue;
    }

}
