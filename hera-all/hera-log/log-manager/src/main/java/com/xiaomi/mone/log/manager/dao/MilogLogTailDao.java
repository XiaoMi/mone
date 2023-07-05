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

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.MachineTypeEnum;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.EQUAL_OPERATE;

@Service
@Slf4j
public class MilogLogTailDao {
    @Resource
    private NutDao dao;

    public MilogLogTailDo newMilogLogtail(MilogLogTailDo mt) {
        return dao.insert(mt);
    }

    public boolean update(MilogLogTailDo mt) {
        return 1 == dao.update(mt);
    }

    public boolean updateMilogLogtail(Long id, Integer parseType, String parseScript, String logPath, String valueList, Long appId, Long envId, String envName,
                                      String tail, List<String> ips, List<FilterDefine> confs,
                                      Long milogAppId, String appName, String logSplitExpress, Integer deployWay, String deploySpace, String firstLineReg) {
//        int ret = dao.update(MilogLogTailDo.class, Chain.make("space_id", spaceId).add("app_id", appId).add("parse_type",parseType).add("log_path",logPath).add("value_list",valueList), Cnd.where("id", "=", id));
        Chain chain = Chain.make("parse_type", parseType).add("parse_script", parseScript)
                .add("log_path", logPath).add("value_list", valueList)
                .add("env_id", envId).add("env_name", envName)
                .add("tail", tail).add("ips", ips)
                .add("filter", confs).add("milog_app_id", milogAppId).add("app_name", appName)
                .add("deploy_way", deployWay)
                .add("deploy_space", deploySpace)
                .add("first_line_reg", firstLineReg);
        if (null != appId) {
            chain.add("app_id", appId);
        }
        chain.add("log_split_express", logSplitExpress);
        chain.add("utime", Instant.now().toEpochMilli());
        chain.add("updater", MoneUserContext.getCurrentUser().getUser());
        int ret = dao.update(MilogLogTailDo.class, chain, Cnd.where("id", EQUAL_OPERATE, id));
        if (ret != 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteMilogLogtail(Long id) {
        int ret = dao.clear(MilogLogTailDo.class, Cnd.where("id", EQUAL_OPERATE, id));
        if (ret != 1) {
            return false;
        } else {
            return true;
        }
    }

    public List<MilogLogTailDo> queryTailByAppName(String appName) {
        Cnd cnd = Cnd.NEW();
        if (!StringUtils.isNotEmpty(appName)) {
            cnd = cnd.and("app_name", EQUAL_OPERATE, appName);
        }
        return dao.query(MilogLogTailDo.class, cnd);
    }

    public List<MilogLogTailDo> getMilogLogtail(List<Long> ids) {
        return dao.query(MilogLogTailDo.class, Cnd.where("id", "in", ids));
    }

    public List<MilogLogTailDo> getMilogLogtailByStoreId(Long storeId) {
        return dao.query(MilogLogTailDo.class, Cnd.where("store_id", EQUAL_OPERATE, storeId));
    }

    public List<MilogLogTailDo> getMilogLogtailByStoreIds(List<Long> ids) {
        return dao.query(MilogLogTailDo.class, Cnd.where("store_id", "in", ids).orderBy("ctime", "desc"));
    }

    public List<MilogLogTailDo> getMilogLogtailByPage(Long storeId, int page, int pagesize) {
        Cnd cnd = Cnd.where("store_id", EQUAL_OPERATE, storeId);
        return dao.query(MilogLogTailDo.class, cnd.orderBy("ctime", "desc"), new Pager(page, pagesize));
    }

    public int getTailCount(Long storeId) {
        Cnd cnd = Cnd.where("store_id", EQUAL_OPERATE, storeId);
        return dao.count(MilogLogTailDo.class, cnd);
    }

    public List<MilogLogTailDo> getMilogLogtailByPage(String appName, int page, int pagesize) {
        Cnd cnd = Cnd.NEW();
        if (!StringUtils.isEmpty(appName)) {
            cnd = cnd.and("app_name", "like", "%" + appName + "%");
        }
        List<MilogLogTailDo> ret = dao.query(MilogLogTailDo.class, cnd.orderBy("ctime", "desc"), new Pager(page, pagesize));
        return ret;
    }

    public Map<Long, List<MilogLogTailDo>> getMilogLogtailByAppId(List<Long> appIds) {
        Map<Long, List<MilogLogTailDo>> ret = new HashMap<>();
        for (Long appId : appIds) {
            List<MilogLogTailDo> logtails = dao.query(MilogLogTailDo.class, Cnd.where("app_id", EQUAL_OPERATE, appId));
            if (logtails != null && logtails.size() > 0) {
                ret.put(appId, logtails);
            }
        }
        return ret;
    }


    public List<MilogLogTailDo> queryByAppIds(Long[] appIds) {
        return dao.query(MilogLogTailDo.class, Cnd.where("app_id", "in", appIds));
    }

    public List<MilogLogTailDo> queryByAppIdAgentIp(Long milogAppId, String agentIp) {
        Sql sql = Sqls.queryEntity("SELECT * FROM milog_logstail WHERE milog_app_id = @milogAppId AND JSON_CONTAINS( ips, JSON_ARRAY( @ip))");
        sql.params().set("milogAppId", milogAppId);
        sql.params().set("ip", agentIp);
        sql.setEntity(dao.getEntity(MilogLogTailDo.class));
        dao.execute(sql);
        return sql.getList(MilogLogTailDo.class);
    }

    public List<MilogLogTailDo> queryByAppAndEnv(Long appId, Long envId) {
        return dao.query(MilogLogTailDo.class, Cnd.where("app_id", EQUAL_OPERATE, appId).and("env_id", EQUAL_OPERATE, envId));
    }

    public List<MilogLogTailDo> queryByMilogAppAndEnv(Long milogAppId, Long envId) {
        return dao.query(MilogLogTailDo.class, Cnd.where("milog_app_id", EQUAL_OPERATE, milogAppId).and("env_id", EQUAL_OPERATE, envId));
    }

    public List<MilogLogTailDo> queryByMilogAppAndEnvId(Long milogAppId, Long envId) {
        Cnd cnd = Cnd.where("milog_app_id", EQUAL_OPERATE, milogAppId);
        if (null != envId) {
            cnd.and("env_id", EQUAL_OPERATE, envId);
        }
        return dao.query(MilogLogTailDo.class, cnd);
    }

    public List<MilogLogTailDo> queryByMilogAppAndEnvK8s(Long milogAppId, Long envId,
                                                         Integer deploy_way) {
        return dao.query(MilogLogTailDo.class,
                Cnd.where("milog_app_id", EQUAL_OPERATE, milogAppId)
                        .and("env_id", EQUAL_OPERATE, envId)
                        .and("deploy_way", EQUAL_OPERATE, deploy_way));
    }

    public MilogLogTailDo queryById(Long id) {
        if (null == id) {
            return null;
        }
        return dao.fetch(MilogLogTailDo.class, id);
    }

    public List<Long> queryAllIds() {
        String idKey = "id";
        List<Record> records = dao.query("milog_logstail", null, null, idKey);
        if (CollectionUtils.isNotEmpty(records)) {
            return records.stream().map(record -> record.getLong(idKey)).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public List<MilogLogTailDo> getLogTailByMilogAppId(Long milogAppId) {
        return dao.query(MilogLogTailDo.class, Cnd.where("milog_app_id", EQUAL_OPERATE, milogAppId));
    }

    public MilogLogTailDo getTailByName(String tailName, Integer appType) {
        return dao.fetch(MilogLogTailDo.class, Cnd.where("tail", EQUAL_OPERATE, tailName)
                .and("app_type", EQUAL_OPERATE, appType));
    }

    public List<MilogLogTailDo> queryByAppType(Integer appType) {
        return dao.query(MilogLogTailDo.class, Cnd.where("app_type", EQUAL_OPERATE, appType));
    }

    public List<MilogLogTailDo> getMilogLogtailByIdsAndName(List<Long> ids, String tail, Integer appType) {
        Cnd cnd = Cnd.where("store_id", "in", ids);
        if (!StringUtils.isEmpty(tail)) {
            cnd.and("tail", "like", "%" + tail + "%");
        }
        if (null != appType) {
            cnd.and("app_type", EQUAL_OPERATE, appType);
        }
        return dao.query(MilogLogTailDo.class, cnd);
    }

    /**
     * 获取ip配置的logtail
     *
     * @param ip
     * @return
     */
    public List<MilogLogTailDo> queryByIp(String ip) {
        Sql sql = Sqls.queryEntity("SELECT * FROM milog_logstail WHERE JSON_CONTAINS(`ips`, JSON_ARRAY( @ip ))");
        sql.params().set("ip", ip);
        sql.setEntity(dao.getEntity(MilogLogTailDo.class));
        dao.execute(sql);
        return sql.getList(MilogLogTailDo.class);
    }

    public int appCount() {
        Sql sql = Sqls.queryRecord("SELECT count(DISTINCT app_id) as access from milog_logstail");
        LinkedList<Record> records = (LinkedList<Record>) dao.execute(sql).getResult();
        int access = records.get(0).getInt("access");
        return access;
    }

    public List<MilogLogTailDo> getAll(String source) {
        Sql sql = Sqls.queryEntity("SELECT ml.* FROM milog_logstail ml LEFT JOIN milog_app_topic_rel mt ON ml.app_id = mt.app_id WHERE mt.source = @source");
        sql.setEntity(dao.getEntity(MilogLogTailDo.class));
        sql.params().set("source", source);
        dao.execute(sql);
        return sql.getList(MilogLogTailDo.class);
    }

    public String queryTailNameByAppIdAndName(String appId, String ip) {
        Sql sql = Sqls.queryString("SELECT tail FROM milog_logstail WHERE app_id=@appId and JSON_CONTAINS(ips, '[\"" + ip + "\"]');");
        sql.params().set("appId", appId);
        dao.execute(sql);
        return sql.getString();
    }

    public List<MilogLogTailDo> queryAppIdByStoreId(Long storeId) {
        return dao.query(MilogLogTailDo.class, Cnd.where("store_id", EQUAL_OPERATE, storeId));
    }

    public List<MilogLogTailDo> getLogTailByLimit(int offset, int rows) {
        String sqlString = String.format("select * from milog_logstail limit %d,%d", offset, rows);
        Sql sql = Sqls.queryEntity(sqlString);
        sql.setEntity(dao.getEntity(MilogLogTailDo.class));
        dao.execute(sql);
        return sql.getList(MilogLogTailDo.class);
    }

    public List<MilogLogTailDo> queryStoreIdByRegionNameEN(String nameEn) {
        Sql sql = Sqls.queryEntity("SELECT * FROM `milog_logstail` where JSON_CONTAINS(motor_rooms, JSON_OBJECT(\"nameEn\", @nameEn))");
        sql.params().set("nameEn", nameEn);
        sql.setEntity(dao.getEntity(MilogLogTailDo.class));
        dao.execute(sql);
        return sql.getList(MilogLogTailDo.class);
    }

    public List<MilogLogTailDo> queryTailNameExists(String tailName, String machineRoom) {
        Sql sql = Sqls.queryEntity("SELECT la.* FROM milog_logstail la LEFT JOIN milog_logstore lt ON la.store_id = lt.id WHERE la.tail = @tailName AND lt.machine_room = @machineRoom");
        sql.params().set("tailName", tailName);
        sql.params().set("machineRoom", machineRoom);
        sql.setEntity(dao.getEntity(MilogLogTailDo.class));
        dao.execute(sql);
        return sql.getList(MilogLogTailDo.class);
    }

    public List<MilogLogTailDo> queryTailsByStoreId(Long storeId) {
        return dao.query(MilogLogTailDo.class, Cnd.where("store_id", EQUAL_OPERATE, storeId));
    }

    public MilogLogTailDo queryTailByMilogAppIdAndIps(Long milogAppId, List<String> ips) {
        List<MilogLogTailDo> MilogLogTailDoList = dao.query(MilogLogTailDo.class, Cnd.where("milog_app_id", EQUAL_OPERATE, milogAppId));
        if (CollectionUtils.isNotEmpty(MilogLogTailDoList)) {
            return MilogLogTailDoList.stream().filter(MilogLogTailDo -> CollectionUtils.isEqualCollection(MilogLogTailDo.getIps(), ips)).findFirst().get();
        }
        return null;
    }

    public Long queryMinTailCountStoreId(Long spaceId, List<Long> storeIdList) {
        Sql sql = Sqls.create("SELECT t.mKey FROM( SELECT count( id) AS mValue, store_id AS mKey FROM milog_logstail WHERE space_id = @spaceId AND store_id IN (" + storeIdList.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") GROUP BY store_id ) t ORDER BY t.mValue LIMIT 1");
        sql.params().set("spaceId", spaceId);
        sql.setCallback((Connection conn, ResultSet rs, Sql sql1) -> {
            Long minCountStoreId = null;
            while (rs.next()) {
                minCountStoreId = rs.getLong(1);
            }
            return minCountStoreId;
        });
        Long object = dao.execute(sql).getObject(Long.class);
        return object;
    }

    public MilogLogTailDo queryServerlessTailByFuncId(Long spaceId, Long storeId, Long milogAppId, Long funcId) {
        List<MilogLogTailDo> logTailDos = dao.query(MilogLogTailDo.class, Cnd.where("space_id", EQUAL_OPERATE, spaceId)
//                .and("store_id", EQUAL_OPERATE, storeId)
                .and("milog_app_id", EQUAL_OPERATE, milogAppId)
                .and("env_id", EQUAL_OPERATE, funcId));
        if (CollectionUtils.isNotEmpty(logTailDos)) {
            return logTailDos.get(logTailDos.size() - 1);
        }
        return null;
    }

    public List<MilogLogTailDo> queryTailsByAppAndStores(Long appId, List<Long> storeIds) {
        return dao.query(MilogLogTailDo.class, Cnd.where("app_id", EQUAL_OPERATE, appId)
                .and("app_type", EQUAL_OPERATE, ProjectTypeEnum.MIONE_TYPE)
                .and("store_id", "in", storeIds));
    }


    /**
     * 查询所有包含的 matrix 类型应用的 logTail
     * //todo sql优化
     */
    public List<MilogLogTailDo> queryTailsByStores(List<Long> storeIds) {
        return dao.query(MilogLogTailDo.class, Cnd.where("store_id", "in", storeIds));
    }

    public List<Integer> queryAllAppId() {
        Sql sql = Sqls.create("SELECT DISTINCT milog_app_id FROM milog_logstail");
        sql.setCallback((Connection conn, ResultSet rs, Sql sql1) -> {
            List<Integer> appBaseIds = Lists.newArrayList();
            while (rs.next()) {
                appBaseIds.add(rs.getInt(1));
            }
            return appBaseIds;
        });
        return dao.execute(sql).getList(Integer.class);
    }

    public List<MilogLogTailDo> queryTailWithAppIdNull() {
        return dao.query(MilogLogTailDo.class, Cnd.where("app_id", "is", null));
    }

    public List<MilogLogTailDo> queryByAppId(Long appId) {
        return dao.query(MilogLogTailDo.class, Cnd.where("app_id", "=", appId));
    }

    public List<MilogLogTailDo> queryByAppId(Long appId, Long milogAppId) {
        return dao.query(MilogLogTailDo.class, Cnd.where("app_id", EQUAL_OPERATE, appId)
                .and("milog_app_id", EQUAL_OPERATE, milogAppId));
    }

    public List<MilogLogTailDo> queryByIds(List<Long> tailIds) {
        return dao.query(MilogLogTailDo.class, Cnd.where("id", "in", tailIds));
    }

    public void updateIps(MilogLogTailDo milogLogtailDo) {
        dao.update(milogLogtailDo, FieldFilter.create(MilogLogTailDo.class, "ips|utime|updater"));
    }

    public List<MilogLogTailDo> queryAppTypeTailByAppId(Long serviceId, Integer typeCode) {
        return dao.query(MilogLogTailDo.class, Cnd.where("app_id", EQUAL_OPERATE, serviceId)
                .and("app_type", EQUAL_OPERATE, typeCode)
                .and("machine_type", EQUAL_OPERATE, MachineTypeEnum.PHYSICAL_MACHINE.getType()));
    }
}
