package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.mone.log.manager.service.impl.EsIndexTemplateServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogTemplateServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class LogTemplateController {

    @Resource
    private LogTemplateServiceImpl logTemplateService;

    @RequestMapping(path = "/log/logTemplate/list", method = "get")
    public Result<List<LogTemplateDTO>> getLogTemplateList(@RequestParam(value = "area") String area) {
        return logTemplateService.getLogTemplateList(area);
    }

    @RequestMapping(path = "/log/logTemplate", method = "get")
    public Result<LogTemplateDetailDTO> getLogTemplate(@RequestParam(value = "logTemplateId") long logTemplateId) {
        return logTemplateService.getLogTemplateById(logTemplateId);
    }
}
