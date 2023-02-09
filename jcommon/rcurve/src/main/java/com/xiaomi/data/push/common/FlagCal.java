/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
