package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.manager.model.vo.UpdateIndexTemplateCommand;

import java.io.IOException;

public interface EsIndexTemplateService {

    /**
     * 更新索引模板
     *
     * @param updateIndexTemplateCommand
     * @return
     */
    public boolean updateIndexTemplate(UpdateIndexTemplateCommand updateIndexTemplateCommand) throws IOException;

    /**
     * 创建索引
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    boolean createIndex(String indexName) throws IOException;

}
