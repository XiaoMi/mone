package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MilogDictionaryParam;
import com.xiaomi.mone.log.manager.model.dto.DictionaryDTO;
import com.xiaomi.mone.log.manager.service.impl.MilogDictionaryServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;

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

}
