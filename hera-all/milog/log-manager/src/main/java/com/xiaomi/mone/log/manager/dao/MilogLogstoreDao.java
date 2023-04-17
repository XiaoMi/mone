package com.xiaomi.mone.log.manager.dao;

import cn.hutool.core.bean.BeanUtil;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.manager.model.bo.MilogLogstoreBo;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.EQUAL_OPERATE;
import static com.xiaomi.mone.log.common.Constant.LIKE_OPERATE;

@Service
public class MilogLogstoreDao {

    @Resource
    private NutDao dao;

    public boolean newMilogLogStore(MilogLogStoreDO ml) {
        MilogLogStoreDO ret = dao.insert(ml);
        if (ret == null) {
            return false;
        } else {
            return true;
        }
    }

    public MilogLogStoreDO insert(MilogLogStoreDO logStoreDO) {
        return dao.insert(logStoreDO);
    }

    public boolean updateMilogLogStore(Long id, Long spaceId, String logstoreName,
                                       Integer storePeriod, Integer shardCnd, String keyList,
                                       Integer logType, String machineRoom, String updater) {
        Long utime = System.currentTimeMillis();
        int ret = dao.update(MilogLogStoreDO.class, Chain.make("space_id", spaceId)
                .add("logstoreName", logstoreName).add("store_period", storePeriod)
                .add("shard_cnt", shardCnd).add("key_list", keyList)
                .add("machine_room", machineRoom).add("updater", updater)
                .add("log_type", logType).add("utime", utime), Cnd.where("id", EQUAL_OPERATE, id));
        if (ret != 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateMilogLogStore(MilogLogStoreDO ml) {
        return 1 == dao.update(ml) ? true : false;
    }


    public boolean deleteMilogSpace(Long id) {
        int ret = dao.clear(MilogLogStoreDO.class, Cnd.where("id", EQUAL_OPERATE, id));
        if (ret != 1) {
            return false;
        } else {
            return true;
        }
    }

    public List<MilogLogStoreDO> getMilogLogstore(List<Long> ids) {
        return dao.query(MilogLogStoreDO.class, Cnd.where("id", "in", ids));
    }

    public List<MilogLogStoreDO> getMilogLogstoreBySpaceId(Long spaceId) {
        return dao.query(MilogLogStoreDO.class, Cnd.where("space_id", EQUAL_OPERATE, spaceId).orderBy("ctime", "desc"));
    }

    public List<MilogLogStoreDO> getMilogLogstoreBySpaceId(List<Long> spaceIdList) {
        return dao.query(MilogLogStoreDO.class, Cnd.where("space_id", "in", spaceIdList).orderBy("ctime", "desc"));
    }

    public List<MilogLogStoreDO> getMilogLogstoreBySpaceIdRegion(Long spaceId, String region) {
        return dao.query(MilogLogStoreDO.class, Cnd.where("space_id", EQUAL_OPERATE, spaceId).and("machine_room", EQUAL_OPERATE, region));
    }

    public Map<String, Object> getMilogLogstoreByPage(String logsotreName, Long spaceId, int page, int pagesize) {
        Cnd cnd = Cnd.where("space_id", "=", spaceId);
        if (StringUtils.isNotEmpty(logsotreName)) {
            cnd = cnd.and("logstoreName", LIKE_OPERATE, "%" + logsotreName + "%");
        }
        Map<String, Object> result = new HashMap<>();
        List<MilogLogStoreDO> ret = dao.query(MilogLogStoreDO.class, cnd.orderBy("utime", "desc"), new Pager(page, pagesize));
        if (CollectionUtils.isNotEmpty(ret)) {
            List<MilogLogstoreBo> logstoreBos = ret.stream().map(milogLogstoreDO -> {
                MilogLogstoreBo milogLogstoreBo = new MilogLogstoreBo();
                BeanUtil.copyProperties(milogLogstoreDO, milogLogstoreBo);
                milogLogstoreBo.setLogTypeText(LogTypeEnum.queryNameByType(milogLogstoreDO.getLogType()));
                return milogLogstoreBo;
            }).collect(Collectors.toList());
            result.put("list", logstoreBos);
        }

        result.put("total", dao.count(MilogLogStoreDO.class, cnd));
        result.put("page", page);
        result.put("pageSize", pagesize);
        return result;
    }

    public Map<String, Object> getAllMilogLogstore(String source) {
        List<MilogLogStoreDO> ret = dao.query(MilogLogStoreDO.class, null);
        ret = ret.stream().filter(milogLogstoreDO -> {
            MilogSpaceDO milogSpace = dao.fetch(MilogSpaceDO.class, milogLogstoreDO.getSpaceId());
            if (milogSpace.getSource().equals(source)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("list", ret);
        return result;
    }

    public List<MilogLogStoreDO> getAllMilogLogstore() {
        return dao.query(MilogLogStoreDO.class, null);
    }

    public MilogLogStoreDO getByName(String logstoreName) {
        List<MilogLogStoreDO> queryByName = dao.query(MilogLogStoreDO.class, Cnd.where("logstoreName", EQUAL_OPERATE, logstoreName));
        return queryByName == null || queryByName.isEmpty() ? null : queryByName.get(0);
    }

    public MilogLogStoreDO queryById(Long id) {
        return dao.fetch(MilogLogStoreDO.class, id);
    }

    public String getAppIndex(Long appId, String ip) {
        String sqlStr = "SELECT s.es_index FROM milog_logstore s, milog_logstail t WHERE t.store_id = s.id and t.app_id = @appId AND JSON_CONTAINS(t.ips, '\"" + ip + "\"')";
        Sql sql = Sqls.create(sqlStr);
        sql.params().set("appId", appId);
        sql.setCallback(Sqls.callback.str());
        dao.execute(sql);
        return sql.getString();
    }

    public boolean verifyExistByName(String logstoreName, Long id) {
        Cnd cnd = Cnd.where("logstoreName", EQUAL_OPERATE, logstoreName);
        if (null != id) {
            cnd.andNot("id", EQUAL_OPERATE, id);
        }
        int count = dao.count(MilogLogStoreDO.class, cnd);
        return count > 0;
    }

    public List<MilogLogStoreDO> queryStoreBySpaceStoreNameVague(Long spaceId, String storeNamePrefix, String machineRoom) {
        Cnd cnd = Cnd.where("machine_room", EQUAL_OPERATE, machineRoom);
        cnd.and("space_id", EQUAL_OPERATE, spaceId);
        if (StringUtils.isNotBlank(storeNamePrefix)) {
            cnd.and("logstoreName", LIKE_OPERATE, String.format("%s%s", storeNamePrefix.trim(), "%"));
        }
        cnd.orderBy("ctime", "desc");
        return dao.query(MilogLogStoreDO.class, cnd);
    }

    public MilogLogStoreDO queryStoreBySpaceStoreName(Long spaceId, String storeName, String machineRoom) {
        Cnd cnd = Cnd.where("machine_room", EQUAL_OPERATE, machineRoom);
        cnd.and("space_id", EQUAL_OPERATE, spaceId);
        if (StringUtils.isNotBlank(storeName)) {
            cnd.and("logstoreName", EQUAL_OPERATE, storeName);
        }
        List<MilogLogStoreDO> logStoreDOS = dao.query(MilogLogStoreDO.class, cnd);
        if (CollectionUtils.isNotEmpty(logStoreDOS)) {
            return logStoreDOS.get(logStoreDOS.size() - 1);
        }
        return null;
    }

    public List<MilogLogStoreDO> queryByIds(List<Long> storeIds) {
        Cnd cnd = Cnd.where("id", "in", storeIds);
        return dao.query(MilogLogStoreDO.class, cnd);
    }

    public List<MilogLogStoreDO> queryAll() {
        return dao.query(MilogLogStoreDO.class, Cnd.NEW());
    }

    public List<MilogLogStoreDO> queryByEsInfo(String regionEn, Long esClusterId) {
        return dao.query(MilogLogStoreDO.class, Cnd.where("machine_room", EQUAL_OPERATE, regionEn).and("es_cluster_id", EQUAL_OPERATE, esClusterId));
    }

    public List<MilogLogStoreDO> queryByLogType(Integer type) {
        return dao.query(MilogLogStoreDO.class, Cnd.where("log_type", EQUAL_OPERATE, type));
    }
}
