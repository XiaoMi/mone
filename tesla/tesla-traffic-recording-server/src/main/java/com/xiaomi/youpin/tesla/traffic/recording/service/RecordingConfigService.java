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
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.*;
import com.xiaomi.youpin.tesla.traffic.recording.daoobj.RecordingConfigDao;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.OrderBy;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordingConfigService {

    @Resource
    private NutDao dao;

    /**
     * 新增录制配置
     *
     * @param recordingConfigDao
     */
    public void addRecordingConfig(RecordingConfigDao recordingConfigDao) {
        dao.insert(recordingConfigDao);
    }

    /**
     * 更新录制配置
     *
     * @param recordingConfigDao
     */
    public void updateRecordingConfig(RecordingConfigDao recordingConfigDao) {
        dao.update(recordingConfigDao);
    }

    public void deleteRecordingConfig(int id) {
        dao.delete(RecordingConfigDao.class, id);
    }

    /**
     * 根据id获取配置
     *
     * @param id
     * @return
     */
    public RecordingConfigDao getRecordingConfigDaoById(int id) {
        RecordingConfigDao recordingConfigDao = dao.fetch(RecordingConfigDao.class, id);
        return recordingConfigDao;
    }

    /**
     * 录制配置列表页
     *
     * @param page
     * @param pageSize
     * @param req
     * @return
     */
    public RecordingConfigList getRecordingConfigListByPage(int page, int pageSize, GetRecordingConfigListReq req) {
        Pager pager = null;
        if (page > 0 && pageSize > 0) {
            pager = dao.createPager(page, pageSize);
        }
        Cnd cnd = Cnd.NEW();
        if (req.getSourceType() > 0) {
            cnd = cnd.and("source_type", "=", req.getSourceType());
        }
        if (req.getStatus() > 0) {
            cnd = cnd.and("status", "=", req.getStatus());
        }
        if (req.getSourceType() == RecordingSourceTypeEnum.GATEWAY.getCode() && req.getEnvType() > 0) {
            cnd = cnd.and("env_type", "=", req.getEnvType());
        }
        if (StringUtils.isNotEmpty(req.getCreator())) {
            cnd = cnd.and("creator", "=", req.getCreator());
        }
        if (StringUtils.isNotEmpty(req.getName())) {
            cnd = cnd.and("name", "LIKE", "%" + req.getName() + "%");

        }
        OrderBy orderBy = cnd.orderBy("id", "desc");
        List<RecordingConfigDao> daoList = dao.query(RecordingConfigDao.class, orderBy, pager);

        int count = dao.count(RecordingConfigDao.class, cnd);

        RecordingConfigList recordingConfigList = new RecordingConfigList();
        recordingConfigList.setList(daoList.stream().map(it -> adapterToRecordingConfig(it)).collect(Collectors.toList()));
        recordingConfigList.setPage(page);
        recordingConfigList.setPagesize(pageSize);
        recordingConfigList.setTotal(count);

        return recordingConfigList;
    }

    public static RecordingConfig adapterToRecordingConfig(RecordingConfigDao recordingConfigDao) {
        if (recordingConfigDao == null) {
            return null;
        }
        RecordingConfig recordingConfig = new RecordingConfig();
        recordingConfig.setId(recordingConfigDao.getId());
        recordingConfig.setName(recordingConfigDao.getName());
        recordingConfig.setRecordingStrategy(recordingConfigDao.getRecordingStrategy());
        recordingConfig.setPercentage(recordingConfigDao.getPercentage());
        recordingConfig.setUid(recordingConfigDao.getUid());
        recordingConfig.setHeaders(recordingConfigDao.getHeaders());
        recordingConfig.setSourceType(recordingConfigDao.getSourceType());
        recordingConfig.setStatus(recordingConfigDao.getStatus());
        recordingConfig.setCreateTime(recordingConfigDao.getCreateTime());
        recordingConfig.setCreator(recordingConfigDao.getCreator());
        recordingConfig.setUpdater(recordingConfigDao.getUpdater());
        recordingConfig.setUpdateTime(recordingConfigDao.getUpdateTime());

        DubboSource dubboSource = new DubboSource();
        dubboSource.setGroup(recordingConfigDao.getGroup());
        dubboSource.setMethods(recordingConfigDao.getMethods());
        dubboSource.setServiceName(recordingConfigDao.getServiceName());
        dubboSource.setVersion(recordingConfigDao.getVersion());
        recordingConfig.setDubboSource(dubboSource);

        GatewaySource gatewaySource = new GatewaySource();
        gatewaySource.setEnvType(recordingConfigDao.getEnvType());
        gatewaySource.setUrl(recordingConfigDao.getUrl());
        recordingConfig.setGatewaySource(gatewaySource);
        recordingConfig.setSaveDays(recordingConfigDao.getSaveDays());

        return recordingConfig;
    }

    public static RecordingConfigDao adapterToRecordingConfigDao(RecordingConfig recordingConfig) {
        if (recordingConfig == null) {
            return null;
        }
        RecordingConfigDao recordingConfigDao = new RecordingConfigDao();
        recordingConfigDao.setId(recordingConfig.getId());
        recordingConfigDao.setUpdater(recordingConfig.getUpdater());
        recordingConfigDao.setUpdateTime(recordingConfig.getUpdateTime());
        recordingConfigDao.setStatus(recordingConfig.getStatus());
        recordingConfigDao.setCreateTime(recordingConfig.getCreateTime());
        recordingConfigDao.setCreator(recordingConfig.getCreator());
        if (recordingConfig.getSourceType() == RecordingSourceTypeEnum.GATEWAY.getCode()) {
            recordingConfigDao.setEnvType(recordingConfig.getGatewaySource().getEnvType());
            recordingConfigDao.setUrl(recordingConfig.getGatewaySource().getUrl());
        }
        if (recordingConfig.getSourceType() == RecordingSourceTypeEnum.DUBBO.getCode()) {
            recordingConfigDao.setGroup(recordingConfig.getDubboSource().getGroup());
            recordingConfigDao.setServiceName(recordingConfig.getDubboSource().getServiceName());
            recordingConfigDao.setMethods(recordingConfig.getDubboSource().getMethods());
            recordingConfigDao.setVersion(recordingConfig.getDubboSource().getVersion());
        }
        recordingConfigDao.setPercentage(recordingConfig.getPercentage());
        recordingConfigDao.setHeaders(recordingConfig.getHeaders());
        recordingConfigDao.setName(recordingConfig.getName());
        recordingConfigDao.setUid(recordingConfig.getUid());
        recordingConfigDao.setRecordingStrategy(recordingConfig.getRecordingStrategy());
        recordingConfigDao.setSourceType(recordingConfig.getSourceType());
        recordingConfigDao.setSaveDays(recordingConfig.getSaveDays());

        return recordingConfigDao;
    }

}
