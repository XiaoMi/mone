package com.xiaomi.youpin.tesla.ip.bo;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/19 14:19
 */
public class MethodInfo implements PsiInfo, Serializable {

    private boolean hidden;

    private String name;

    public MethodInfo(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public void hidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
