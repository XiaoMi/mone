package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LogTail {
    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private LogTailServiceImpl logTailService;

    public void handleStoreTail(Long storeId) {
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryTailsByStoreId(storeId);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            milogLogtailDos.forEach(milogLogtailDo -> {
                logTailService.updateSendMsg(milogLogtailDo, milogLogtailDo.getIps());
            });
        }
    }
}
