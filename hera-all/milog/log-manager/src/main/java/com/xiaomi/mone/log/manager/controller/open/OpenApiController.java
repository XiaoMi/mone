package com.xiaomi.mone.log.manager.controller.open;

import com.xiaomi.mone.log.api.model.bo.MiLogMoneTransfer;
import com.xiaomi.mone.log.api.model.vo.MiLogMoneEnv;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.AccessMilogParam;
import com.xiaomi.mone.log.manager.model.dto.MilogAppOpenVo;
import com.xiaomi.mone.log.manager.model.vo.AccessMiLogVo;
import com.xiaomi.mone.log.manager.model.vo.LogPathTopicVo;
import com.xiaomi.mone.log.manager.service.impl.MilogAppTopicServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.MilogOpenServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/8/17 15:38
 */
@Controller
public class OpenApiController {

    @Resource
    private MilogAppTopicServiceImpl milogAppTopicService;

    @Resource
    private MilogOpenServiceImpl milogOpenService;

    @RequestMapping(path = "/open/api/milog/app/list", method = "get")
    public Result<List<MilogAppOpenVo>> queryAllMilogAppList() {
        return milogAppTopicService.queryAllMilogAppList();
    }

    /**
     * @param milogAppId
     * @return
     */
    @RequestMapping(path = "/open/api/milog/config/log/path", method = "get")
    public Result<List<LogPathTopicVo>> queryTopicConfigByAppId(@RequestParam(value = "milogAppId") Long milogAppId) {
        return Result.success(milogAppTopicService.queryTopicConfigByAppId(milogAppId));
    }

    /**
     * mione迁移，milog无感知
     */
    @RequestMapping(path = "/open/api/milog/env/transfer", method = "POST")
    public Result<MiLogMoneTransfer> ypMoneEnvTransfer(@RequestParam(value = "logMoneEnv")
                                                               MiLogMoneEnv logMoneEnv) {
        return Result.success(milogOpenService.ypMoneEnvTransfer(logMoneEnv));
    }


    /**
     * mifaas接入milog
     *
     * @param milogParam
     * @return
     */
    @RequestMapping(path = "/open/api/milog/access/mifass")
    public Result<AccessMiLogVo> accessToMilog(@RequestParam("param") AccessMilogParam milogParam) {
        return milogAppTopicService.accessToMilog(milogParam);
    }
}
