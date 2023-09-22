/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.api.model.vo.TailLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.bo.BatchQueryParam;
import com.xiaomi.mone.log.manager.model.bo.LogTailParam;
import com.xiaomi.mone.log.manager.model.bo.MlogParseParam;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;
import com.xiaomi.mone.log.manager.model.vo.QuickQueryVO;
import com.xiaomi.mone.log.manager.service.HeralogHomePageService;
import com.xiaomi.mone.log.manager.service.impl.LogProcessServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogSpaceServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogStoreServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class MilogConfigController {

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

    /**
     * ********* milogSpace **********
     */

    @RequestMapping(path = "/milog/space/new")
    public Result<String> newMilogSpace(MilogSpaceParam cmd) {
        return logSpaceService.newMilogSpace(cmd);
    }

    @RequestMapping(path = "/milog/space/getbyid", method = "get")
    public Result<MilogSpaceDTO> getMilogSpaceById(@RequestParam("id") Long id) {
        return logSpaceService.getMilogSpaceById(id);
    }

    @RequestMapping(path = "/milog/space/getall", method = "get")
    public Result<List<MapDTO<String, Long>>> getMilogSpaces(@RequestParam("tenantId") Long tenantId) {
        return logSpaceService.getMilogSpaces(tenantId);
    }

    @RequestMapping(path = "/milog/space/getbypage", method = "get")
    public Result<PageInfo<MilogSpaceDTO>> getMilogSpaceByPage(@RequestParam(value = "spaceName") String spaceName,
                                                               @RequestParam("tenantId") Long tenantId,
                                                               @RequestParam("page") Integer page,
                                                               @RequestParam("pageSize") Integer pageSize) {
        return logSpaceService.getMilogSpaceByPage(spaceName, tenantId, page, pageSize);
    }

    @RequestMapping(path = "/milog/space/delete", method = "get")
    public Result<String> deleteMilogSpace(@RequestParam("id") Long id) {
        return logSpaceService.deleteMilogSpace(id);
    }

    @RequestMapping(path = "/milog/space/update")
    public Result<String> updateMilogSpace(@RequestParam("param") MilogSpaceParam param) {
        return logSpaceService.updateMilogSpace(param);
    }

    /**
     * ********* milogLogstore **********
     */

    @RequestMapping(path = "/milog/store/new")
    public Result<String> newLogStore(@RequestParam("param") LogStoreParam param) {
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

    /**
     * All collection information less than progress Ration progress
     *
     * @param progressRation progress 0.80
     * @return
     */
    @RequestMapping(path = "/milog/col/process/imperfect", method = "get")
    public Result<List<UpdateLogProcessCmd.CollectDetail>> getColProcessImperfect(@RequestParam("progressRation") Double progressRation) {
        return logProcessService.getColProcessImperfect(progressRation);
    }

    @RequestMapping(path = "/milog/store/ips", method = "get")
    public Result<List<Map<String, String>>> getStoreIps(@RequestParam("storeId") Long storeId) {
        return logStoreService.getStoreIps(storeId);
    }

    /**
     * ********* milogLogtail **********
     */
    @RequestMapping(path = "/milog/tail/new")
    public Result<LogTailDTO> newLogTail(@RequestParam("param") LogTailParam param) {
        return logTailService.newMilogLogTail(param);
    }

    @RequestMapping(path = "/milog/tail/getbyid", method = "get")
    public Result<LogTailDTO> getLogTailById(@RequestParam("id") Long id) {
        return logTailService.getMilogLogtailById(id);
    }

    @RequestMapping(path = "/milog/tail/getbyids", method = "get")
    public Result<List<LogTailDTO>> getLogTailByIds(@RequestParam("ids") List<Long> ids) {
        return logTailService.getMilogLogtailByIds(ids);
    }

    @RequestMapping(path = "/milog/tail/getbyStoreId", method = "get")
    public Result<List<LogTailDTO>> getTailByStoreId(@RequestParam("storeId") Long storeId) {
        return logTailService.getTailByStoreId(storeId);
    }

    @RequestMapping(path = "/milog/tail/getbypage", method = "get")
    public Result<Map<String, Object>> getLogTailByPage(@RequestParam("storeId") Long storeId,
                                                        @RequestParam("page") int page,
                                                        @RequestParam("pageSize") int pageSize) throws IOException {
        return logTailService.getMilogLogBypage(storeId, page, pageSize);
    }

    @RequestMapping(path = "/milog/tail/getcntbystoreid", method = "get")
    public Result<Map<String, Object>> getLogTailCountByStoreId(@RequestParam("storeId") Long storeId) {
        return logTailService.getLogTailCountByStoreId(storeId);
    }

    @RequestMapping(path = "/milog/tail/update")
    public Result<Void> updateLogTail(LogTailParam param) {
        return logTailService.updateMilogLogTail(param);
    }

    @RequestMapping(path = "/milog/tail/delete", method = "get")
    public Result<Void> deleteLogTail(@RequestParam(value = "id") Long id) {
        return logTailService.deleteLogTail(id);
    }

    @RequestMapping(path = "/milog/tail/gettailname", method = "get")
    public Result<List<String>> getTailNames(@RequestParam("id") Long id,
                                             @RequestParam("appType") Integer appType,
                                             @RequestParam("tail") String tail) {
        return logTailService.getTailNamesBystoreId(tail, appType, id);
    }

    /**
     * Collection rate dictionary (need to be migrated to the dictionary interface later)
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
     * Query all applications connected to the store
     *
     * @param storeId
     * @return
     */
    @RequestMapping(path = "/milog/tail/app/store/id", method = "get")
    public Result<List<MapDTO>> queryAppByStoreId(@RequestParam(value = "storeId") Long storeId) {
        return logTailService.queryAppByStoreId(storeId);
    }

    /**
     * Get all environment information of project configuration based on app ID
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
     * front page
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
     * Get the store list under the space
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

    // Quick access
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
     * Test parsing rules
     *
     * @param mlogParseParam
     * @return
     */
    @RequestMapping(path = "/milog/logtail/parse/test")
    public Result<Object> parseScriptTest(@RequestParam(value = "param") MlogParseParam mlogParseParam) {
        return logTailService.parseScriptTest(mlogParseParam);
    }

    /**
     * Parse the index content according to the example
     *
     * @param mlogParseParam
     * @return
     */
    @RequestMapping(path = "/milog/logtail/parse/example")
    public Result<Object> parseExample(@RequestParam(value = "param") MlogParseParam mlogParseParam) {
        return logTailService.parseExample(mlogParseParam);
    }

    /**
     * Quick query by application
     *
     * @param milogAppId
     * @return
     */
    @RequestMapping(path = "/milog/logtail/tail/quick/app", method = "get")
    public Result<List<QuickQueryVO>> quickQueryByApp(@RequestParam(value = "milogAppId") Long milogAppId) {
        return logTailService.quickQueryByApp(milogAppId);
    }

    /**
     * Query the space and store of the current application
     *
     * @param appId
     * @param platFormCode
     * @return
     */
    @RequestMapping(path = "/milog/store/app", method = "get")
    public Result<QuickQueryVO> queryAppStore(@RequestParam(value = "appId") Long appId,
                                              @RequestParam(value = "platFormCode") Integer platFormCode) {
        return logTailService.queryAppStore(appId, platFormCode);
    }

    /**
     * Query the es index list under the department where the currently created user is located
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
