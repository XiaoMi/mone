package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.domain.EsIndexTemplate;
import com.xiaomi.mone.log.manager.domain.LogTemplate;
import com.xiaomi.mone.log.manager.model.vo.UpdateIndexTemplateCommand;
import com.xiaomi.mone.log.manager.service.EsIndexTemplateService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class EsIndexTemplateServiceImpl implements EsIndexTemplateService {
    @Resource
    private EsCluster esCluster;

    @Resource
    private EsIndexTemplate esIndexTemplate;

    @Resource
    private LogTemplate logTemplate;

    /**
     * 更新索引模板
     *
     * @param command
     * @return
     */
    @Override
    public boolean updateIndexTemplate(UpdateIndexTemplateCommand command) throws IOException {
        return false;
    }

    /**
     * 创建索引
     *
     * @param templateName
     * @return
     */
    @Override
    public boolean createIndex(String templateName) throws IOException {
        return esIndexTemplate.createIndex(templateName);
    }

}
