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
