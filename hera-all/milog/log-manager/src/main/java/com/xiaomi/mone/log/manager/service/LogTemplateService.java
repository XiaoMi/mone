package com.xiaomi.mone.log.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;

import java.io.IOException;
import java.util.List;

public interface LogTemplateService extends IService<MilogLogTemplateDO> {
    /**
     * 日志模板列表
     *
     * @return
     */
    Result<List<LogTemplateDTO>> getLogTemplateList(String area);

    /**
     * 获取日志模板
     *
     * @param logTemplateId
     * @return
     * @throws IOException
     */
    Result<LogTemplateDetailDTO> getLogTemplateById(long logTemplateId);
}
