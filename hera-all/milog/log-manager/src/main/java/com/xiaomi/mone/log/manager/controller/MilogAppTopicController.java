package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.AppTopicParam;
import com.xiaomi.mone.log.manager.model.dto.MapDTO;
import com.xiaomi.mone.log.manager.model.dto.MilogAppConfigTailDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.service.impl.MilogAppTopicServiceImpl;
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
 * @date 2021/7/27 11:19
 */
@Controller
@Deprecated
public class MilogAppTopicController {

    @Resource
    private MilogAppTopicServiceImpl milogAppTopicService;

    /**
     * 查询所有的app与topic列表
     *
     * @param param
     * @return
     */
    @RequestMapping(path = "/milog/app/topic/list")
    public Result<PageInfo<MilogAppConfigTailDTO>> queryAppTopicList(AppTopicParam param) {
        return milogAppTopicService.queryAppTopicList(param);
    }

    /**
     * 查询所有已经存在的topic
     *
     * @return
     */
    @RequestMapping(path = "/milog/topic/list", method = "get")
    public Result<List<MapDTO>> queryAllExistTopicList() {
        return milogAppTopicService.queryAllExistTopicList();
    }

    /**
     * 创建App对应的topic
     *
     * @param appId
     * @param appName
     * @return
     */
    @RequestMapping(path = "/milog/app/topic/create", method = "get")
    public Result<String> createTopic(@RequestParam(value = "appId") Long appId, @RequestParam(value = "appName") String appName) {
        return milogAppTopicService.createTopic(appId, appName);
    }

    /**
     * 重新选择对用的topic
     *
     * @param id         记录ID
     * @param existTopic 存在的topic name
     * @return
     */
    @RequestMapping(path = "/milog/app/topic/update", method = "get")
    public Result<String> updateExistsTopic(@RequestParam("id") Long id, @RequestParam("existTopic") String existTopic) {
        return milogAppTopicService.updateExistsTopic(id, existTopic);
    }

    @RequestMapping(path = "/milog/app/topic/del", method = "get")
    public Result<String> delTopicRecord(@RequestParam("appId") Long appId){
        return milogAppTopicService.delTopicRecord(appId);
    }

    @RequestMapping(path = "/milog/app/topic/del/all", method = "get")
    public Result<String> delTopicRecordAll(){
        return milogAppTopicService.delTopicRecordAll();
    }
}
