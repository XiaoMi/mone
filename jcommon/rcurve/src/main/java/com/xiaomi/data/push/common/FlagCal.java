package com.xiaomi.data.push.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author goodjava@qq.com
 */
public class FlagCal {


    @Getter
    @Setter
    private int flag;

    public FlagCal() {
    }

    public FlagCal(int flag) {
        this.flag = flag;
    }

    /**
     * 添加一项或多项权限
     */
    public void enable(int permission) {
        flag |= permission;
    }

    /**
     * 删除一项或多项权限
     */
    public void disable(int permission) {
        flag &= ~permission;
    }

    /**
     * 是否拥某些权限
     */
    public boolean isAllow(int permission) {
        return (flag & permission) == permission;
    }

    public boolean isTrue(int permission) {
        return (flag & permission) == permission;
    }


}
