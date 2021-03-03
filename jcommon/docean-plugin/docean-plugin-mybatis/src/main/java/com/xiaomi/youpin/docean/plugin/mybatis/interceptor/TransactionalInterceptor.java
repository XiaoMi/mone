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

package com.xiaomi.youpin.docean.plugin.mybatis.interceptor;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.plugin.cat.CatInterceptor;
import com.xiaomi.youpin.docean.plugin.mybatis.MybatisTransaction;
import com.xiaomi.youpin.docean.plugin.mybatis.Transactional;
import com.xiaomi.youpin.docean.plugin.mybatis.TransactionalContext;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class TransactionalInterceptor extends CatInterceptor {

    @Override
    public void before(AopContext context, Method method, Object[] args) {
        super.before(context, method, args);
        TransactionalContext.getContext().set(new MybatisTransaction(null, false));
    }


    @SneakyThrows
    @Override
    public Object after(AopContext context, Method method, Object res) {
        MybatisTransaction transaction = TransactionalContext.getContext().get();
        try {
            Object r = super.after(context, method, res);
            transaction.commit();
            return r;
        } finally {
            transaction.close();
            TransactionalContext.getContext().close();
        }
    }


    @SneakyThrows
    @Override
    public void exception(AopContext context, Method method, Throwable ex) {
        super.exception(context, method, ex);
        MybatisTransaction transaction = TransactionalContext.getContext().get();
        try {
            transaction.rollback();
        } finally {
            transaction.close();
            TransactionalContext.getContext().close();
        }
    }

    @Override
    public boolean needEnhance(Method method) {
        Transactional tr = method.getAnnotation(Transactional.class);
        return tr.type().equals("mybatis");
    }
}
