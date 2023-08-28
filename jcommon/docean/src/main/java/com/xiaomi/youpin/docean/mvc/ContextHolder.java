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

package com.xiaomi.youpin.docean.mvc;

import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.mvc.common.MvcConst;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class ContextHolder {

    private static ThreadLocal<ContextHolder> context = ThreadLocal.withInitial(() -> new ContextHolder());

    private MvcContext mvcContext;

    private static class VirtualThreadContextHolder extends ContextHolder {
        @Override
        public MvcContext get() {
            return MvcConst.MVC_CONTEXT.get();
        }
    }

    private static VirtualThreadContextHolder virtualThreadContextHolder = new VirtualThreadContextHolder();


    public void set(MvcContext mvcContext) {
        this.mvcContext = mvcContext;
    }

    public MvcContext get() {
        return this.mvcContext;
    }

    private static ContextHolder getContext0() {
        return context.get();
    }

    public static ContextHolder getContext() {
        boolean virtualThread = Mvc.ins().getMvcConfig().isVirtualThread();
        if (virtualThread) {
            return virtualThreadContextHolder;
        }
        return getContext0();
    }

    public void close() {
        context.remove();
    }

}
