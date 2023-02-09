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

package com.xiaomi.youpin.tesla.traffic.recording.service;

import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import com.xiaomi.youpin.mibench.bo.RequestParam;
import com.xiaomi.youpin.mibench.service.OpenApiService;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayData;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayRequest;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.ReplayService;
import com.xiaomi.youpin.tesla.traffic.recording.daoobj.ReplayResultDao;
import com.xiaomi.youpin.tesla.traffic.recording.daoobj.TrafficDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service(group = "$group", interfaceClass = ReplayService.class)
public class ReplayServiceImpl implements ReplayService {


    @Reference(check = false, interfaceClass = OpenApiService.class)
    private OpenApiService openApiService;


    @Resource
    private TrafficService trafficService;

    @Resource
    private NutDao dao;


    @Override
    public Result<ReplayData> replay(ReplayRequest request) {
        long now = System.currentTimeMillis();
        TrafficDao traffic = trafficService.getById(request.getId());
        log.info("replay:{}", traffic);
        RequestParam param = new RequestParam();
        param.setMethod(traffic.getHttpMethod());
        param.setBody(getBody(traffic));
        param.setHeaders(getHeader(traffic));
        param.setTimeout(1000);
        param.setUrl("http://st.m.youpin.mi.com" + traffic.getUrl());
        com.xiaomi.youpin.mibench.bo.Result<String> res = openApiService.httpTest(param);
        log.info("replay {} res:{}", param, res.toString());
        ReplayData data = new ReplayData();


        ReplayResultDao replayResultDao = dao.fetch(ReplayResultDao.class, Cnd.where("traffic_id", "=", request.getId()));
        if (null == replayResultDao) {
            replayResultDao = new ReplayResultDao();
            replayResultDao.setCtime(now);
        }
        replayResultDao.setResult(res.getData());
        replayResultDao.setTrafficId(request.getId());
        replayResultDao.setUtime(now);

        dao.insertOrUpdate(replayResultDao);

        data.setData(res.getData());
        return Result.success(data);
    }


    private Map getHeader(TrafficDao trafficDao) {
        if (null != trafficDao.getModifyHeaders() && trafficDao.getModifyHeaders().size() > 0) {
            return trafficDao.getModifyHeaders();
        }
        return trafficDao.getOriginHeaders();
    }


    private String getBody(TrafficDao trafficDao) {
        if (StringUtils.isNotEmpty(trafficDao.getModifyBody())) {
            return trafficDao.getModifyBody();
        }
        return trafficDao.getOrginBody();
    }


}
