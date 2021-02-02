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

package com.xiaomi.youpin.docean.plugin.cat;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import java.lang.reflect.Method;

/**
 * @author zheng.xucn@outlook.com
 */
public class CatInterceptor extends EnhanceInterceptor {

    private static final String CAT_TYPE = "METHODS";

    @Override
    public void before(AopContext aopContext, Method method, Object[] args) {
        Transaction t = Cat.newTransaction(CAT_TYPE, method.getName());
        t.setStatus(Transaction.SUCCESS);
        CatContext.getContext().setTransaction(t);
    }

    @Override
    public Object after(AopContext context, Method method, Object res) {
        Transaction t = CatContext.getContext().getTransaction();
        t.complete();
        CatContext.getContext().close();
        return res;
    }

    @Override
    public void exception(AopContext context, Method method, Throwable ex) {
        Transaction t = CatContext.getContext().getTransaction();
        t.setStatus(ex);
        t.complete();
        CatContext.getContext().close();
    }

}
