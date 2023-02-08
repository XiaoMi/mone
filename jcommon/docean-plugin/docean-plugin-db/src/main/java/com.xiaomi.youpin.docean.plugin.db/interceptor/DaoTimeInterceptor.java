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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.lang.Stopwatch;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/28 20:07
 */
@Slf4j
public class DaoTimeInterceptor implements DaoInterceptor {

    @Setter
    private boolean printSql = true;

    public DaoTimeInterceptor() {
    }

    public DaoTimeInterceptor(String printSql) {
        this.printSql = Boolean.parseBoolean(printSql);
    }

    public DaoTimeInterceptor(boolean printSql) {
        this.printSql = printSql;
    }

    @Override
    public void filter(DaoInterceptorChain chain) throws DaoException {
        Stopwatch sw = Stopwatch.begin();
        try {
            chain.doChain();
        } finally {
            sw.stop();
            if (printSql) {
                log.info("time={}ms, sql={}",
                        sw.getDuration(),
                        chain.getDaoStatement().toString());
            }

        }
    }
}
