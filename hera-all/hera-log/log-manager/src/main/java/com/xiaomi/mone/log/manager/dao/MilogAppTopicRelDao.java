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
package com.xiaomi.mone.log.manager.dao;

import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppTopicRelDO;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.EQUAL_OPERATE;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/8 19:33
 */
@Service
@Slf4j
public class MilogAppTopicRelDao {

    @Resource
    private NutDao dao;

    @Reference(interfaceClass = HeraAppService.class, group = "$dubbo.env.group", check = false)
    private HeraAppService heraAppService;

    public MilogAppTopicRelDO insert(MilogAppTopicRelDO milogAppTopicRel) {
        return dao.insert(milogAppTopicRel);
    }

    public List<MilogAppTopicRelDO> queryAppInfo(Long appId, String appName, Long tenantId, String source) {
        return queryAppInfo(appId, appName, tenantId, source, null);
    }

    public List<MilogAppTopicRelDO> queryAppInfo(Long appId, String appName, Long tenantId, String source, Integer appType) {
        Cnd cnd = Cnd.where("app_id", EQUAL_OPERATE, appId);
        if (StringUtils.isNotEmpty(appName)) {
            cnd.andEX("app_name", EQUAL_OPERATE, appName);
        }
        if (null != tenantId) {
            cnd.andEX("tenant_id", EQUAL_OPERATE, tenantId);
        }
        if (null != appType) {
            cnd.andEX("type", EQUAL_OPERATE, appType);
        }
        if (StringUtils.isNotEmpty(source)) {
            cnd.andEX("source", EQUAL_OPERATE, source);
        }
        return dao.query(MilogAppTopicRelDO.class, cnd);
    }

    public void deleteAppTopicRelDb(Long appId, String appName, Long tenantId, String source) {
        List<MilogAppTopicRelDO> milogAppTopicRels = queryAppInfo(appId, appName, tenantId, source);
        if (CollectionUtils.isNotEmpty(milogAppTopicRels)) {
            dao.delete(MilogAppTopicRelDO.class, milogAppTopicRels.get(0).getId());
        }
    }

    public MilogAppTopicRelDO queryByAppId(Long appId, String source) {
        Cnd cnd = Cnd.where("app_id", EQUAL_OPERATE, appId);
        if (StringUtils.isNotEmpty(source)) {
            cnd.andEX("source", EQUAL_OPERATE, source);
        }
        List<MilogAppTopicRelDO> milogAppTopicRels = dao.query(MilogAppTopicRelDO.class, cnd);
        if (CollectionUtils.isNotEmpty(milogAppTopicRels)) {
            return milogAppTopicRels.get(milogAppTopicRels.size() - 1);
        }
        return null;
    }

    public MilogAppTopicRelDO queryByAppId(Long appId, Integer type) {
        Cnd cnd = Cnd.where("app_id", EQUAL_OPERATE, appId);
        if (null != type) {
            cnd.andEX("type", EQUAL_OPERATE, type);
        }
        List<MilogAppTopicRelDO> milogAppTopicRels = dao.query(MilogAppTopicRelDO.class, cnd);
        if (CollectionUtils.isNotEmpty(milogAppTopicRels)) {
            return milogAppTopicRels.get(milogAppTopicRels.size() - 1);
        }
        return null;
    }

    public List<MilogAppTopicRelDO> queryByMilogAppIds(List<Long> milogAppIds) {
        Cnd cnd = Cnd.where("id", "IN", milogAppIds);
        return dao.query(MilogAppTopicRelDO.class, cnd);
    }

    public List<MilogAppTopicRelDO> queryAppInfoByName(String appName, Integer type) {
        Cnd cnd = Cnd.where("type", EQUAL_OPERATE, type);
        if (StringUtils.isNotEmpty(appName)) {
            cnd.and("app_name", "like", "%" + appName + "%");
        }
        List<MilogAppTopicRelDO> ret = dao.query(MilogAppTopicRelDO.class, cnd);
        return ret;
    }

    public List<MilogAppTopicRelDO> queryAppInfoByName(String appName, String source, Integer type) {
        Cnd cnd = Cnd.NEW();
        if (StringUtils.isNotEmpty(appName)) {
            Cnd.where("app_name", "like", "%" + appName + "%");
        }
        if (null != type) {
            cnd.and("type", EQUAL_OPERATE, type);
        } else {
            cnd.and("source", EQUAL_OPERATE, source);
        }
        List<MilogAppTopicRelDO> ret = dao.query(MilogAppTopicRelDO.class, cnd);
        return ret;
    }

