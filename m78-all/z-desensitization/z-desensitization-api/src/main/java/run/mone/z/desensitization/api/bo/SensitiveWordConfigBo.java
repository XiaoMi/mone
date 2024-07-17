package run.mone.z.desensitization.api.bo;

import run.mone.z.desensitization.api.common.SensitiveWordTypeEnum;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2023/11/14
 */
public class SensitiveWordConfigBo implements Serializable {
    /**
     * @see SensitiveWordTypeEnum
     */
    private Integer type;
    private String content;
    private Boolean isRegexMatch;
    private Boolean isCaseSensitive;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRegexMatch() {
        return isRegexMatch;
    }

    public void setRegexMatch(Boolean regexMatch) {
        isRegexMatch = regexMatch;
    }

    public Boolean getCaseSensitive() {
        return isCaseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        isCaseSensitive = caseSensitive;
    }
}
