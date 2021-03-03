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

package com.xiaomi.youpin.quota.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.youpin.quota.bo.Resource;
import com.xiaomi.youpin.quota.bo.ResourceBo;
import com.xiaomi.youpin.quota.bo.Result;
import com.xiaomi.youpin.quota.dao.ResourceDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.quota.bo.QuotaConst.RESOURCE_LEVEL_INIT;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service(interfaceClass = ResourceService.class, group = "${dubbo.group}")
public class ResourceServiceImpl implements ResourceService {

    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    private Dao dao;

    @Autowired
    private ResourceDao resourceDao;

    @Override
    public Result<List<ResourceBo>> list() {
        List<Resource> res = dao.query(Resource.class,Cnd.orderBy().desc("remain_cpu") );
        return new Result(res.stream().map(it -> {
            ResourceBo bo = new ResourceBo();
            bo.setIp(it.getIp());
            bo.setCpu(it.getCpu());
            return bo;
        }));
    }

    @Override
    public Result<Map<String, Object>> list(int page, int pageSize, int status, HashMap<String, String> map) {
        String ip = null;
        String owner = null;
        String ORDER_BY_R_CPU=" order by remain_cpu desc";
        if (map.containsKey("ip")) {
            ip = map.get("ip");
        }
        if (map.containsKey("owner")) {
            owner = map.get("owner");
        }

        List<Resource> list;
        if (!StringUtils.isEmpty(ip) && !StringUtils.isEmpty(owner)) {
            String sqlStr = "select * from resource where owners like \"%%" + owner + "%%\"  and ip=@ip"+ORDER_BY_R_CPU+"  limit @pageSize offset @offset";
            Sql sql = Sqls.create(sqlStr);
            sql.params().set("offset", (page - 1) * pageSize).set("pageSize", pageSize).set("ip", ip);
            sql.setCallback(Sqls.callback.entities());
            sql.setEntity(dao.getEntity(Resource.class));
            list = dao.execute(sql).getList(Resource.class);
        } else if (!StringUtils.isEmpty(ip)) {
            list = dao.query(Resource.class, Cnd.where("ip", "=", ip), new Pager(page, pageSize));
        } else if (!StringUtils.isEmpty(owner)) {
            String sqlStr = "select * from resource where owners like \"%%" + owner + "%%\""+ORDER_BY_R_CPU+" limit @pageSize offset @offset";
            Sql sql = Sqls.create(sqlStr);
            sql.params().set("offset", (page - 1) * pageSize).set("pageSize", pageSize);
            sql.setCallback(Sqls.callback.entities());
            sql.setEntity(dao.getEntity(Resource.class));
            list = dao.execute(sql).getList(Resource.class);
        } else {
            list = dao.query(Resource.class, Cnd.orderBy().desc("remain_cpu"), new Pager(page, pageSize));
        }
        List<ResourceBo> resourceBoList = list.stream().map(it -> {
            return this.adapterResourceBo(it);
        }).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("resourceList", resourceBoList);

        int total;
        if (!StringUtils.isEmpty(ip) && !StringUtils.isEmpty(owner)) {
            String sqlStr = "select count(*) as count from resource where owners like \"%%" + owner + "%%\"  and ip=@ip"+ORDER_BY_R_CPU;
            Sql sql = Sqls.create(sqlStr);
            sql.params().set("ip", ip);
            sql.setCallback(new SqlCallback() {
                public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                    while (rs.next()) {
                        return rs.getInt("count");
                    }
                    return 0;
                }
            });
            total = dao.execute(sql).getInt();
        } else if (!StringUtils.isEmpty(ip)) {
            total = dao.count("resource", Cnd.where("ip", "=", ip));
        } else if (!StringUtils.isEmpty(owner)) {
            String sqlStr = "select count(*) as count from resource where owners like \"%%" + owner + "%%\""+ORDER_BY_R_CPU;
            Sql sql = Sqls.create(sqlStr);
            sql.setCallback(new SqlCallback() {
                public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                    while (rs.next()) {
                        return rs.getInt("count");
                    }
                    return 0;
                }
            });
            total = dao.execute(sql).getInt();
        } else {
            total = dao.count("resource", null);
        }

        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return new Result<>(result);
    }

    private ResourceBo adapterResourceBo(Resource resource) {
        ResourceBo bo = new ResourceBo();
        bo.setIp(resource.getIp());
        bo.setCpu(resource.getCpu());
        bo.setMem(resource.getMem());
        bo.setPorts(new HashSet<>(resource.getPorts()));
        bo.setName(resource.getName());
        bo.setHostName(resource.getHostName());
        bo.setLables(resource.getLabels());
        bo.setId(resource.getId());
        bo.setType(resource.getType());
        bo.setRemainCpu(resource.getRemainCpu());
        bo.setSystemCpu(resource.getSystemCpu());
        bo.setRemainMem(resource.getRemainMem());
        bo.setSystemMem(resource.getSystemMem());
        bo.setSystemPorts(resource.getSystemPorts());
        bo.setOwners(resource.getOwners());
        bo.setLevel(resource.getLevel());
        bo.setPrice(resource.getPrice());
        bo.setLoadAverage(resource.getLoadAverage());
        bo.setRorder(resource.getRorder());
        bo.setUtime(resource.getUtime());
        bo.setStatus(resource.getStatus());
        bo.setSupportKeyCenter(resource.getSupportKeyCenter());
        bo.setIsOneApp(resource.getIsOneApp());
        bo.setBizIds(resource.getBizIds());
        return bo;
    }

    @Override
    public Result<ResourceBo> getResourceByIp(String ip) throws Exception {
        Resource data = dao.fetch(Resource.class, Cnd.where("ip", "=", ip));
        if (data == null) {
            throw new Exception("该记录已经不存在");
        }
        return new Result<ResourceBo>(adapterResourceBo(data));
    }

    @Override
    public Result<List<ResourceBo>> getResourceByEnvId(int envId) {
        List<Resource> list = resourceDao.getResourceByEnvId(envId);

        if (list == null || list.size() == 0) {
            return new Result(Lists.newArrayList());
        }

        List<ResourceBo> res = list.stream().map(it -> adapterResourceBo(it)).collect(Collectors.toList());
        return new Result(res);
    }

    @Override
    public Result<List<ResourceBo>> getResourceByProjectId(int projectId) {
        List<Resource> list = resourceDao.getResourceByProjectId(projectId);

        if (list == null || list.size() == 0) {
            return new Result(Lists.newArrayList());
        }

        List<ResourceBo> res = list.stream().map(it -> adapterResourceBo(it)).collect(Collectors.toList());
        return new Result(res);
    }

    @Override
    public Result<Boolean> updateOrderById(int id, int rorder) throws Exception {
        Resource data = dao.fetch(Resource.class, Cnd.where("id", "=", id));
        if (data == null) {
            throw new Exception("该记录已经不存在");
        }

        dao.update(Resource.class, Chain.make("rorder", rorder), Cnd.where("id", "=", id));
        return new Result<>(true);
    }

    @Transactional("masterTransactionManager")
    @Override
    public Result<Boolean> updateResource(ResourceBo resourceBo) {
        lock.lock();
        try {
            Resource data = dao.fetch(Resource.class, Cnd.where("ip", "=", resourceBo.getIp()));
            Integer systemMem = this.getSystemMem(resourceBo);
            List<Integer> systemCpus = this.getSystemCpus(resourceBo);
            List<Integer> systemPorts = this.getSystemPorts(resourceBo);
            if (null == data) {
                Resource resource = new Resource();
                resource.setIp(resourceBo.getIp());
                resource.setCpu(resourceBo.getCpu());
                resource.setSystemCpu(systemCpus);
                resource.setRemainCpu(resourceBo.getCpu() - systemCpus.size());
                resource.setMem(resourceBo.getMem());
                resource.setSystemMem(systemMem);
                resource.setRemainMem(resourceBo.getMem() - systemMem);
                resource.setBizIds(Maps.newHashMap());
                resource.setProjectIds(Lists.newArrayList());
                resource.setPorts(Lists.newArrayList());
                resource.setSystemPorts(Lists.newArrayList(systemPorts));
                resource.setOwners(this.getOwners(resourceBo));
                resource.setLevel(this.getLevel(resourceBo));
                resource.setIsOneApp(this.getIsOneApp(resourceBo));
                resource.setSupportKeyCenter(this.getKeyCenter(resourceBo));
                resource.setLoadAverage(resourceBo.getLoadAverage());
                resource.setType(this.getType(resourceBo));
                dao.insert(resource);
            } else {
                dao.update(Resource.class,
                        Chain.make("labels", new Gson().toJson(resourceBo.getLables()))
                                .add("cpu", resourceBo.getCpu())
                                .add("system_cpu", systemCpus)
                                .add("mem", resourceBo.getMem())
                                .add("system_mem", systemMem)
                                .add("owners", Lists.newArrayList(this.getOwners(resourceBo)))
                                .add("level", this.getLevel(resourceBo))
                                .add("type", this.getType(resourceBo))
                                .add("is_oneapp", this.getIsOneApp(resourceBo))
                                .add("support_key_center", this.getKeyCenter(resourceBo))
                                .add("load_average", resourceBo.getLoadAverage())
                                .add("system_ports", systemPorts),
                        Cnd.where("ip", "=", resourceBo.getIp()));
            }
            return new Result<>(true);
        } finally {
            lock.unlock();
        }
    }

    private ArrayList<String> getOwners(ResourceBo resourceBo) {
        if (resourceBo.getLables() != null) {
            ArrayList<String> owners = StringUtils.isEmpty(resourceBo.getLables().get("owners"))
                    ? Lists.newArrayList()
                    : Lists.newArrayList(resourceBo.getLables().get("owners").split(","));
            return owners;
        }
        return Lists.newArrayList();
    }

    private Integer getLevel(ResourceBo resourceBo) {
        if (Optional.ofNullable(resourceBo.getLables()).isPresent()) {
            int level = StringUtils.isEmpty(resourceBo.getLables().get("level"))
                    ? RESOURCE_LEVEL_INIT
                    : Integer.valueOf(resourceBo.getLables().get("level"));
            return level;
        }
        return RESOURCE_LEVEL_INIT;
    }

    private String getType(ResourceBo resourceBo) {
        if (Optional.ofNullable(resourceBo.getLables()).isPresent()) {
            String type = StringUtils.isEmpty(resourceBo.getLables().get("type"))
                    ? ""
                    : resourceBo.getLables().get("type");
            return type;
        }
        return "";
    }

    private Integer getIsOneApp(ResourceBo resourceBo) {
        if (Optional.ofNullable(resourceBo.getLables()).isPresent()) {
            int isOneApp = StringUtils.isEmpty(resourceBo.getLables().get("oneapp"))
                    ? 0
                    : (resourceBo.getLables().get("oneapp").equals("true") ? 1 : 0);
            return isOneApp;
        }
        return 0;
    }

    private int getKeyCenter(ResourceBo resourceBo) {
        if (resourceBo.getLables() != null) {
            int keyCenter = StringUtils.isEmpty(resourceBo.getLables().get("keycenter"))
                    ? 0
                    : (resourceBo.getLables().get("keycenter").equals("true") ? 1 : 0);
            return keyCenter;
        }
        return 0;
    }

    private List<Integer> getSystemCpus(ResourceBo resourceBo) {
        if (resourceBo.getLables() != null) {
            ArrayList<String> systemCpu = StringUtils.isEmpty(resourceBo.getLables().get("system_cpus"))
                    ? Lists.newArrayList()
                    : Lists.newArrayList(resourceBo.getLables().get("system_cpus").split(","));
            return systemCpu.stream().map(it -> Integer.valueOf(it)).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private List<Integer> getSystemPorts(ResourceBo resourceBo) {
        if (resourceBo.getLables() != null) {
            ArrayList<String> systemPort = StringUtils.isEmpty(resourceBo.getLables().get("system_ports"))
                    ? Lists.newArrayList()
                    : Lists.newArrayList(resourceBo.getLables().get("system_ports").split(","));
            return systemPort.stream().map(it -> Integer.valueOf(it)).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private Integer getSystemMem(ResourceBo resourceBo) {
        if (resourceBo.getLables() != null && !StringUtils.isEmpty(resourceBo.getLables().get("system_mem"))) {
            return Integer.valueOf(resourceBo.getLables().get("system_mem"));
        }

        return 0;
    }

    @Override
    public Result<Long> getPrice(String ip) {
        try {
            ResourceBo resource = getResourceByIp(ip).getData();
            return new Result(0, "", resource.getPrice() / resource.getCpu());
        } catch (Exception e) {
            log.error("get price exception: ", e);
        }
        return new Result(0, "", 0);
    }

    @Override
    public Result<Integer> setPrice(String ip, long price) {
        return new Result(0,"", dao.update("resource", Chain.make("price", price), Cnd.where("ip", "=", ip)));
    }

}
