package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.manager.service.impl.MiLogMetaManageServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;

@Controller
public class MiLogMetaManageController {
    @Resource
    private MiLogMetaManageServiceImpl miLogMetaManageService;

    @RequestMapping(path = "/milog/meta/query", method = "get")
    public LogCollectMeta queryLogCollectMeta(@RequestParam(value = "agentId") String agentId, @RequestParam(value = "agentIp") String agentIp) {
        return miLogMetaManageService.queryLogCollectMeta(agentId, agentIp);
    }

}
