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

package com.xiaomi.youpin.quota.dao;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.youpin.quota.bo.*;
import com.xiaomi.youpin.quota.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Repository
public class ResourceDao {


    @Autowired
    private Dao dao;

    @Autowired
    private ConfigService configService;

    private static final String CPU_METRICS = "CPU";
    private static final String CPU_MEM_METRICS = "CPU_MEM";
    private static final String MEM_METRICS = "MEM";

    /**
     * 获取可以使用的cpu
     *
     * @param resource
     * @param num
     * @return
     */
    public List<Integer> getCpu(Resource resource, int num) {
        Map<Long, BizResource> map = resource.getBizIds();

        if (null == map) {
            map = Maps.newHashMap();
        }

        //找到所有被占用的cpu
        List<Integer> cpulist = map.values().stream().map(it -> it.getCpus()).flatMap(Collection::stream).collect(Collectors.toList());
        //找到系统占用的cpu
        if (resource.getSystemCpu() != null && resource.getSystemCpu().size() != 0) {
            cpulist.addAll(resource.getSystemCpu());
        }
        List<Integer> selectCpu = IntStream.range(0, resource.getCpu()).filter(it -> !cpulist.contains(it)).limit(num).mapToObj(it -> it).collect(Collectors.toList());
        return selectCpu;
    }

