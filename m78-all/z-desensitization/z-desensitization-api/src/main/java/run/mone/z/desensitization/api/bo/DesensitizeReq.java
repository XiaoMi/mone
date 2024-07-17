package run.mone.z.desensitization.api.bo;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2023/11/14
 */
public class DesensitizeReq implements Serializable {
    private String text;
    private List<SensitiveWordConfigBo> sensitiveWordConfigBo;
    private Boolean aiDesensitizeFlag;

    private Boolean needExtract;

    private String langType;

    private String username;

    public String getLangType() {
        return langType;
    }

    public void setLangType(String langType) {
        this.langType = langType;
    }

    public Boolean getNeedExtract() {
        return needExtract;
    }

    public void setNeedExtract(Boolean needExtract) {
        this.needExtract = needExtract;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<SensitiveWordConfigBo> getSensitiveWordConfigBo() {
        return sensitiveWordConfigBo;
    }

    public void setSensitiveWordConfigBo(List<SensitiveWordConfigBo> sensitiveWordConfigBo) {
        this.sensitiveWordConfigBo = sensitiveWordConfigBo;
    }

    public Boolean getAiDesensitizeFlag() {
        return aiDesensitizeFlag;
    }

    public void setAiDesensitizeFlag(Boolean aiDesensitizeFlag) {
        this.aiDesensitizeFlag = aiDesensitizeFlag;
    }
}
