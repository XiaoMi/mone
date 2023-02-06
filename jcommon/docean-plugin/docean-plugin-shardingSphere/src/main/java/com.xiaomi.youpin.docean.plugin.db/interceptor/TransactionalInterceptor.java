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

package com.xiaomi.youpin.docean.plugin.db.interceptor;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.exception.DoceanException;
import lombok.SneakyThrows;
import org.nutz.trans.NutTransaction;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class TransactionalInterceptor extends EnhanceInterceptor {

    @Override
    public void before(AopContext context, Method method, Object[] args) {
        NutTransaction transaction = new NutTransaction();
        transaction.setLevel(2);
        Trans.set(transaction);
        Trans.setCount(0);
        try {
            Trans.begin();
        } catch (Exception e) {
            throw new DoceanException(e);
        }
    }


    @SneakyThrows
    @Override
    public Object after(AopContext context, Method method, Object res) {
        try {
            Object r = super.after(context, method, res);
            Trans.commit();
            return r;
        } finally {
//            Trans.close();
        }
    }


    @SneakyThrows
    @Override
    public void exception(AopContext context, Method method, Throwable ex) {
        try {
            Trans.rollback();
        } finally {
//            Trans.close();
        }
    }

}
