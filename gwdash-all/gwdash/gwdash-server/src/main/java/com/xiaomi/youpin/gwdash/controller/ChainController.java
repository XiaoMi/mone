/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.controller;


import ao.CaseAO;
import ao.ChainLayoutAO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.ChainService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 链路压测
 * @author zhenghao
 *
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class ChainController {

    @Autowired
    private ChainService chainService;

    @Autowired
    private LoginService loginService;

    /**
     * 链路添加
     * @param gsonParam
     * @return
     */
    @RequestMapping(value = "/chain/add", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Map<String, Object>> add(@RequestBody String gsonParam, HttpServletRequest request) {
        log.info("ChainController /chain/add gsonParam:{}", gsonParam);
        Gson gson = new Gson();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> mapParam = gson.fromJson(gsonParam, type);
        List<ChainLayoutAO> chainLayoutAOList = new ArrayList<>();
        String mapGson = gson.toJson(mapParam.get("backParam"));
        JsonArray jsonArray = new JsonParser().parse(mapGson).getAsJsonArray();

        SessionAccount account = loginService.getAccountFromSession(request);
        String username = null;
        if (account != null) {
            username = account.getUsername();
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            ChainLayoutAO bean = gson.fromJson(jsonArray.get(i),ChainLayoutAO.class);
            bean.setFrontParam(mapParam.get("frontParam"));
            bean.setChainAliasName(mapParam.get("chainAliasName").toString());
            bean.setBackParam(mapParam.get("backParam"));
            // todo
            bean.setUid(username);
            chainLayoutAOList.add(bean);
        }
        String uuid = chainService.addChain(chainLayoutAOList);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", uuid);
        log.info("ChainController /chain/add result:{}", map.toString());
        return Result.success(map);
    }

    /**
     * 链路执行
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/chain/execute", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<List<ChainLayoutAO>> execute(String uuid) {
        log.info("ChainController /chain/execute uuid:{}", uuid);
        if (StringUtils.isBlank(uuid)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        List<ChainLayoutAO> chainLayoutAOList = chainService.executeChain(uuid);
        log.info("ChainController /chain/execute result:{}", chainLayoutAOList);
        return Result.success(chainLayoutAOList);
    }

    @RequestMapping(value = "/chain/caseList", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<List<CaseAO>> caseList() {
        List<CaseAO> caseList = chainService.showCaseList();
        return Result.success(caseList);
    }

    /**
     * 链路分页
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/chain/page", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> page(Integer page, Integer pageSize, String chainAliasName, HttpServletRequest request) {
        log.info("ChainController /chain/execute page:{}, pageSize:{}, chainAliasName:{}",
                page, pageSize, chainAliasName);
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        // todo
        SessionAccount account = loginService.getAccountFromSession(request);
        String username = null;
        if (account != null) {
            username = account.getUsername();
        }

        String uid = username;
        Map<String, Object> map = chainService.chainPage(uid, page, pageSize, chainAliasName);
        map.put("page", page);
        map.put("pageSize", pageSize);
        log.info("ChainController /chain/page result:{}", map);
        return Result.success(map);
    }

    /**
     * 链路详情
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/chain/detail", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> detail(String uuid) {
        log.info("ChainController /chain/execute uuid:{}", uuid);
        if (StringUtils.isBlank(uuid)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        List<ChainLayoutAO> chainLayoutAOList = chainService.getChainByUUid(uuid);
        log.info("ChainController /chain/page result:{}", chainLayoutAOList);
        return Result.success(chainLayoutAOList);
    }

    @RequestMapping(value = "/chain/update", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> update(@RequestBody String gsonParam, HttpServletRequest request) {
        log.info("ChainController /chain/update gsonParam:{}", gsonParam);
        Gson gson = new Gson();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> mapParam = gson.fromJson(gsonParam, type);
        List<ChainLayoutAO> chainLayoutAOList = new ArrayList<>();
        String backGson = gson.toJson(mapParam.get("backParam"));
        JsonArray jsonArray = new JsonParser().parse(backGson).getAsJsonArray();

        SessionAccount account = loginService.getAccountFromSession(request);
        String username = null;
        if (account != null) {
            username = account.getUsername();
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            ChainLayoutAO bean = gson.fromJson(jsonArray.get(i),ChainLayoutAO.class);
            bean.setFrontParam(mapParam.get("frontParam"));
            bean.setChainAliasName(mapParam.get("chainAliasName").toString());
            bean.setBackParam(mapParam.get("backParam"));
            // todo
            bean.setUid(username);
            chainLayoutAOList.add(bean);
        }

        String result = chainService.updateChain(chainLayoutAOList, mapParam.get("uuid").toString());
        log.info("ChainController /chain/update result:{}", result);
        return Result.success(result);
    }

    /**
     * 删除链路
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/chain/delete", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Object> delete(String uuid) {
        log.info("ChainController /chain/delete uuid:{}", uuid);
        if (StringUtils.isBlank(uuid)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        String result = chainService.deleteChain(uuid);
        log.info("ChainController /chain/delete result:{}", result);
        return Result.success(result);
    }

}