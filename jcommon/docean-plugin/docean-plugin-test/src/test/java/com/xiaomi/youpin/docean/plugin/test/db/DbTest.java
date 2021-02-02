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

package com.xiaomi.youpin.docean.plugin.test.db;

import com.xiaomi.youpin.docean.plugin.test.common.DbUtils;
import org.junit.Test;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.NutTxDao;

import java.beans.PropertyVetoException;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
public class DbTest {


    @Test
    public void testDb() throws PropertyVetoException {
        NutDao dao = new NutDao(DbUtils.datasource());
        NutTxDao td = new NutTxDao(dao);

        td.beginRC();
        td.insert(new com.xiaomi.youpin.docean.plugin.test.bo.Test());
        td.commit();
    }
}
