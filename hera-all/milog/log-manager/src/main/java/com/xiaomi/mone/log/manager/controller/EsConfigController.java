package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.manager.model.vo.UpdateIndexTemplateCommand;
import com.xiaomi.mone.log.manager.service.impl.EsIndexTemplateServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
public class EsConfigController {
    @Resource
    private EsIndexTemplateServiceImpl esIndexTemplateService;

    @RequestMapping(path = "/es/updateIndexTemplate", method = "get")
    public Boolean updateIndexTemplate(@RequestParam(value = "updateIndexTemplateCommand") UpdateIndexTemplateCommand updateIndexTemplateCommand) throws IOException {
        return esIndexTemplateService.updateIndexTemplate(updateIndexTemplateCommand);
    }

    @RequestMapping(path = "/es/createIndex", method = "get")
    public Boolean createIndex(@RequestParam(value = "templateName") String templateName) throws IOException {
        return esIndexTemplateService.createIndex(templateName);
    }
}
