package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.service.impl.MilogStreamServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/27 17:24
 */
@Controller
public class MilogStreamController {

    @Resource
    private MilogStreamServiceImpl milogStreamService;

    @RequestMapping(path = "/milog/execute/sql/test", method = "get")
    public Result executeSql(@RequestParam(value = "sql") String sql) {
        milogStreamService.executeSql(sql);
        return Result.success();
    }

    /**
     * 配置下发给stream
     *
     * @return
     */
    @RequestMapping(path = "/milog/stream/config/issue", method = "get")
    public Result<String> configIssueStream(@RequestParam(value = "ip") String ip) {
        return milogStreamService.configIssueStream(ip);
    }

}
