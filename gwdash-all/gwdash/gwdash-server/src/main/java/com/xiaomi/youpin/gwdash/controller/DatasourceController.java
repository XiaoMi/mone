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

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.bo.TeslaDs;
import com.xiaomi.youpin.gwdash.common.BizUtils;
import com.xiaomi.youpin.gwdash.common.Keys;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.support.Parameter;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.mvc.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 数据源操作
 */
@RestController
@Slf4j
public class DatasourceController {

    @Autowired
    private Dao dao;


    @Autowired
    private Redis redis;

    @Autowired
    private LoginService loginService;

    /**
     * fetch
     **/
    @RequestMapping(value = "/api/ds/fetch", method = RequestMethod.GET)
    public Result<TeslaDs> fetch(int id) {
        TeslaDs ds = dao.fetch(TeslaDs.class, id);
        ds.setPassWd("");
        return Result.success(ds);
    }


    /**
     * list
     **/
    @RequestMapping(value = "/api/ds/list", method = RequestMethod.GET)
    public Result<Map<String, Object>> list(HttpServletRequest request,
                                            @RequestParam(required = false, defaultValue = "false") boolean isMine,
                                            @RequestParam(required = false, defaultValue = "0") int page,
                                            @RequestParam(required = false, defaultValue = "100") int pageSize) {
        SessionAccount account = loginService.getAccountFromSession(request);
        String creater = account.getUsername();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("total", dao.count(TeslaDs.class, Cnd.where("1", "=", 1)));
        List<TeslaDs> list = dao.query(TeslaDs.class, Cnd.where("1", "=", 1), new Pager(page, pageSize));
        list = list.stream().map(it->{
            it.setPassWd("");
            String pw = getPwfromMonngoUrl(it);
            if (pw != null && pw.length() != 0) {
                it.setDataSourceUrl(it.getDataSourceUrl().replace(pw, "***"));
            }
            return it;
        }).collect(Collectors.toList());
        map.put("list", list);

        return Result.success(map);
    }


    /**
     * list
     **/
    @RequestMapping(value = "/api/ds/query", method = RequestMethod.GET)
    public Result<List<TeslaDs>> query(String ids) {
        if (StringUtils.isEmpty(ids) || ids.equals("null")) {
            return Result.success(Lists.newArrayList());
        }
        List<Long> list = Arrays.stream(ids.split(",")).map(it -> Long.valueOf(it)).collect(Collectors.toList());
        return Result.success(
                dao.query(TeslaDs.class, Cnd.where("id", "in", list)).stream().map(it->{
            it.setPassWd("");
            String pw = getPwfromMonngoUrl(it);
            if (pw != null && pw.length() != 0) {
                it.setDataSourceUrl(it.getDataSourceUrl().replace(pw, "***"));
            }
            return it;
        }).collect(Collectors.toList()));
    }


    /**
     * delete
     */
    @RequestMapping(value = "/api/ds/delete", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> delete(@RequestBody TeslaDs data) {
        dao.delete(TeslaDs.class, data.getId());
        redis.del(Keys.dsKey(data.getId()));
        return Result.success(true);
    }

    /**
     * update
     */
    @RequestMapping(value = "/api/ds/update", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> update(@RequestBody TeslaDs data, HttpServletRequest request) {
        int id = data.getId();
        TeslaDs ts = dao.fetch(TeslaDs.class, id);
        SessionAccount account = loginService.getAccountFromSession(request);
        data.setUtime(System.currentTimeMillis());
        data.setCreator(account.getUsername());

        if (StringUtils.isEmpty(data.getPassWd())) {
            data.setPassWd(ts.getPassWd());
        }

        if (getPwfromMonngoUrl(data).equals("***")) {
            data.setDataSourceUrl(ts.getDataSourceUrl());
        }

        dao.update(data);
        redis.set(Keys.dsKey(data.getId()), new Gson().toJson(data));
        return Result.success(true);
    }

    /**
     * insert
     */
    @RequestMapping(value = "/api/ds/insert", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> insert(@RequestBody TeslaDs data, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        long now = System.currentTimeMillis();
        data.setCtime(now);
        data.setUtime(now);
        data.setCreator(account.getUsername());
        dao.insert(data);
        redis.set(Keys.dsKey(data.getId()), new Gson().toJson(data));
        return Result.success(true);
    }

    private String getPwfromMonngoUrl(TeslaDs teslaDs) {
        if (teslaDs.getType() != 5) {
            return "";
        }
        String url = teslaDs.getDataSourceUrl();
        if (url == null || url.length() == 0) {
            return "";
        }
        //截取@之前的字符串
        String str = url.substring(0, url.indexOf("@"));
        if (str == null || str.length() == 0) {
            return "";
        }
        String[] arrStr = str.split(":");
        return arrStr[arrStr.length - 1];
    }
}
