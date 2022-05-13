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

package com.xiaomi.mone.buddy.agent.advice;

import com.xiaomi.mone.buddy.agent.bo.Context;
import com.xiaomi.mone.buddy.agent.bo.Scope;
import com.xiaomi.mone.buddy.agent.common.CallableWrapper;
import com.xiaomi.mone.buddy.agent.common.RunnableWrapper;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/7 23:40
 */
public class ExecuteAdvic {


    @Advice.OnMethodEnter
    public static void onMethodEnter(@Advice.This Object obj, @Advice.Origin Method method, @Advice.AllArguments(typing = Assigner.Typing.DYNAMIC) Object[] args, @Advice.Local("context") Context context, @Advice.Local("scope") Scope scope) {
        if (args.length == 1) {
            Object o = args[0];
            if (o instanceof Runnable) {
                Runnable r = (Runnable) o;
                RunnableWrapper w = new RunnableWrapper(r);
                args[0] = w;
            }
            if (o instanceof Callable) {
                Callable c = (Callable) o;
                CallableWrapper cw = new CallableWrapper(c);
                args[0] = cw;
            }
        }
    }


}
