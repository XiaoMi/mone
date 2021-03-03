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

import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayResponse;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.GetTrafficReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.Traffic;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.TrafficList;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.TrafficDubboService;
import com.xiaomi.youpin.tesla.traffic.recording.daoobj.ReplayResultDao;
import com.xiaomi.youpin.tesla.traffic.recording.daoobj.TrafficDao;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author 
 * @author goodjava@qq.com
 * 提供对外的dubbo接口
 */
@Slf4j
@Service(group = "$group", interfaceClass = TrafficDubboService.class)
public class TrafficDubboServiceImpl implements TrafficDubboService {

    @Resource
    private TrafficService trafficService;

    @Resource
    private NutDao dao;


    @Override
    public Result<TrafficList> getTrafficList(GetTrafficReq req) {
        try {
            TrafficList trafficList = trafficService.getTrafficListByPage(req.getPage(), req.getPageSize(), req);
            return Result.success(trafficList);
        } catch (Exception e) {
            log.error("RecordingDubboServiceImpl.getRecordingConfigList, ", e);
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result<Traffic> getTraffic(GetTrafficReq req) {
        TrafficDao traffic = trafficService.getById(req.getId());
        Traffic data = TrafficService.adapterToTraffic(traffic);
        return Result.success(data);
    }

    @Override
    public Result<Boolean> delTraffic(GetTrafficReq req) {
        trafficService.delById(req.getId());
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateTraffic(GetTrafficReq req) {
        TrafficDao traffic = trafficService.getById(req.getId());
        Optional.ofNullable(traffic).ifPresent(t -> {
            t.setModifyHeaders(req.getNewHeaders());
            t.setModifyBody(req.getNewBody());
            trafficService.update(t);
        });
        return Result.success(true);
    }

    @Override
    public Result<ReplayResponse> getLastCallResult(GetTrafficReq req) {
        ReplayResultDao data = dao.fetch(ReplayResultDao.class, Cnd.where("traffic_id", "=", req.getId()));
        ReplayResponse res = new ReplayResponse();
        res.setData("");
        Optional.ofNullable(data).ifPresent(it -> {
            res.setData(it.getResult());
        });
        return Result.success(res);
    }

}
