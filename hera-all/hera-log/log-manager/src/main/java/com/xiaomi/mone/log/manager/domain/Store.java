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
package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogStoreSpaceAuthDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogStoreSpaceAuth;
import com.xiaomi.youpin.docean.anno.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class Store {
    @Resource
    private MilogLogstoreDao logstoreDao;

    @Resource
    private MilogStoreSpaceAuthDao storeSpaceAuthDao;

    public List<MilogLogStoreDO> getStoreList(List<Long> spaceIdList) {
        List<MilogLogStoreDO> storeList = logstoreDao.getMilogLogstoreBySpaceId(spaceIdList);
        Set<Long> storeIdSet = storeList.stream().map(MilogLogStoreDO::getId).collect(Collectors.toSet());
        List<MilogStoreSpaceAuth> storeAuthList = storeSpaceAuthDao.queryBySpaceId(spaceIdList);
        if (storeAuthList != null && !storeAuthList.isEmpty()) {
            List<Long> authStoreIdList = storeAuthList.stream().filter(auth -> !storeIdSet.contains(auth.getStoreId())).map(MilogStoreSpaceAuth::getStoreId).collect(Collectors.toList());
            List<MilogLogStoreDO> authStoreList = logstoreDao.queryByIds(authStoreIdList);
            storeList.addAll(authStoreList);
        }
        return storeList;
    }
}
