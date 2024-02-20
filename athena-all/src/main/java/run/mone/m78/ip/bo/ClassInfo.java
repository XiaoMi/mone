package run.mone.m78.ip.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/19 14:11
 */
@Data
@Builder
public class ClassInfo implements Serializable, PsiInfo {

    private String className;

    private String moduleName;

    private boolean hidden;


    @Override
    public String toString() {
        if (null == moduleName) {
            return className;
        }
        return className + "  (" + moduleName + ")";
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public void setName(String name) {
        this.className = name;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void hidden(boolean hidden) {
        this.hidden = hidden;
    }
}
