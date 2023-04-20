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