    public List<Resource> getResourceByEnvId(int envId) {
        Sql sql = Sqls.create("select * from resource where biz_ids->'$.\"$id\"' is not null");
        sql.vars().set("id", envId);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Resource.class));

        List<Resource> list = dao.execute(sql).getList(Resource.class);

        return list;
    }

    public List<Resource> getResourceByProjectId(int projectId) {
        String sqlStr = "select * from resource where JSON_CONTAINS(project_ids,'" + projectId + "','$') != 0";
        Sql sql = Sqls.create(sqlStr);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Resource.class));
        List<Resource> list = dao.execute(sql).getList(Resource.class);

        return list;
    }


    public void addBizId(int rid, long bizId, int cpu, long mem, Set<Integer> ports) {
        Resource resource = dao.fetch(Resource.class, rid);
        List<Integer> selectCpu = getCpu(resource, cpu);

        BizResource bizResource = new BizResource();
        bizResource.setBizId(bizId);
        bizResource.setCpus(selectCpu);
        bizResource.setMem(mem);
        bizResource.setPorts(new ArrayList<>(ports));

        Sql sql = Sqls.create("update resource set biz_ids=json_insert(biz_ids,'$.\"$id\"',@br) where id = @id");
        sql.vars().set("id", bizId);
        sql.params()
                .set("id", rid)
                .set("br", new Gson().toJson(bizResource));
        dao.execute(sql);
    }


    public void removeBizId(int rid, long bizId, int cpu, long mem, Set<Integer> ports, long projectId) {
        Resource resource = dao.fetch(Resource.class, rid);
        String updateProjectId = "";
        if (projectId > 0) {
            resource.getProjectIds().remove(projectId);
            updateProjectId = "project_ids='" + new Gson().toJson(resource.getProjectIds()) + "',";
        }
        Sql sql = Sqls.create("update resource set remain_cpu=remain_cpu+@cpu,remain_mem=remain_mem+@mem,ports=@ports," + updateProjectId + "biz_ids=json_remove(biz_ids,'$.\"$id\"') where id = @id");
        sql.vars().set("id", bizId);
        if (null != ports) {
            resource.getPorts().removeAll(ports);
        }
        sql.params().set("id", rid).set("cpu", cpu).set("mem", mem).set("ports", new Gson().toJson(resource.getPorts()));
        dao.execute(sql);
    }


    /**
     * 用来返回最好的K个resources.
     * 从nacos获得配置.根据配置返回筛选模式
     *
     * @return 返回sql语句
     */
    private String topKResources() {
        String metrics = configService.getTopKMetrics();
        String desc = configService.isDesc() ? "desc" : "asc";
        if (StringUtils.isEmpty(metrics)) {
            return cpuMetrics(desc);
        }

        switch (metrics) {
            case CPU_MEM_METRICS:
                return cpuMemMetrics(desc);
            case MEM_METRICS:
                return memMetrics(desc);
            default:
                return cpuMetrics(desc);
        }
    }

    /**
     * 剩余cpu/总cpu数量的比例大的优先。
     * * 如果相等，则剩余cpu多的优先。
     *
     * @param desc sql order by　的排序顺序
     * @return
     */
    private String cpuMetrics(String desc) {
        return String.format("remain_cpu / CAST(cpu as decimal) %s, remain_cpu %s", desc, desc);
    }

    /**
     * 综合CPU和memory指标
     *
     * @param desc 　sql order by　的排序顺序
     * @return
     */
    private String cpuMemMetrics(String desc) {
        return String.format("remain_cpu / CAST(cpu as decimal)  + remain_mem / CAST(mem as decimal) %s, remain_cpu %s, remain_mem %s", desc, desc, desc);
    }

    /**
     * 剩余memory/总memory数量的比例大的优先。
     * * 如果相等，则剩余memory多的优先。
     *
     * @param desc 　sql order by　的排序顺序
     * @return
     */
    private String memMetrics(String desc) {
        return String.format("remain_mem / CAST(mem as decimal) %s, remain_mem %s", desc, desc);
    }


    public List<Resource> list(int cpu, long mem, Set<Integer> ports, long projectId, long bizId, String owner, int level, boolean supportKeyCenter, int num, Map<String, String> labels) {
        return list(cpu, mem, ports, projectId, bizId, owner, level, supportKeyCenter, num, labels, new ResourceListReq());
    }


    /**
     * 寻找可以使用的资源
     * <p>
     * 条件:
     * cpu 核数够
     * mem 内存
     * ports 端口
     * projectId 项目id
     * bizId 项目下的部署配置id
     * owner 拥有者
     * level 级别
     * supportKeyCenter 是否支持keycenter
     * 这台机器之前没有部署此业务容器
     * 这台机器不是专属机器
     * 指定target ip
     *
     * @param cpu
     * @param bizId
     * @return
     */
    public List<Resource> list(int cpu, long mem, Set<Integer> ports, long projectId, long bizId, String owner, int level, boolean supportKeyCenter, int num, Map<String, String> labels, ResourceListReq req) {
        String portStr = "";
        String portStr1 = "";

        //请求的端口号不能已经被占用
        for (Integer it : ports) {
            portStr = portStr + " and JSON_CONTAINS(ports,'" + it + "','$') = 0";
            portStr1 = portStr1 + " and JSON_CONTAINS(system_ports,'" + it + "','$') = 0";
        }

        //支持keycenter
        String keyCenterStr = "";
        if (supportKeyCenter) {
            keyCenterStr = " and support_key_center=1";
        }

        //每个项目在每台机器上只允许部署一次
        String projectIdStr = "";
        if (projectId > 0) {
            projectIdStr = " and JSON_CONTAINS(project_ids,'" + projectId + "','$') = 0";
        }


        //支持直接指定ip地址(直接选择给定的ip地址)
        String targetIp = "";
        if (StringUtils.isNotEmpty(req.getTargetIp())) {
            targetIp = " and ip = @ip";
        }


        //资源支持owner的概念，优先匹配owner
        String ownerStr = "";
        if (!StringUtils.isEmpty(owner)) {
            ownerStr = " JSON_CONTAINS(owners, json_array(\"" + owner + "\"),'$') desc,";
        }


        //支持独占机器的概念，is_oneapp=1代表独占，独占机器谁抢到是谁的
        String oneAppStr = " and (is_oneapp = 0 or (is_oneapp = 1 and JSON_LENGTH(biz_ids) = 0))";

        //目前只筛选type=docker的resource
        String typeStr = " and type = \"" + QuotaConst.RESOURCE_TYPE_DOCKER + "\"";

        String select = "select * from resource";

        //构造where 语句
        String where = new StringBuilder()
                .append(" where")
                //check cpu
                .append(" remain_cpu >= @remain_cpu")
                //check mem
                .append(" and remain_mem >= @remain_mem")
                //check 应用level
                .append(" and level <= @level")
                //check 上边是否已经安装此应用
                .append(" and biz_ids->'$.\"$id\"' is null")
                .append(portStr)
                .append(portStr1)
                .append(projectIdStr)
                .append(keyCenterStr)
                .append(targetIp)
                .append(oneAppStr)
                .append(typeStr)
                .append(" and status=0 and cpu >0")
                .toString();

        String orderBy = new StringBuilder()
                .append(" order by ")
                .append(ownerStr)
                .append(" level desc, JSON_LENGTH(owners), rorder desc, ")
                .append(topKResources()).toString();

        String sqlStr = select + where + orderBy + " limit @num";

        Sql sql = Sqls.create(sqlStr);
        sql.vars().set("id", bizId);
        sql.params().set("remain_cpu", cpu).set("remain_mem", mem).set("num", num).set("level", level);
        if (StringUtils.isNotEmpty(req.getTargetIp())) {
            sql.params().set("ip", req.getTargetIp());
        }

        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Resource.class));
        List<Resource> list = dao.execute(sql).getList(Resource.class);

        log.info("ResourceDao.list, bizId: {}, resourceList: {}, sql: {}", bizId, list, sql);

        return list;
    }

    /**
     * 降级配置,目前只支持cpu,mem
     * <p>
     *
     * @param bizId
     * @param decrCpu
     * @return
     */
    public boolean downgrade(long bizId, int decrCpu, long decrMem) {
        List<Quota> quotas = dao.query(Quota.class, Cnd.where("biz_id", "=", bizId));
        List<Integer> rids = quotas.stream().map(it -> it.getResourceId()).collect(Collectors.toList());
        List<Resource> resources = rids.stream()
                .map(rid -> dao.fetch(Resource.class, rid))
                .collect(Collectors.toList());
        //更新quota
        updateQuotaDecr(decrCpu, decrMem, quotas);
        //更新资源
        updateResourceDecr(bizId, decrCpu, decrMem, resources);
        //更新quota_request
        updateQuotaRequestDecr(bizId, decrCpu, decrMem);
        log.info("upgrade :{} {} success", bizId, decrCpu);
        return true;
    }

    /**
     * 升级配置,目前只支持cpu,mem
     * <p>
     * 已占用的全部resource 都支持 升级,才能升级
     *
     * @param bizId
     * @param incrCpu
     * @return
     */
    public boolean upgrade(long bizId, int incrCpu, long incrMem) {
        List<Quota> quotas = dao.query(Quota.class, Cnd.where("biz_id", "=", bizId));
        List<Integer> rids = quotas.stream().map(it -> it.getResourceId()).collect(Collectors.toList());
        List<Resource> resources = rids.stream().map(rid -> dao.fetch(Resource.class, rid)).collect(Collectors.toList());

        int finalIncrCpu = incrCpu;
        long finalIncrMem = incrMem;
        if (incrCpu > 0) {
            List<Resource> list = resources.stream()
                    .filter(it -> it.getRemainCpu() >= incrCpu)
                    .collect(Collectors.toList());
            //全部都符合标准,才可以升级
            if (list.size() != rids.size()) {
                finalIncrCpu = 0;
            }
        }
        if (incrMem > 0) {
            List<Resource> list = resources.stream()
                    .filter(it -> it.getRemainMem() >= incrMem)
                    .collect(Collectors.toList());
            //全部都符合标准,才可以升级
            if (list.size() != rids.size()) {
                finalIncrMem = 0L;
            }
        }


        if (finalIncrCpu > 0 || finalIncrMem > 0) {
            //更新quota
            updateQuotaIncr(finalIncrCpu, finalIncrMem, quotas);
            //更新资源
            updateResourceIncr(bizId, finalIncrCpu, finalIncrMem, resources);
            //更新quota_request
            updateQuotaRequestIncr(bizId, finalIncrCpu, finalIncrMem);
            return true;
        }

        return false;
    }

    private void updateQuotaRequestIncr(long bizId, int incrCpu, long incrMem) {
        QuotaRequest request = dao.fetch(QuotaRequest.class, Cnd.where("biz_id", "=", bizId));
        if (incrCpu > 0) {
            request.setCpu(request.getCpu() + incrCpu);
        }
        if (incrMem > 0) {
            request.setMem(request.getMem() + incrMem);
        }
        dao.update(request);
    }

    private void updateQuotaRequestDecr(long bizId, int decrCpu, long decrMem) {
        QuotaRequest request = dao.fetch(QuotaRequest.class, Cnd.where("biz_id", "=", bizId));
        if (decrCpu > 0) {
            request.setCpu(request.getCpu() - decrCpu);
        }
        if (decrMem > 0) {
            request.setMem(request.getMem() - decrMem);
        }
        dao.update(request);
    }

    private void updateResourceIncr(long bizId, int incrCpu, long incrMem, List<Resource> resources) {
        resources.forEach(it -> {
            if (incrCpu > 0) {
                it.setRemainCpu(it.getRemainCpu() - incrCpu);
                Map<Long, BizResource> bizMap = it.getBizIds();
                BizResource v = bizMap.get(bizId);
                List<Integer> cpus = this.getCpu(it, incrCpu);
                v.getCpus().addAll(cpus);
            }
            if (incrMem > 0) {
                it.setRemainMem(it.getRemainMem() - incrMem);
                Map<Long, BizResource> bizMap = it.getBizIds();
                BizResource v = bizMap.get(bizId);
                v.setMem(v.getMem() + incrMem);
            }
            dao.update(it);
        });
    }

    private void updateResourceDecr(long bizId, int decrCpu, long decrMem, List<Resource> resources) {
        resources.forEach(it -> {
            if (decrCpu > 0) {
                it.setRemainCpu(it.getRemainCpu() + decrCpu);
                Map<Long, BizResource> bizMap = it.getBizIds();
                BizResource v = bizMap.get(bizId);
                //min 是容错处理,之后需要去掉(数据出现问题了)

                int num = Math.abs(v.getCpus().size() - decrCpu);
                List<Integer> list = v.getCpus().subList(0, Math.min(num, v.getCpus().size()));

                v.setCpus(list);
            }
            if (decrMem > 0) {
                it.setRemainMem(it.getRemainMem() + decrMem);
                BizResource v = it.getBizIds().get(bizId);
                v.setMem(v.getMem() - decrMem);
            }
            dao.update(it);
        });
    }

    private void updateQuotaIncr(int incrCpu, long incrMem, List<Quota> quotas) {
        quotas.forEach(it -> {
            if (incrCpu > 0) {
                it.setCpu(it.getCpu() + incrCpu);
            }
            if (incrMem > 0) {
                it.setMem(it.getMem() + incrMem);
            }
            dao.update(it);
        });
    }

    private void updateQuotaDecr(int decrCpu, long decrMem, List<Quota> quotas) {
        quotas.forEach(it -> {
            if (decrCpu > 0) {
                it.setCpu(it.getCpu() - decrCpu);
            }
            if (decrMem > 0) {
                it.setMem(it.getMem() - decrMem);
            }
            dao.update(it);
        });
    }

}
