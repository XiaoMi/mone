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

import com.xiaomi.mone.log.manager.model.dto.MilogAppConfigTailDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppMiddlewareRel;
import com.xiaomi.mone.log.manager.service.impl.MilogAppMiddlewareRelServiceImpl;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

import static com.xiaomi.mone.log.common.Constant.EQUAL_OPERATE;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/23 11:15
 */
@Service
@Slf4j
public class MilogAppMiddlewareRelDao {

    @Resource
    private NutDao dao;

    @Resource
    private MilogAppMiddlewareRelServiceImpl milogAppMiddlewareRelService;

    public void insertUpdate(MilogAppMiddlewareRel middlewareRel) {
        Cnd cnd = Cnd.where("milog_app_id", EQUAL_OPERATE, middlewareRel.getMilogAppId())
                .and("middleware_id", EQUAL_OPERATE, middlewareRel.getMiddlewareId())
                .and("tail_id", EQUAL_OPERATE, middlewareRel.getTailId());
        List<MilogAppMiddlewareRel> middlewareRels = dao.query(MilogAppMiddlewareRel.class, cnd);
        if (CollectionUtils.isEmpty(middlewareRels)) {
            List<MilogAppMiddlewareRel> appMiddlewareRels = queryByCondition(middlewareRel.getMilogAppId(), null, middlewareRel.getTailId());
            if (null != appMiddlewareRels) {
                for (MilogAppMiddlewareRel appMiddlewareRel : appMiddlewareRels) {
                    delete(appMiddlewareRel.getId());
                }
            }
            dao.insert(middlewareRel);
        } else {
            MilogAppMiddlewareRel rel = middlewareRels.get(middlewareRels.size() - 1);
            if (null != rel.getConfig().getBatchSendSize() && null == middlewareRel.getConfig().getBatchSendSize()) {
                middlewareRel.getConfig().setBatchSendSize(rel.getConfig().getBatchSendSize());
            }
            middlewareRel.setId(rel.getId());
            dao.update(middlewareRel);
        }
    }

    public List<MilogAppMiddlewareRel> queryByCondition(Long milogAppId, Long middlewareId, Long tailId) {
        Cnd cnd = Cnd.NEW();
        if (null != milogAppId) {
            cnd.and("milog_app_id", EQUAL_OPERATE, milogAppId);
        }
        if (null != middlewareId) {
            cnd.and("middleware_id", EQUAL_OPERATE, middlewareId);
        }
        if (null != tailId) {
            cnd.and("tail_id", EQUAL_OPERATE, tailId);
        }
        List<MilogAppMiddlewareRel> middlewareRels = dao.query(MilogAppMiddlewareRel.class, cnd);
        return middlewareRels;
    }

    public boolean updateTopicRelMqConfig(Long id, MilogAppMiddlewareRel.Config config) {
        int ret = dao.update(MilogAppMiddlewareRel.class, Chain.make("config", config), Cnd.where("id", "=", id));
        if (ret == 1) {
            return true;
        }
        return false;
    }

    public void deleteRel(Long milogAppId, Long tailId) {
        List<MilogAppMiddlewareRel> milogAppMiddlewareRels = queryByCondition(milogAppId, null, tailId);
        if (CollectionUtils.isNotEmpty(milogAppMiddlewareRels)) {
            milogAppMiddlewareRels.forEach(middlewareRel -> {
                dao.delete(MilogAppMiddlewareRel.class, middlewareRel.getId());
            });
        }
    }

    public List<MilogAppConfigTailDTO.ConfigTailDTO> queryByAMilogAppId(Long milogAppId) {
        Sql sql = Sqls.queryEntity("SELECT\n" +
                "\tmr.middleware_id AS middlewareId,\n" +
                "\tmc.type AS type,\n" +
                "\tmc.alias AS middlewareName,\n" +
                "\tml.id AS tailId,\n" +
                "\tml.tail AS tailName,\n" +
                "\tml.creator AS tailCreator,\n" +
                "\tml.ctime AS tailCreateTime,\n" +
                "\tml.utime AS tailUpdateTime,\n" +
                "\tml.updater AS tailUpdater,\n" +
                "\tmr.config AS mqConfig \n" +
                "FROM\n" +
                "\tmilog_app_middleware_rel mr\n" +
                "\tLEFT JOIN milog_middleware_config mc ON mr.middleware_id = mc.id\n" +
                "\tLEFT JOIN milog_logstail ml ON mr.tail_id = ml.id \n" +
                "WHERE\n" +
                "\tmr.milog_app_id = @milogAppId");
        sql.params().set("milogAppId", milogAppId);
        sql.setEntity(dao.getEntity(MilogAppConfigTailDTO.ConfigTailDTO.class));
        dao.execute(sql);
        return sql.getList(MilogAppConfigTailDTO.ConfigTailDTO.class);
    }

    public void update(MilogAppMiddlewareRel milogAppMiddlewareRel) {
        dao.update(milogAppMiddlewareRel);
    }

    public void delete(Long id) {
        dao.delete(MilogAppMiddlewareRel.class, id);
    }

    public void insertConfig(Long milogAppId, Long tailId, Long middlewareConfigId, String topicName) {
        milogAppMiddlewareRelService.bindingTailConfigRel(tailId, milogAppId, middlewareConfigId, topicName);
    }

    public List<MilogAppMiddlewareRel> getAppRelByLimit(int offset, int rows) {
        String sqlString = String.format("select * from milog_app_middleware_rel limit %d,%d", offset, rows);
        Sql sql = Sqls.queryEntity(sqlString);
        sql.setEntity(dao.getEntity(MilogAppMiddlewareRel.class));
        dao.execute(sql);
        return sql.getList(MilogAppMiddlewareRel.class);
    }

    public Integer queryCountByTopicName(String topicName) {
        Sql sql = Sqls.queryRecord("SELECT count(1) as count FROM `milog_app_middleware_rel` where config ->'$.topic' = @topicName");
        sql.params().set("topicName", topicName);
        LinkedList<Record> records = (LinkedList<Record>) dao.execute(sql).getResult();
        int access = records.get(0).getInt("count");
        return access;
    }
}
