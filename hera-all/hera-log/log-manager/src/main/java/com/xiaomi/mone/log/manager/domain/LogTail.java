package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.service.bind.LogTypeProcessor;
import com.xiaomi.mone.log.manager.service.bind.LogTypeProcessorFactory;
import com.xiaomi.mone.log.manager.service.extension.tail.TailExtensionService;
import com.xiaomi.mone.log.manager.service.extension.tail.TailExtensionServiceFactory;
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

    @Resource
    private MilogLogstoreDao logStoreDao;

    @Resource
    private MilogLogTemplateMapper milogLogTemplateMapper;

    @Resource
    private LogTypeProcessorFactory logTypeProcessorFactory;

    private LogTypeProcessor logTypeProcessor;

    private TailExtensionService tailExtensionService;

    public void init() {
        logTypeProcessorFactory.setMilogLogTemplateMapper(milogLogTemplateMapper);
        logTypeProcessor = logTypeProcessorFactory.getLogTypeProcessor();
        tailExtensionService = TailExtensionServiceFactory.getTailExtensionService();
    }

    public void handleStoreTail(Long storeId) {
        MilogLogStoreDO milogLogStoreDO = logStoreDao.queryById(storeId);

        boolean supportedConsume = logTypeProcessor.supportedConsume(milogLogStoreDO.getLogType());

        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryTailsByStoreId(storeId);
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            milogLogtailDos.forEach(milogLogtailDo -> {
                tailExtensionService.updateSendMsg(milogLogtailDo, milogLogtailDo.getIps(), supportedConsume);
            });
        }
    }
}
