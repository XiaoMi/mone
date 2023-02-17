package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.log.manager.model.bo.BatchQueryParam;
import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.bo.CreateOrUpdateSpaceCmd;
import com.xiaomi.mone.log.manager.model.bo.MlogParseParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.CreateOrUpdateLogStoreCmd;
import com.xiaomi.mone.log.manager.model.vo.QuickQueryVO;
import com.xiaomi.mone.log.manager.service.HeralogHomePageService;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogSpaceServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogProcessServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogStoreServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.mvc.MvcContext;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class HeraLogConfigController {

    @Resource
    private LogSpaceServiceImpl logSpaceService;

    @Resource
    private LogStoreServiceImpl logStoreService;

    @Resource
    private LogTailServiceImpl logTailService;

    @Resource
    private HeralogHomePageService heralogHomePageService;

    @Resource
    private LogProcessServiceImpl logProcessService;

    @Resource
    Tpc tpc;

    /**
     * ********* milogSpace **********
     */

    @RequestMapping(path = "/milog/space/new")
    public Result<String> newMilogSpace(CreateOrUpdateSpaceCmd cmd) {
        return logSpaceService.newMilogSpace(cmd);
    }

    @RequestMapping(path = "/milog/space/getbyid", method = "get")
    public Result<MilogSpaceDTO> getMilogSpaceById(@RequestParam("id") Long id) {
        return logSpaceService.getMilogSpaceById(id);
    }

    @RequestMapping(path = "/milog/space/getall", method = "get")
    public Result<List<MapDTO<String, Long>>> getMilogSpaces() {
        return logSpaceService.getMilogSpaces();
    }

    @RequestMapping(path = "/milog/space/getbypage", method = "get")
    public Result<PageInfo<MilogSpaceDTO>> getMilogSpaceByPage(MvcContext context, @RequestParam(value = "spaceName") String spaceName,
                                                               @RequestParam("page") Integer page,
                                                               @RequestParam("pageSize") Integer pageSize) {
        return logSpaceService.getMilogSpaceByPage(spaceName, page, pageSize);
    }

    @RequestMapping(path = "/milog/space/delete", method = "get")
    public Result<String> deleteMilogSpace(@RequestParam("id") Long id) {
        return logSpaceService.deleteMilogSpace(id);
    }

    @RequestMapping(path = "/milog/space/update")
    public Result<String> updateMilogSpace(@RequestParam("param") CreateOrUpdateSpaceCmd param) {
        return logSpaceService.updateMilogSpace(param);
    }

    /**
     * ********* milogLogstore **********
     */

    @RequestMapping(path = "/milog/store/new")
    public Result<String> newLogStore(@RequestParam("param") CreateOrUpdateLogStoreCmd param) {
        return logStoreService.newLogStore(param);
    }

    @RequestMapping(path = "/milog/store/getbyid", method = "get")
    public Result<LogStoreDTO> getLogStoreById(@RequestParam("id") Long id) {
        return logStoreService.getLogStoreById(id);
    }

    @RequestMapping(path = "/milog/store/getbyids")
    public Result<List<MilogLogStoreDO>> getLogStoreByIds(BatchQueryParam param) throws IOException {
        return logStoreService.getLogStoreByIds(param.getIds());
    }

    @RequestMapping(path = "/milog/store/getbypage", method = "get")
    public Result<Map<String, Object>> getLogStoreByPage(@RequestParam(value = "logstoreName") String logstoreName,
                                                         @RequestParam("spaceId") Long spaceId,
                                                         @RequestParam("page") int page,
                                                         @RequestParam("pageSize") int pageSize) {
        return logStoreService.getLogStoreByPage(logstoreName, spaceId, page, pageSize);
    }

    @RequestMapping(path = "/milog/store/getbyspace", method = "get")
    public Result<List<MapDTO<String, Long>>> getLogStoreBySpaceId(@RequestParam("spaceId") Long spaceId) {
        return logStoreService.getLogStoreBySpaceId(spaceId);
    }

    @RequestMapping(path = "/milog/store/getall", method = "get")
    public Result<Map<String, Object>> getAllLogStore() {
        return logStoreService.getAllLogStore();
    }

    @RequestMapping(path = "/milog/store/delete", method = "get")
    public Result<Void> deleteLogStore(@RequestParam("id") Long id) {
        return logStoreService.deleteLogStore(id);
    }

    @RequestMapping(path = "/milog/store/log/process", method = "get")
    public Result<List<TailLogProcessDTO>> storeLogProcess(@RequestParam("type") String type, @RequestParam("value") String value) {
        return logProcessService.getStoreLogProcess(type, value);
    }

    @RequestMapping(path = "/milog/store/ips", method = "get")
    public Result<List<Map<String, String>>> getStoreIps(@RequestParam("storeId") Long storeId) {
        return logStoreService.getStoreIps(storeId);
    }

    /**
     * ********* milogLogtail **********
     */
    @RequestMapping(path = "/milog/tail/new")
    public Result<MilogTailDTO> newMilogLogtail(@RequestParam("param") MilogLogtailParam param) {
        return logTailService.newMilogLogTail(param);
    }

    @RequestMapping(path = "/milog/tail/getbyid", method = "get")
    public Result<MilogTailDTO> getMilogLogtailById(@RequestParam("id") Long id) {
        return logTailService.getMilogLogtailById(id);
    }

    @RequestMapping(path = "/milog/tail/getbyids", method = "get")
    public Result<List<MilogTailDTO>> getMilogLogtailByIds(@RequestParam("ids") List<Long> ids) {
        return logTailService.getMilogLogtailByIds(ids);
    }

    @RequestMapping(path = "/milog/tail/getbyStoreId", method = "get")
    public Result<List<MilogTailDTO>> getTailByStoreId(@RequestParam("storeId") Long storeId) {
        return logTailService.getTailByStoreId(storeId);
    }

    @RequestMapping(path = "/milog/tail/getbypage", method = "get")
    public Result<Map<String, Object>> getMilogLogtailByPage(@RequestParam("storeId") Long storeId,
                                                             @RequestParam("page") int page,
                                                             @RequestParam("pageSize") int pageSize) throws IOException {
        return logTailService.getMilogLogBypage(storeId, page, pageSize);
    }

    @RequestMapping(path = "/milog/tail/getcntbystoreid", method = "get")
    public Result<Map<String, Object>> getMilogLogTailCountByStoreId(@RequestParam("storeId") Long storeId) {
        return logTailService.getLogTailCountByStoreId(storeId);
    }

    @RequestMapping(path = "/milog/tail/update")
    public Result<Void> updateMilogLogTail(MilogLogtailParam param) {
        return logTailService.updateMilogLogTail(param);
    }

    @RequestMapping(path = "/milog/tail/delete", method = "get")
    public Result<Void> deleteMilogLogTail(@RequestParam(value = "id") Long id) {
        return logTailService.deleteMilogLogTail(id);
    }

    @RequestMapping(path = "/milog/tail/gettailname", method = "get")
    public Result<List<String>> getTailNames(@RequestParam("id") Long id,
                                             @RequestParam("appType") Integer appType,
                                             @RequestParam("tail") String tail) {
        return logTailService.getTailNamesBystoreId(tail, appType, id);
    }

    /**
     * TODO 收集速率字典（后期需要迁移到字典接口中）
     *
     * @return
     */
    @RequestMapping(path = "/milog/tail/tailrate", method = "get")
    public Result<List<MapDTO<String, String>>> tailRateLimie() {
        return logTailService.tailRatelimit();
    }

    /**
     * ********* other **********
     */

    @RequestMapping(path = "/milog/tail/getapp", method = "get")
    public Result<List<MapDTO>> getAppInfoByName(@RequestParam(value = "appName") String appName,
                                                 @RequestParam(value = "type") Integer type) {
        return logTailService.getAppInfoByName(appName, type);
    }

    /**
     * 查询该store下接入的所有应用
     *
     * @param storeId
     * @return
     */
    @RequestMapping(path = "/milog/tail/app/store/id", method = "get")
    public Result<List<MapDTO>> queryAppByStoreId(@RequestParam(value = "storeId") Long storeId) {
        return logTailService.queryAppByStoreId(storeId);
    }

    /**
     * 根据appId获取项目配置的所有环境信息
     *
     * @param milogAppId
     * @return
     */
    @RequestMapping(path = "/milog/project/env/appId", method = "get")
    public Result<List<MilogAppEnvDTO>> getEnInfosByAppId(@RequestParam(value = "milogAppId") Long milogAppId,
                                                          @RequestParam(value = "deployWay") Integer deployWay) {
        return logTailService.getEnInfosByAppId(milogAppId, deployWay);
    }

    /**
     * 获取mis应用的机房信息
     *
     * @param milogAppId
     * @return
     */
    @RequestMapping(path = "/milog/project/mis/zone/appId", method = "get")
    public Result<List<SimpleAppEnvDTO>> getRegionZonesByAppId(@RequestParam(value = "milogAppId") Long milogAppId,
                                                               @RequestParam(value = "machineRoom") String machineRoom) {
        return logTailService.getRegionZonesByAppId(milogAppId, machineRoom);
    }

    /**
     * 首页
     */
    @RequestMapping(path = "/milog/index/access", method = "get")
    public Result<Map<String, Object>> getAccess() {
        return heralogHomePageService.milogAccess();
    }

    @RequestMapping(path = "/milog/index/getunaccessapp", method = "get")
    public Result<List<UnAccessAppDTO>> getUnAccessApp() {
        return heralogHomePageService.unAccessAppList();
    }

    /**
     * 获取space下边的store列表
     *
     * @param spaceId
     * @return
     */
    @RequestMapping(path = "/milog/index/getspacetree", method = "get")
    public Result<List<MilogSpaceTreeDTO>> getMilogSpaceTree(@RequestParam(value = "spaceId") Long spaceId) {
        return heralogHomePageService.getMilogSpaceTree(spaceId);
    }

    @RequestMapping(path = "/milog/index/fastaccesslogpattern", method = "get")
    public Result<List<ValueDTO<String>>> fastAccessMiloglogPattern() {
        return heralogHomePageService.getMiloglogAccessPattern();
    }

    // 快速接入
    @RequestMapping(path = "/milog/index/fastaccess")
    public Result<Void> fastAccess() {
        return null;
    }


    @RequestMapping(path = "/milog/app/type/tail/storeId", method = "get")
    public Result<List<AppTypeTailDTO>> queryAppTailByStoreId(@RequestParam(value = "storeId") Long storeId) {
        return logTailService.queryAppTailByStoreId(storeId);
    }


    @RequestMapping(path = "/milog/logstore/region/nameEn", method = "get")
    public Result<List<MilogLogStoreDO>> queryLogStoreByRegionEn(@RequestParam(value = "nameEn") String nameEn) {
        return logTailService.queryLogStoreByRegionEn(nameEn);
    }

    /**
     * 测试解析规则
     *
     * @param mlogParseParam
     * @return
     */
    @RequestMapping(path = "/milog/logtail/parse/test")
    public Result<Object> parseScriptTest(@RequestParam(value = "param") MlogParseParam mlogParseParam) {
        return logTailService.parseScriptTest(mlogParseParam);
    }

    /**
     * 根据应用快速查询
     *
     * @param milogAppId
     * @return
     */
    @RequestMapping(path = "/milog/logtail/tail/quick/app", method = "get")
    public Result<List<QuickQueryVO>> quickQueryByApp(@RequestParam(value = "milogAppId") Long milogAppId) {
        return logTailService.quickQueryByApp(milogAppId);
    }

    /**
     * 查询当前创建用户所在部门的下的es索引列表
     *
     * @param regionCode
     * @param logTypeCode
     * @return
     */
    @RequestMapping(path = "/milog/store/es/index", method = "get")
    public Result<List<MenuDTO<Long, String>>> queryDeptExIndexList(@RequestParam(value = "regionCode") String regionCode,
                                                                    @RequestParam(value = "logTypeCode") Integer logTypeCode) {
        return logStoreService.queryDeptExIndexList(regionCode, logTypeCode);
    }
}
