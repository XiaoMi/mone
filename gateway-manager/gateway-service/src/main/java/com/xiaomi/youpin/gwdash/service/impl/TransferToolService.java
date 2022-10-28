package com.xiaomi.youpin.gwdash.service.impl;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.gwdash.dao.mapper.ApiGroupInfoMapper;
import com.xiaomi.youpin.gwdash.dao.mapper.ApiInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.gwdash.dao.model.transfer.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author goodjava@qq.com
 * @date 2022/10/2 21:53
 */
@Service
@Slf4j
public class TransferToolService {

    private SimpleDataSource ds;

    @Resource
    private ApiInfoMapper mapper;

    @Resource
    private ApiGroupInfoMapper apiGroupInfoMapper;

    @Resource
    private Dao oldDao;

    private Gson gson = new Gson();

    @PostConstruct
    public void init() throws ClassNotFoundException {
        ds = new SimpleDataSource();
        ds.setUsername("");
        ds.setPassword("");
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:80/mone4?characterEncoding=utf8&useSSL=false");
    }


    /**
     * 迁移mysql数据
     * http://127.0.0.1:8088/tool/transfer?num=10000000&tenement=1
     * http://127.0.0.1:8088/tool/transfer?num=20000000&tenement=2
     * 1 中国区  2有品？
     */
    public void transferData(int num, String tenement) {
        //int num = 10000000;
        NutDao dao = new NutDao(ds);
        userCollection(dao, num);
        metadata(dao, num,tenement);
        metadataRelation(dao, num);
        teslaDs(dao, num,tenement);
        apiGroupInfo(num);
        pluginData(dao, num,tenement);
        pluginInfo(dao, num,tenement);
        groupInfo(dao, num,tenement);
        filterInfo(dao, num,tenement);
        userInfo(dao, num,tenement);
        apiInfo(num, dao,tenement);
    }


    private void filterInfo(Dao dao, int num, String tenement) {
        List<FilterInfoBoTransfer> list = oldDao.query(FilterInfoBoTransfer.class, null);
        list.forEach(it -> {
            it.setId(it.getId() + num);
            it.setTenement(tenement);
            dao.insert(it);
        });
        log.info("filterInfo done");
    }


    /**
     * 同步gw_group_info
     *
     * @param dao
     * @param num
     */
    private void groupInfo(Dao dao, int num, String tenement) {
        List<GroupInfoTransfer> list = oldDao.query(GroupInfoTransfer.class, null);
        list.forEach(it -> {
            it.setId(it.getId() + num);
            it.setTenement(tenement);
            dao.insert(it);
        });
        log.info("groupInfo done");
    }

    /**
     * 处理字段中是拼接的
     *
     * @param dao
     * @param num
     */
    private void userInfo(Dao dao, int num, String tenement) {
        oldDao.query(UserInfoTransfer.class, null).forEach(it -> {
            it.setId(it.getId() + num);
            it.setTenement(tenement);
            String gids = it.getGids();
            if (!gids.equals("0")) {
                gids = StreamSupport.stream(Splitter.on("_").split(gids).spliterator(), false)
                        .map(v -> {
                            if (v.equals("0")) {
                                return "0";
                            }
                            return String.valueOf(Integer.valueOf(v) + num);
                        }).collect(Collectors.joining("_"));
                it.setGids(gids);
            }
            dao.insert(it);
        });
        log.info("userInfo done");
    }

    private void apiGroupInfo(int num) {
        JdbcTemplate jt = new JdbcTemplate(this.ds);
        apiGroupInfoMapper.selectByExample(new ApiGroupInfoExample()).forEach(it -> {
            jt.update("insert into api_group_info values(?,?,?,?,?,?,?)",
                    it.getId() + num,
                    it.getName(),
                    it.getDescription(),
                    it.getGid() + num,
                    it.getBaseUrl(),
                    it.getCtime(),
                    it.getUtime());
        });
        log.info("apiGroupInfo done");
    }

    private void teslaDs(Dao dao, int num, String tenement) {
        oldDao.query(TeslaDsTransfer.class, null).forEach(it -> {
            it.setId(it.getId() + num);
            it.setTenement(tenement);
            dao.insert(it);
        });
        log.info("teslaDs done");
    }

    /**
     * 修改 metadata_relation 表
     *
     * @param dao
     * @param num
     */
    private void metadataRelation(Dao dao, int num) {
        oldDao.query(MetaDataRelationTransfer.class, null).forEach(it -> {
            //修改id
            it.setId(it.getId() + num);
            it.setSource(it.getSource() + num);
            it.setTarget(it.getTarget() + num);
            dao.insert(it);
        });
        log.info("metadataRelation done");
    }

    private void metadata(Dao dao, int num, String tenement) {
        oldDao.query(MetaDataTransfer.class, null).forEach(it -> {
            //修改id
            it.setId(it.getId() + num);
            it.setTenement(tenement);
            dao.insert(it);
        });
        log.info("metadata done");
    }

    private void userCollection(Dao dao, int num) {
        oldDao.query(UserCollectionInfoTransfer.class, null).forEach(it -> {
            //修改id
            it.setId(it.getId() + num);
            it.setApiInfoId(it.getApiInfoId() + num);
            dao.insert(it);
        });
        log.info("userCollection done");
    }

    private void pluginInfo(Dao dao, int num, String tenement) {
        oldDao.query(PluginInfoBoTransfer.class, null).forEach(it -> {
            //修改id
            it.setId(it.getId() + num);
            it.setDataId(it.getDataId() + num);
            it.setTenant(tenement);
            dao.insert(it);
        });
        log.info("pluginInfo done");
    }

    private void pluginData(Dao dao, int num, String tenement) {
        oldDao.query(PluginDataTransfer.class, null).forEach(it -> {
            //修改id
            it.setId(it.getId() + num);
            it.setTenant(tenement);
            dao.insert(it);
        });
        log.info("pluginData done");
    }

    /**
     * 处理字段中是json的
     *
     * @param num
     * @param dao
     */
    private void apiInfo(int num, NutDao dao, String tenement) {
        log.info("updateApiInfo start");
        oldDao.query(ApiInfoTransfer.class, null).forEach(it -> {
            log.info("insert apiinfo:{}", it.getId());
            it.setId(it.getId() + num);
            it.setGroupId(it.getGroupId() + num);

            String filterParams = it.getFilterParams();
            JsonArray jsonArray = gson.fromJson(filterParams, JsonArray.class);
            if (jsonArray!=null){
                jsonArray.forEach(o -> {
                    JsonObject obj = o.getAsJsonObject();
                    obj.addProperty("id", obj.get("id").getAsInt() + num);
                });
                filterParams = gson.toJson(jsonArray);
            } else {
                filterParams = "";
            }
            String dsIds = it.getDsIds();
            if (StringUtils.isNotBlank(dsIds)){
                StringBuilder sb = new StringBuilder();
                Splitter.on(",").split(dsIds).forEach(id -> sb.append(Integer.valueOf(id) + num).append(","));
                it.setDsIds(sb.substring(0, sb.length()-1));
            }

            it.setFilterParams(filterParams);
            it.setTenement(tenement);
            dao.insert(it);
        });
        log.info("updateApiInfo done");
    }

}
