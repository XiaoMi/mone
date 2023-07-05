/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.dao;

import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;

/**
 * @author zhangping17
 * @date 2021-10-25
 */
@Service
@Slf4j
public class MilogLockDao {

    @Resource
    private NutDao dao;

    public int updateLock(String code, int status){
        Cnd cnd = Cnd.where("code", "=", code);
        if (status == 0){
            cnd = cnd.and("status", "=", 1);
        } else {
            cnd = cnd.and("status", "=", 0);
        }
        int res = dao.update("milog_lock", Chain.make("status", status).add("utime", System.currentTimeMillis()), cnd);
        return res;
    }
}
