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
