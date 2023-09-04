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

import com.xiaomi.mone.log.manager.model.pojo.MilogStoreSpaceAuth;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.collections.CollectionUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.util.List;

import static com.xiaomi.mone.log.common.Constant.EQUAL_OPERATE;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/7/14 16:13
 */
@Service
public class MilogStoreSpaceAuthDao {

    @Resource
    private NutDao dao;

    public MilogStoreSpaceAuth queryByStoreSpace(Long storeId, Long spaceId) {
        Cnd cnd = Cnd.NEW();
        if (null != storeId) {
            cnd.where().and("store_id", EQUAL_OPERATE, storeId);
        }
        if (null != spaceId) {
            cnd.where().and("space_id", EQUAL_OPERATE, spaceId);
        }
        List<MilogStoreSpaceAuth> milogStoreSpaceAuths = dao.query(MilogStoreSpaceAuth.class, cnd);
        if (CollectionUtils.isNotEmpty(milogStoreSpaceAuths)) {
            return milogStoreSpaceAuths.get(milogStoreSpaceAuths.size() - 1);
        }
        return null;
    }

    public void add(MilogStoreSpaceAuth storeSpaceAuth) {
        dao.insert(storeSpaceAuth);
    }

    public int update(MilogStoreSpaceAuth milogStoreSpaceAuth) {
        return dao.update(milogStoreSpaceAuth);
    }

    public List<MilogStoreSpaceAuth> queryStoreIdsBySpaceId(Long spaceId) {
        return dao.query(MilogStoreSpaceAuth.class, Cnd.where("space_id", EQUAL_OPERATE, spaceId).orderBy("ctime", "desc"));
    }

    public List<MilogStoreSpaceAuth> queryBySpaceId(List<Long> spaceIdList) {
        return dao.query(MilogStoreSpaceAuth.class, Cnd.where("space_id", "in", spaceIdList).orderBy("ctime", "desc"));
    }
}
