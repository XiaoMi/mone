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

package com.xiaomi.mone.buddy.agent.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/7 23:41
 */
public class Context {

    @Getter
    @Setter
    private Span span;

    private static final ThreadLocal<Context> tr = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new Context();
        }
    };

    public static Context getContext() {
        return tr.get();
    }


    public static void removeContext() {
        tr.remove();
    }






}
