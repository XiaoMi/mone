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

import com.dianping.cat.message.Transaction;

/**
 * @author zheng.xucn@outlook.com
 */
public class CatContext {

    private static ThreadLocal<CatContext> context = new ThreadLocal<CatContext>() {
        @Override
        protected CatContext initialValue() {
            return new CatContext();
        }
    };

    private Transaction transaction;

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public static CatContext getContext() {
        return context.get();
    }

    public void close() {
        context.remove();
    }
}
