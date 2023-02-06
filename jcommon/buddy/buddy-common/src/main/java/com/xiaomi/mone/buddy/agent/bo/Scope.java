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

import java.util.UUID;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/7 23:41
 */
public class Scope {

    public static void start() {
        Span span = Context.getContext().getSpan();
        if (null == span) {
            span = new Span();
            span.setTraceId(UUID.randomUUID().toString());
            Context.getContext().setSpan(span);
        }
    }

    public static void close() {
        Context.removeContext();
    }

}