    public List<MilogAppTopicRelDO> queryAppInfoByNameSource(String appName, String source, Integer type) {
        Cnd cnd = Cnd.NEW();
        if (StringUtils.isNotEmpty(appName)) {
            Cnd.where("app_name", "like", "%" + appName + "%");
        }
        if (StringUtils.isNotBlank(source)) {
            cnd.and("source", EQUAL_OPERATE, source);
        }
        if (null != type) {
            cnd.and("type", EQUAL_OPERATE, type);
        }
        List<MilogAppTopicRelDO> ret = dao.query(MilogAppTopicRelDO.class, cnd);
        return ret;
    }

    public List<MilogAppTopicRelDO> queryAppTopicList(Condition cnd, Pager pager) {
        if (null == pager) {
            return dao.query(MilogAppTopicRelDO.class, cnd);
        }
        return dao.query(MilogAppTopicRelDO.class, cnd, pager);
    }

    public void updateTopicName(Long appId, String topicNameSimple) {
    }

    public MilogAppTopicRelDO queryById(Long id) {
        AppBaseInfo appBaseInfo = heraAppService.queryById(id);
        MilogAppTopicRelDO milogAppTopicRelDO = new MilogAppTopicRelDO();
        milogAppTopicRelDO.setId(appBaseInfo.getId().longValue());
        milogAppTopicRelDO.setAppId(Long.valueOf(appBaseInfo.getBindId()));
        milogAppTopicRelDO.setAppName(appBaseInfo.getAppName());
        if (CollectionUtils.isNotEmpty(appBaseInfo.getTreeIds())) {
            milogAppTopicRelDO.setTreeIds(appBaseInfo.getTreeIds().stream().map(Integer::longValue).collect(Collectors.toList()));
        }
        milogAppTopicRelDO.setSource(appBaseInfo.getPlatformName());
        milogAppTopicRelDO.setType(appBaseInfo.getPlatformType());
        return milogAppTopicRelDO;
    }

    public void updateAppTopicRelMqConfigById(Long id, String existTopic) {
    }

    public Integer queryAppTopicPageCount(Condition cnd) {
        return dao.count(MilogAppTopicRelDO.class, cnd);
    }

    public void delTopicRecordAll() {
        dao.delete(MilogAppTopicRelDO.class);
    }

    public List<MilogAppTopicRelDO> queryAllMilogAppList() {
        return queryAppTopicList(Cnd.NEW(), null);
    }

    public List<MilogAppTopicRelDO> queryAllAccessMilogAppList() {
        Sql sql = Sqls.queryEntity("SELECT\n" +
                "\tmt.* \n" +
                "FROM\n" +
                "\tmilog_app_topic_rel mt\n" +
                "\tLEFT JOIN ( SELECT DISTINCT milog_app_id FROM milog_logstail ) ml ON mt.id = ml.milog_app_id \n" +
                "WHERE  ml.milog_app_id IS NOT NULL");
        sql.setEntity(dao.getEntity(MilogAppTopicRelDO.class));
        dao.execute(sql);
        return sql.getList(MilogAppTopicRelDO.class);
    }


    public List<MilogAppTopicRelDO> queryAppsExistInMachine(String ip) {
        Sql sql = Sqls.queryEntity("SELECT mt.* FROM milog_app_topic_rel mt LEFT JOIN( SELECT DISTINCT ml.app_id,ms.source FROM milog_logstail ml LEFT JOIN milog_space ms ON ml.space_id = ms.id WHERE JSON_CONTAINS( ml.ips, JSON_ARRAY( @ip)) ) ma ON mt.app_id = ma.app_id and mt.source = ma.source WHERE ma.app_id IS NOT NULL");
        sql.params().set("ip", ip);
        sql.setEntity(dao.getEntity(MilogAppTopicRelDO.class));
        dao.execute(sql);
        return sql.getList(MilogAppTopicRelDO.class);
    }

    public MilogAppTopicRelDO queryByIpAndAppid(Long appId, String ip) {
        Sql sql = Sqls.queryEntity("SELECT mt.* FROM milog_app_topic_rel mt LEFT JOIN( SELECT DISTINCT ml.app_id,ms.source FROM milog_logstail ml LEFT JOIN milog_space ms ON ml.space_id = ms.id WHERE JSON_CONTAINS( ml.ips, JSON_ARRAY( @ip)) ) ma ON mt.app_id = ma.app_id and mt.source = ma.source WHERE ma.app_id =@appId");
        sql.params().set("ip", ip);
        sql.params().set("appId", appId);
        sql.setEntity(dao.getEntity(MilogAppTopicRelDO.class));
        dao.execute(sql);
        return sql.getList(MilogAppTopicRelDO.class).get(0);
    }

