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

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.enums.RecordingSourceTypeEnum;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.DubboSource;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.*;
import com.xiaomi.youpin.tesla.traffic.recording.daoobj.TrafficDao;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.OrderBy;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 
 */
@Service
public class TrafficService {

    @Resource
    private NutDao dao;

    /**
     * 新增流量
     *
     * @param trafficDao
     */
    public void addTraffic(TrafficDao trafficDao) {
        dao.insert(trafficDao);
    }


    public TrafficDao getById(int id) {
        return dao.fetch(TrafficDao.class, id);
    }

    public boolean delById(int id) {
        return dao.delete(TrafficDao.class, id) > 0;
    }


    public boolean update(TrafficDao trafficDao) {
        trafficDao.setUpdateTime(System.currentTimeMillis());
        dao.update(trafficDao);
        return true;
    }


    /**
     * 获取流量列表页
     *
     * @param page
     * @param pageSize
     * @param req
     * @return
     */
    public TrafficList getTrafficListByPage(int page, int pageSize, GetTrafficReq req) {
        Pager pager = null;
        if (page > 0 && pageSize > 0) {
            pager = dao.createPager(page, pageSize);
        }
        Cnd cnd = Cnd.NEW();
        if (req.getRecordingConfigId() > 0) {
            cnd = cnd.and("recording_config_id", "=", req.getRecordingConfigId());
        }
        OrderBy orderBy = cnd.orderBy("id", "desc");
        List<TrafficDao> daoList = dao.query(TrafficDao.class, orderBy, pager);

        int count = dao.count(TrafficDao.class, cnd);

        TrafficList trafficList = new TrafficList();
        trafficList.setList(daoList.stream().map(it -> adapterToTraffic(it)).collect(Collectors.toList()));
        trafficList.setPage(page);
        trafficList.setPagesize(pageSize);
        trafficList.setTotal(count);

        return trafficList;
    }

    public static TrafficDao adapterToTrafficDao(Traffic traffic) {
        if (traffic == null) {
            return null;
        }
        TrafficDao trafficDao = new TrafficDao();
        long now = System.currentTimeMillis();
        trafficDao.setCreateTime(now);
        trafficDao.setUpdateTime(now);

        if (traffic.getSourceType() == RecordingSourceTypeEnum.GATEWAY.getCode()) {
            trafficDao.setHost(traffic.getHttpTraffic().getHost());
            trafficDao.setHttpMethod(traffic.getHttpTraffic().getHttpMethod());
            trafficDao.setOrginBody(traffic.getHttpTraffic().getOrginBody());
            trafficDao.setModifyBody(traffic.getHttpTraffic().getModifyBody());
            trafficDao.setModifyHeaders(traffic.getHttpTraffic().getModifyHeaders());
            trafficDao.setOriginHeaders(traffic.getHttpTraffic().getOriginHeaders());
            trafficDao.setOriginQueryString(traffic.getHttpTraffic().getOriginQueryString());
            trafficDao.setUrl(traffic.getHttpTraffic().getUrl());
        }

        trafficDao.setId(traffic.getId());
        trafficDao.setInvokeBeginTime(traffic.getInvokeBeginTime());
        trafficDao.setInvokeEndTime(traffic.getInvokeEndTime());
        trafficDao.setRecordingConfigId(traffic.getRecordingConfigId());
        trafficDao.setResponse(traffic.getResponse());
        trafficDao.setSourceType(traffic.getSourceType());
        trafficDao.setTraceId(traffic.getTraceId());
        trafficDao.setUid(traffic.getUid());
        trafficDao.setSaveDays(traffic.getSaveDays());

        return trafficDao;
    }

    public static Traffic adapterToTraffic(TrafficDao trafficDao) {
        if (trafficDao == null) {
            return null;
        }
        Traffic traffic = new Traffic();
        traffic.setId(trafficDao.getId());
        traffic.setInvokeBeginTime(trafficDao.getInvokeBeginTime());
        traffic.setInvokeEndTime(trafficDao.getInvokeEndTime());
        traffic.setRecordingConfigId(trafficDao.getRecordingConfigId());
        traffic.setResponse(trafficDao.getResponse());
        traffic.setSourceType(trafficDao.getSourceType());
        traffic.setTraceId(trafficDao.getTraceId());
        traffic.setUid(trafficDao.getUid());
        traffic.setSaveDays(trafficDao.getSaveDays());

        DubboTraffic dubboTraffic = new DubboTraffic();
        dubboTraffic.setOrginBody(trafficDao.getOrginBody());
        traffic.setDubboTraffic(dubboTraffic);

        HttpTraffic httpTraffic = new HttpTraffic();
        httpTraffic.setHost(trafficDao.getHost());
        httpTraffic.setHttpMethod(trafficDao.getHttpMethod());
        httpTraffic.setOrginBody(trafficDao.getOrginBody());
        httpTraffic.setOriginHeaders(trafficDao.getOriginHeaders());
        httpTraffic.setUrl(trafficDao.getUrl());
        httpTraffic.setOriginQueryString(trafficDao.getOriginQueryString());
        httpTraffic.setModifyHeaders(trafficDao.getModifyHeaders());
        httpTraffic.setModifyBody(trafficDao.getModifyBody());
        traffic.setHttpTraffic(httpTraffic);

        traffic.setCreateTime(trafficDao.getCreateTime());
        traffic.setUpdateTime(trafficDao.getUpdateTime());
        traffic.setUpdater(trafficDao.getUpdater());
        traffic.setCreator(trafficDao.getCreator());

        return traffic;
    }

}
