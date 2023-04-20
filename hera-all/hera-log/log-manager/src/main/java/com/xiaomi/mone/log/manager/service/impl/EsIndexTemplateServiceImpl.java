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
        // 查找模板
//        GetIndexTemplatesRequest getIndexTemplatesRequest = new GetIndexTemplatesRequest(command.getIndexTemplateName());
//        EsService esService = esCluster.getEsService(null);
//        List<IndexTemplateMetaData> templateList = esService.getIndexTemplates(getIndexTemplatesRequest);
//        // 查不到或查到多个模板，返回false
//        if (templateList == null || templateList.size() != 1) {
//            return false;
//        }
//        IndexTemplateMetaData template = templateList.get(0);
//        PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest(command.getIndexTemplateName());
//        // 匹配名称
//        putIndexTemplateRequest.patterns(Arrays.asList(command.getIndexTemplateName() + "*"));
//        // 设置别名别名
//        putIndexTemplateRequest.alias(new Alias(command.getIndexTemplateName()));
//        // 优先级
//        putIndexTemplateRequest.order(20);
//        // 版本号
//        putIndexTemplateRequest.version(template.version() + 1);
//        // 模板设置
//        esIndexTemplate.buildSetting(putIndexTemplateRequest, command);
//        // 模板映射
//        esIndexTemplate.buildMapping(putIndexTemplateRequest, command);
//        return esService.createIndexTemplate(putIndexTemplateRequest);
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
