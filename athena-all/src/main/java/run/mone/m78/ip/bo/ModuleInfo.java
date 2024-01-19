package run.mone.m78.ip.bo;

import lombok.Builder;

/**
 * @author goodjava@qq.com
 * @date 2023/6/19 21:50
 */
@Builder
public class ModuleInfo implements PsiInfo{

    private String name;

    private boolean hidden;;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public void hidden(boolean hidden) {
        this.hidden = hidden;
    }
}
