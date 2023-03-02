package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.service.impl.MiLogToolServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;

import static com.xiaomi.mone.log.common.Constant.SUCCESS_MESSAGE;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/5 10:54
 */
@Controller
public class MiLogToolController {

    @Resource
    private MiLogToolServiceImpl miLogToolService;

    @RequestMapping(path = "/milog/tool/send/msg", method = "get")
    public Result<String> sendLokiMsg(@RequestParam(value = "tailId") Long tailId) {
        return miLogToolService.sendLokiMsg(tailId);
    }

    /**
     * 修复alert表中没有milogAppId的问题
     *
     * @return
     */
    @RequestMapping(path = "/milog/tool/fix/alert/milogAppId", method = "get")
    public Result<String> fixAlertAppId() {
        miLogToolService.fixAlertAppId();
        return Result.success("success fixAlertAppId ");
    }

    /**
     * 修复alert表中没有tailId的问题
     *
     * @return
     */
    @RequestMapping(path = "/milog/tool/fix/alert/tailId", method = "get")
    public Result<String> fixMilogAlertTailId() {
        miLogToolService.fixMilogAlertTailId();
        return Result.success("success fixMilogAlertTailId");
    }

    /**
     * 修复alert表中没有tailId的问题
     *
     * @return
     */
    @RequestMapping(path = "/milog/tool/fix/resource/label", method = "GET")
    public Result<String> fixResourceLabel() {
        return Result.success(miLogToolService.fixResourceLabel());
    }

    /**
     * 修复历史的store的mqResourceId
     */
    @RequestMapping(path = "/milog/tool/fix/store/mq_resource_id", method = "GET")
    public Result<String> fixLogStoreMqResourceId(@RequestParam(value = "storeId") Long storeId) {
        return Result.success(miLogToolService.fixLogStoreMqResourceId(storeId));
    }

    /**
     * 修复es的信息变化了重新写入nacos
     *
     * @param spaceId
     * @return
     */
    @RequestMapping(path = "/milog/tool/fix/es/nacos", method = "GET")
    public Result<String> fixNacosEsInfo(@RequestParam(value = "spaceId") Long spaceId) {
        return Result.success(miLogToolService.fixNacosEsInfo(spaceId));
    }

    /**
     * copy该store下的mis应用且部署方式为物理机的tail到新的store中
     *
     * @param targetStoreId
     * @param sourceStoreId
     * @return
     */
    @RequestMapping(path = "/milog/tool/multi/machine/tail/batch", method = "GET")
    public Result<String> batchCopyMultiMachineTail(@RequestParam("targetStoreId") Long targetStoreId,
                                                    @RequestParam("sourceStoreId") Long sourceStoreId) {
        return Result.success(miLogToolService.batchCopyMultiMachineTail(targetStoreId, sourceStoreId));
    }

    @RequestMapping(path = "/milog/tool/fix/tail/app", method = "GET")
    public Result<String> fixLogTailLogAppId(@RequestParam("targetStoreId") String appName) {
        miLogToolService.fixLogTailLogAppId(appName);
        return Result.success(SUCCESS_MESSAGE);
    }


}
