package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MilogDictionaryParam;
import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;
import com.xiaomi.mone.log.manager.service.impl.MilogDictionaryServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/26 15:29
 */
@Controller
public class MilogDictionaryController {

    @Resource
    private MilogDictionaryServiceImpl milogDictionaryService;

    @RequestMapping(path = "/milog/dictionary/list", method = "post")
    public Result<Map<Integer, List<DictionaryDTO<?>>>> queryDictionaryList(MilogDictionaryParam codes) {
        return milogDictionaryService.queryDictionaryList(codes);
    }


    @RequestMapping(path = "/milog/sync/mis/app", method = "get")
    public Result<String> synchronousMisApp(@RequestParam("serviceName") String serviceName) {
        return milogDictionaryService.synchronousMisApp(serviceName);
    }

    @RequestMapping(path = "/milog/sync/radar/app", method = "get")
    public Result<String> synchronousRadarApp(@RequestParam("serviceName") String serviceName) {
        return milogDictionaryService.synchronousRadarApp(serviceName);
    }

    @RequestMapping(path = "/milog/down/file", method = "get")
    public Result<String> downLoadFile() {
        return milogDictionaryService.downLoadFile();
    }


    @RequestMapping(path = "/milog/fix/tail/milog/appId", method = "get")
    public Result<String> fixLogTailMilogAppId(@RequestParam("appName") String appName) {
        return milogDictionaryService.fixLogTailMilogAppId(appName);
    }
}
