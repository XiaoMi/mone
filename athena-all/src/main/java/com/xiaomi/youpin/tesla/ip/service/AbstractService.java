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

package com.xiaomi.youpin.tesla.ip.service;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.xiaomi.youpin.tesla.ip.common.Context;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/7 21:01
 */
public abstract class AbstractService {

    private AbstractService next;

    public abstract void execute(Context context, AnActionEvent e);


    public void setNext(AbstractService service) {
        this.next = service;
    }


    public void next(Context context, AnActionEvent e) {
        if (null != next) {
            this.next.execute(context, e);
        } else {
            //到尾部了
            context.setStatus(1);
        }
    }


}