    public int getAppCount() {
        return dao.count(MilogAppTopicRelDO.class);
    }

    public List<MilogAppTopicRelDO> getMioneAppAll(String source) {
        Cnd cnd = Cnd.where("source", EQUAL_OPERATE, source).and("type", EQUAL_OPERATE, ProjectTypeEnum.MIONE_TYPE.getCode());
        return dao.query(MilogAppTopicRelDO.class, cnd);
    }

    public MilogAppTopicRelDO queryIsExists(MilogAppTopicRelDO milogAppTopicRel) {
        Cnd cnd = Cnd.where("app_id", EQUAL_OPERATE, milogAppTopicRel.getAppId())
                .and("source", EQUAL_OPERATE, milogAppTopicRel.getSource())
                .and("type", EQUAL_OPERATE, milogAppTopicRel.getType());
        List<MilogAppTopicRelDO> appTopicRels = dao.query(MilogAppTopicRelDO.class, cnd);
        if (CollectionUtils.isNotEmpty(appTopicRels)) {
            return appTopicRels.get(appTopicRels.size() - 1);
        }
        return null;
    }

    public void insertNoExists(MilogAppTopicRelDO milogAppTopicRel) {
        MilogAppTopicRelDO appTopicRel = queryIsExists(milogAppTopicRel);
        if (null == appTopicRel) {
            dao.insert(milogAppTopicRel);
        } else {
            if ((null != milogAppTopicRel.getNodeIPs() && milogAppTopicRel.getNodeIPs().size() > 0) &&
                    (null == appTopicRel.getNodeIPs() || appTopicRel.getNodeIPs().size() == 0 || milogAppTopicRel.getNodeIPs().size() != appTopicRel.getNodeIPs().size())) {
                appTopicRel.setNodeIPs(milogAppTopicRel.getNodeIPs());
                dao.update(appTopicRel);
            }
        }

    }

    public void update(MilogAppTopicRelDO milogAppTopicRel) {
        dao.update(milogAppTopicRel);
    }

    public MilogAppTopicRelDO queryByIamTreeId(Long iamTreeId) {
        Cnd cnd = Cnd.where("iam_tree_id", EQUAL_OPERATE, iamTreeId);
        List<MilogAppTopicRelDO> appTopicRels = dao.query(MilogAppTopicRelDO.class, cnd);
        if (CollectionUtils.isNotEmpty(appTopicRels)) {
            return appTopicRels.get(appTopicRels.size() - 1);
        }
        return null;
    }

    public void deleteByAppIds(Long appId, String source) {
        MilogAppTopicRelDO appTopicRel = queryByAppId(appId, source);
        if (null != appTopicRel) {
            dao.delete(MilogAppTopicRelDO.class, appTopicRel.getId());
        }
    }

    public List<MilogAppTopicRelDO> queryAppInfoByChinaCondition(Long appId, Long iamTreeId) {
        Cnd cnd = Cnd.where("app_id", EQUAL_OPERATE, appId);
        if (null != iamTreeId) {
            cnd.andEX("iam_tree_id", EQUAL_OPERATE, iamTreeId);
        }
        List<MilogAppTopicRelDO> appTopicRels = dao.query(MilogAppTopicRelDO.class, cnd);
        return appTopicRels;
    }

    public List<MilogAppTopicRelDO> queryByIds(List<Long> ids) {
        Cnd cnd = Cnd.where("id", "IN", ids);
        return dao.query(MilogAppTopicRelDO.class, cnd);
    }

    public List<MilogAppTopicRelDO> queryAppbyNameSource(String appName, String source) {
        Cnd cnd = Cnd.where("app_name", EQUAL_OPERATE, appName)
                .and("source", EQUAL_OPERATE, source);
        return dao.query(MilogAppTopicRelDO.class, cnd);
    }

    public List<MilogAppTopicRelDO> queryAppbyName(String appName) {
        Cnd cnd = Cnd.where("app_name", EQUAL_OPERATE, appName);
        return dao.query(MilogAppTopicRelDO.class, cnd);
    }
}
