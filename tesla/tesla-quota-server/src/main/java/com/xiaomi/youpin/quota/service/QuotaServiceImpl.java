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
import com.xiaomi.youpin.quota.bo.*;
import com.xiaomi.youpin.quota.dao.QuotaRequestDao;
import com.xiaomi.youpin.quota.dao.RecordDao;
import com.xiaomi.youpin.quota.dao.ResourceDao;
import com.xiaomi.youpin.quota.exception.CommonError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.quota.bo.QuotaConst.RESOURCE_LEVEL_DEFAULT;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service(interfaceClass = QuotaService.class, group = "${dubbo.group}")
public class QuotaServiceImpl implements QuotaService {

    @Autowired
    private Dao dao;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private QuotaRequestDao quotaRequestDao;

    @Autowired
    private RecordDao recordDao;

    private final static String BIZ_ID = "biz_id";
    private final static String IP = "ip";


    private ReentrantLock lock = new ReentrantLock();

    /**
     * 获取配额
     *
     * @param quotaInfo
     * @return
     */
    @Transactional("masterTransactionManager")
    @Override
    public Result<List<ResourceBo>> createQuota(QuotaInfo quotaInfo) {
        Record record = new Record();
        record.setType("createQuota");
        record.setOperator(quotaInfo.getOperator());
        record.setBizId(quotaInfo.getBizId());

        lock.lock();
        try {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    record.setStatus(status == 0 ? 0 : 1);
                    recordDao.insert(record);
                }
            });

            record.setProjectBefore(getQuota(quotaInfo.getBizId(), false));
            final long now = System.currentTimeMillis();
            //一个配额请求只能创建一次
            QuotaRequest req = dao.fetch(QuotaRequest.class, Cnd.where(BIZ_ID, "=", quotaInfo.getBizId()));
            if (null != req) {
                return new Result<>(Lists.newArrayList());
            }

            int cpu = quotaInfo.getCpu();
            long mem = quotaInfo.getMem();
            Set<Integer> ports = quotaInfo.getPorts();

            QuotaRequest quotaRequest = new QuotaRequest();
            quotaRequest.setBizId(quotaInfo.getBizId());
            quotaRequest.setCpu(cpu);
            quotaRequest.setMem(mem);
            quotaRequest.setOwner(this.getOwner(quotaInfo));
            quotaRequest.setPorts(new ArrayList<>(ports));
            List<Integer> quotaIds = Lists.newArrayList();

            List<Resource> list = resourceDao.list(cpu, mem, ports, quotaInfo.getProjectId(), quotaInfo.getBizId(), this.getOwner(quotaInfo), this.getLevel(quotaInfo), this.getKeyCenter(quotaInfo), quotaInfo.getNum(), quotaInfo.getLabels(), new ResourceListReq());
            quotaRequest.setNum(list.size());
            list.stream().forEach(it -> {
                it.setRemainCpu(it.getRemainCpu() - cpu);
                it.setRemainMem(it.getRemainMem() - mem);
                it.getPorts().addAll(ports);
                if (quotaInfo.getProjectId() > 0) {
                    it.getProjectIds().add(quotaInfo.getProjectId());
                }
                dao.update(it);
                //修改bizIds信息
                resourceDao.addBizId(it.getId(), quotaInfo.getBizId(), cpu, mem, ports);

                //生成配额记录
                Quota quota = new Quota();
                quota.setResourceId(it.getId());
                quota.setCpu(cpu);
                quota.setBizId(quotaInfo.getBizId());
                quota.setCtime(now);
                quota.setUtime(now);
                quota.setIp(it.getIp());
                quota.setMem(mem);
                quota.setPorts(new ArrayList<>(ports));
                dao.insert(quota);
                quotaIds.add(quota.getId());
            });
            quotaRequest.setQuotas(quotaIds);
            dao.insert(quotaRequest);
            record.setProjectAfter(getQuota(quotaInfo.getBizId(), false));

            return new Result(list.stream().map(it -> {
                ResourceBo bo = new ResourceBo();
                bo.setIp(it.getIp());
                BizResource br = dao.fetch(Resource.class, it.getId()).getBizIds().get(quotaInfo.getBizId());
                bo.setCpuCore(br.getCpus());
                bo.setMem(mem);
                bo.setPorts(ports);
                bo.setCpu(cpu);
                return bo;
            }).collect(Collectors.toList()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<List<UpgradeBo>> getUpgradeInfo(QuotaInfo quotaInfo) {
        List<Resource> resourceList = resourceDao.getResourceByEnvId((int) quotaInfo.getBizId());
        return new Result<>(resourceList.stream().map(it -> {
            boolean upgrade = it.getRemainCpu() >= quotaInfo.getCpu() && it.getRemainMem() >= quotaInfo.getMem();
            UpgradeBo bo = new UpgradeBo();
            bo.setCanUpgrade(upgrade);
            bo.setIp(it.getIp());
            bo.setCpu(quotaInfo.getCpu() - it.getRemainCpu());
            bo.setMem(quotaInfo.getMem() - it.getRemainMem());
            return bo;
        }).collect(Collectors.toList()));
    }

    @Override
    public Result<ExpansionBo> getExpansionInfo(QuotaInfo quotaInfo) {
        int cpu = quotaInfo.getCpu();
        long mem = quotaInfo.getMem();
        ResourceListReq req = new ResourceListReq();
        if (StringUtils.isNotEmpty(quotaInfo.getTargetIp())) {
            //可以指定ip
            req.setTargetIp(quotaInfo.getTargetIp());
        }
        Set<Integer> ports = quotaInfo.getPorts();
        List<Resource> list = resourceDao.list(cpu, mem, ports, quotaInfo.getProjectId(), quotaInfo.getBizId(), this.getOwner(quotaInfo), this.getLevel(quotaInfo), this.getKeyCenter(quotaInfo), quotaInfo.getNum(), quotaInfo.getLabels(), req);
        ExpansionBo bo = new ExpansionBo();
        bo.setNum(list.size());
        return new Result<>(bo);
    }


    /**
     * 增加配额(注意是水平扩展)
     *
     * @param quotaInfo
     * @param request
     */
    private List<ResourceBo> addQuota(QuotaInfo quotaInfo, QuotaRequest request) {
        int cpu = quotaInfo.getCpu();
        long mem = quotaInfo.getMem();
        Set<Integer> ports = quotaInfo.getPorts();
        //筛选机器是水平的(也就是说,一台机器只能部署相同的应用1个)
        ResourceListReq req = new ResourceListReq();
        if (StringUtils.isNotEmpty(quotaInfo.getTargetIp())) {
            //可以指定ip
            req.setTargetIp(quotaInfo.getTargetIp());
        }
        List<Resource> list = resourceDao.list(cpu, mem, ports, quotaInfo.getProjectId(), quotaInfo.getBizId(), this.getOwner(quotaInfo), this.getLevel(quotaInfo), this.getKeyCenter(quotaInfo), quotaInfo.getNum(), quotaInfo.getLabels(), req);

        List<Integer> quotas = request.getQuotas();
        List<ResourceBo> res = list.stream().map(it -> {
            it.setRemainCpu(it.getRemainCpu() - cpu);
            it.setRemainMem(it.getRemainMem() - mem);
            it.getPorts().addAll(ports);
            if (quotaInfo.getProjectId() > 0) {
                it.getProjectIds().add(quotaInfo.getProjectId());
            }
            dao.update(it);
            //生成配额记录
            Quota quota = new Quota();
            quota.setResourceId(it.getId());
            quota.setBizId(quotaInfo.getBizId());
            quota.setCpu(cpu);
            quota.setIp(it.getIp());
            quota.setMem(mem);
            quota.setPorts(new ArrayList<>(ports));
            dao.insert(quota);

            quotas.add(quota.getId());

            resourceDao.addBizId(it.getId(), quotaInfo.getBizId(), request.getCpu(), mem, ports);
            ResourceBo bo = new ResourceBo();
            bo.setIp(it.getIp());

            //获取最新的
            BizResource br = dao.fetch(Resource.class, it.getId()).getBizIds().get(quotaInfo.getBizId());
            bo.setCpuCore(br.getCpus());

            bo.setHostName(it.getHostName());
            bo.setName(it.getName());

            return bo;
        }).collect(Collectors.toList());

        request.setNum(request.getNum() + list.size());
        dao.update(request);
        return res;
    }


    private List<ResourceBo> getQuota(long bizId, boolean throwException) {
        try {
            List<ResourceBo> list = dao.query(Quota.class, Cnd.where("biz_id", "=", bizId)).stream().map(it -> {
                ResourceBo bo = new ResourceBo();
                bo.setIp(it.getIp());
                bo.setId(it.getResourceId());
                bo.setBizId(bizId);
                return bo;
            }).collect(Collectors.toList());

            return list.stream().map(it -> {
                //获取最新的
                Resource resource = dao.fetch(Resource.class, it.getId());
                if (null == resource) {
                    log.warn("resource is null:{}", it.getId());
                    return null;
                }
                BizResource br = resource.getBizIds().get(bizId);
                if (null == br) {
                    log.warn("br is null:{}", bizId);
                    return null;
                }
                it.setCpuCore(br.getCpus());
                return it;
            }).filter(it -> null != it).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            if (throwException) {
                throw e;
            }
        }
        return new LinkedList<>();
    }


    /**
     * 移除一部分配额
     */
    private List<ResourceBo> removeQuota(QuotaInfo quotaInfo, QuotaRequest request) {
        List<ResourceBo> ips = Lists.newArrayList();
        //删除新加的配额(新加的必然id比较大)
        List<Integer> ids = request.getQuotas().stream().sorted(Comparator.reverseOrder()).limit(quotaInfo.getNum()).map(id -> {
            Quota quota = dao.fetch(Quota.class, id);
            if (null == quota) {
                return null;
            }
            int rid = quota.getResourceId();
            Resource resource = dao.fetch(Resource.class, rid);
            ResourceBo bo = new ResourceBo();
            bo.setIp(resource.getIp());
            ips.add(bo);
            dao.delete(Quota.class, id);
            resourceDao.removeBizId(rid, quotaInfo.getBizId(), quota.getCpu(), quota.getMem(), quotaInfo.getPorts(), quotaInfo.getProjectId());
            return id;
        }).collect(Collectors.toList());
        request.getQuotas().removeAll(ids);
        request.setNum(request.getNum() - quotaInfo.getNum());
        dao.update(request);
        return ips;
    }


    /**
     * 销毁配额 (实例数填0)
     *
     * @param quotaInfo
     * @return
     */
    @Transactional("masterTransactionManager")
    @Override
    public Result<List<ResourceBo>> destoryQuota(QuotaInfo quotaInfo) {
        Record record = new Record();
        record.setType("destroyQuota");
        record.setOperator(quotaInfo.getOperator());
        record.setBizId(quotaInfo.getBizId());


        lock.lock();
        try {

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    record.setStatus(status == 0 ? 0 : 1);
                    recordDao.insert(record);
                }
            });

            record.setProjectBefore(getQuota(quotaInfo.getBizId(), false));
            QuotaRequest data = dao.fetch(QuotaRequest.class, Cnd.where(BIZ_ID, "=", quotaInfo.getBizId()));
            quotaInfo.setNum(data.getQuotas().size());
            quotaInfo.setPorts(new HashSet<>(data.getPorts()));

            List<Quota> list = dao.query(Quota.class, Cnd.where("id", "in", data.getQuotas()));
            List<ResourceBo> res = list.stream().map(it -> {
                Resource resource = dao.fetch(Resource.class, it.getResourceId());
                ResourceBo bo = new ResourceBo();
                bo.setIp(resource.getIp());
                return bo;
            }).collect(Collectors.toList());
            removeQuota(quotaInfo, data);
            dao.delete(data);
            record.setProjectAfter(getQuota(quotaInfo.getBizId(), false));
            return new Result(res);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 把一台机器上的服务迁移至另外一台
     * <p>
     * 可以直接指定ip(admin 用户可执行 QuotaInfo -> targetIp)
     *
     * @param quotaInfo 　需要参数：机器的ip和服务的bizId
     * @return 如果成功，返回新机器的ip
     */
    @Transactional("masterTransactionManager")
    @Override
    public Result<ResourceBo> drift(QuotaInfo quotaInfo) {

        Record record = new Record();
        record.setType("drift");
        record.setOperator(quotaInfo.getOperator());

        lock.lock();
        try {

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    record.setStatus(status == 0 ? 0 : 1);
                    recordDao.insert(record);
                }
            });


            String ip = quotaInfo.getIp();
            long bizId = quotaInfo.getBizId();
            record.setIp(ip);
            record.setBizId(bizId);
            record.setProjectBefore(getQuota(bizId, false));

            Quota quota = dao.fetch(Quota.class, Cnd.where("ip", "=", ip).and("biz_id", "=", bizId));
            if (quota == null) {
                return Result.fail(CommonError.UnknownQuota);
            }

            int cpu = quota.getCpu();
            long mem = quota.getMem();
            Set<Integer> ports = new HashSet<>(quota.getPorts());
            int num = 1;
            String excludedIp = ip;

            quotaInfo.setCpu(cpu);
            quotaInfo.setMem(mem);
            quotaInfo.setPorts(ports);
            quotaInfo.setNum(num);

            QuotaRequest quotaRequest = dao.fetch(QuotaRequest.class, Cnd.where("biz_id", "=", bizId));
            List<ResourceBo> newQuota = addQuota(quotaInfo, quotaRequest);
            if (newQuota == null || newQuota.size() != 1 || newQuota.get(0).equals(excludedIp)) {
                record.setProjectAfter(getQuota(bizId, false));
                return Result.fail(CommonError.FailedToAddNewQuota);
            }

            removeQuota(quota, bizId, quotaInfo.getProjectId());
            record.setProjectAfter(getQuota(bizId, false));

            return new Result(newQuota.get(0));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 服务器下线,这台机器的资源会被释放
     *
     * @param quotaInfo 　需要参数：机器的ip
     * @return　返回这台机器下线之前在运行的服务bizId
     */
    @Transactional("masterTransactionManager")
    @Override
    public Result<List<ResourceBo>> offline(QuotaInfo quotaInfo) {
        Record record = new Record();
        record.setType("offline");
        record.setOperator(quotaInfo.getOperator());

        lock.lock();

        try {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    record.setStatus(status == 0 ? 0 : 1);
                    recordDao.insert(record);
                }
            });


            String ip = quotaInfo.getIp();
            record.setIp(ip);
            Resource resource = dao.fetch(Resource.class, Cnd.where(IP, "=", ip));
            if (resource == null) {
                return Result.fail(CommonError.UnknownResource);
            }
            Map<Long, BizResource> bizIds = resource.getBizIds();
            if (bizIds != null) {
                record.setResourceBefore(new HashMap<>(bizIds));
            }

            int cpu = 0;
            long mem = 0;
            for (BizResource bizResource : bizIds.values()) {
                long bizId = bizResource.getBizId();
                Quota data = dao.fetch(Quota.class, Cnd.where("ip", "=", ip).and("biz_id", "=", bizId));
                cpu += data.getCpu();
                mem += data.getMem();
                quotaRequestDao.removeQuota(bizId, data.getId());
                dao.delete(data);
            }
            List<ResourceBo> res = bizIds.keySet().stream().map(key -> {
                ResourceBo bo = new ResourceBo();
                bo.setBizId(key);
                return bo;
            }).collect(Collectors.toList());

            resource.setRemainCpu(resource.getRemainCpu() + cpu);
            resource.setRemainMem(resource.getRemainMem() + mem);
            resource.getPorts().clear();
            resource.getBizIds().clear();
            resource.getProjectIds().clear();

            //status等于1,resource不能使用
            takeResourceOffline(resource);
            dao.update(resource);
            return new Result(res);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 下线的机器的status是1
     *
     * @param resource
     */
    private void takeResourceOffline(Resource resource) {
        resource.setStatus(1);
    }

    /**
     * 修改配额
     *
     * @param quotaInfo
     * @return
     */
    @Transactional("masterTransactionManager")
    @Override
    public ModifyQuotaRes modifyQuota(QuotaInfo quotaInfo) {
        log.info("modify quota:{}", new Gson().toJson(quotaInfo));

        long begin = System.currentTimeMillis();

        Record record = new Record();
        record.setOperator(quotaInfo.getOperator());


        //请求中带过来的cpu
        int reqCpu = quotaInfo.getCpu();
        if (reqCpu < 1) {
            reqCpu = 1;
        }
        //请求中带过来的mem，最少1g，1024*1024*1024b
        long reqMem = quotaInfo.getMem();
        if (reqMem < 1073741824L) {
            reqMem = 1073741824L;
        }

        lock.lock();
        try {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    record.setStatus(status == 0 ? 0 : 1);
                    recordDao.insert(record);
                }
            });


            record.setBizId(quotaInfo.getBizId());
            QuotaRequest request = dao.fetch(QuotaRequest.class, Cnd.where(BIZ_ID, "=", quotaInfo.getBizId()));

            //没有配额会自动创建
            if (request == null) {
                record.setType("create");
                Result<List<ResourceBo>> list = createQuota(quotaInfo);
                ModifyQuotaRes res = new ModifyQuotaRes();
                res.setType("add");
                res.setIps(list.getData());
                res.setCurrIps(list.getData());

                record.setProjectAfter(list.getData());
                return res;
            }
            record.setProjectBefore(getQuota(quotaInfo.getBizId(), false));

            int num = quotaInfo.getNum();
            int oldNum = request.getNum();
            if (request.getNum() > 0) {
                //用之前设置好的
                quotaInfo.setCpu(request.getCpu());
                quotaInfo.setMem(request.getMem());
                quotaInfo.setPorts(request.getPorts().stream().collect(Collectors.toSet()));
            } else {
                //没有实例了,可以用最新指定的
                request.setCpu(quotaInfo.getCpu());
                request.setMem(quotaInfo.getMem());
                request.setPorts(quotaInfo.getPorts().stream().collect(Collectors.toList()));
                dao.update(request);
            }

            ModifyQuotaRes res = new ModifyQuotaRes();
            //需要增加实例数量
            if (num > oldNum) {
                record.setType("add");
                quotaInfo.setNum(num - oldNum);
                List<ResourceBo> ips = addQuota(quotaInfo, request);
                res.setIps(ips);
                res.setType("add");
            } else if (num < oldNum) {
                record.setType("remove");
                //需要减少实例数量
                quotaInfo.setNum(oldNum - num);
                List<ResourceBo> ips = removeQuota(quotaInfo, request);
                res.setIps(ips);
                res.setType("remove");
            } else {
                //没增加也没减少
                res.setType("get");
                res.setIps(Lists.newArrayList());
                res.setIps(getQuota(quotaInfo.getBizId(), true));
                if (reqCpu > request.getCpu() || reqMem > request.getMem()) {
                    record.setType("upgrade");
                    res.setSubType(ModifyQuotaRes.SubType.upgrade.ordinal());
                    //尝试升级配置
                    int incrCpu = reqCpu - request.getCpu() > 0 ? reqCpu - request.getCpu() : 0;
                    long incrMem = reqMem - request.getMem() > 0 ? reqMem - request.getMem() : 0L;
                    boolean success = resourceDao.upgrade(quotaInfo.getBizId(), incrCpu, incrMem);
                    res.setSuccess(success);
                    if (!success) {
                        log.error("upgrade error, modify quota:{}", new Gson().toJson(quotaInfo));
                    }
                }
                if (reqCpu < request.getCpu() || reqMem < request.getMem()) {
                    record.setType("downgrade");
                    res.setSubType(ModifyQuotaRes.SubType.downgrade.ordinal());
                    //尝试降级配置
                    int decrCpu = request.getCpu() - reqCpu > 0 ? request.getCpu() - reqCpu : 0;
                    long decrMem = request.getMem() - reqMem > 0 ? request.getMem() - reqMem : 0L;
                    boolean success = resourceDao.downgrade(quotaInfo.getBizId(), decrCpu, decrMem);
                    res.setSuccess(success);
                }
            }
            //当前的全部信息
            res.setCurrIps(getQuota(quotaInfo.getBizId(), true));
            record.setProjectAfter(res.getCurrIps());
            log.info("{} res:{} useTime:{}", quotaInfo.getBizId(), new Gson().toJson(res), System.currentTimeMillis() - begin);
            return res;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param ip
     * @param bizId
     * @return
     */
    @Transactional("masterTransactionManager")
    @Override
    public Result<Boolean> removeQuota(String ip, long bizId, long projectId) {
        Record record = new Record();
        record.setBizId(bizId);
        record.setType("removeQuota");

        lock.lock();
        try {

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    record.setStatus(status == 0 ? 0 : 1);
                    recordDao.insert(record);
                }
            });

            record.setProjectBefore(getQuota(bizId, false));
            Quota data = dao.fetch(Quota.class, Cnd.where("ip", "=", ip).and("biz_id", "=", bizId));

            removeQuota(data, bizId, projectId);
            record.setProjectAfter(getQuota(bizId, false));

        } finally {
            lock.unlock();
        }
        return new Result<>(true);
    }

    private void removeQuota(Quota data, long bizId, long projectId) {
        if (null != data) {
            int cpu = data.getCpu();
            resourceDao.removeBizId(data.getResourceId(), bizId, cpu, data.getMem(), new HashSet<>(data.getPorts()), projectId);
            quotaRequestDao.removeQuota(bizId, data.getId());
            dao.delete(data);
        }
    }


    @Transactional("masterTransactionManager")
    @Override
    public Result<Boolean> updateQuota(String ip, long bizId, long time) {
        dao.update(Quota.class, Chain.make("utime", time), Cnd.where("ip", "=", ip).and(BIZ_ID, "=", bizId));
        return new Result<>(true);
    }

    @Override
    public Result<List<QuotaInfo>> quotaList(long bizId) {
        List<QuotaInfo> list = dao.query(Quota.class, Cnd.where("biz_id", "=", bizId)).stream().map(it -> {
            QuotaInfo info = new QuotaInfo();
            info.setIp(it.getIp());
            return info;
        }).collect(Collectors.toList());
        return new Result<>(list);
    }

    @Override
    public Result<Boolean> revise() {
        log.info("revise");
//        reviseProjectId();
        return new Result<>(true);
    }

    private void reviseProjectId() {
        Map<String, Long> map = Maps.newHashMap();
        List<org.nutz.dao.entity.Record> list = dao.query("project_env", null);
        list.stream().forEach(it -> map.put(it.getString("id"), it.getLong("project_id")));
        log.info("map:{}", map);
        List<org.nutz.dao.entity.Record> rlist = dao.query("resource", null);
        rlist.stream().forEach(it -> {
            Map br = new Gson().fromJson(it.getString("biz_ids"), Map.class);
            Set<String> s = (br.keySet());
            Set<Long> pids = s.stream().map(it2 -> map.get(it2)).collect(Collectors.toSet());
            log.info(it.get("id") + "+" + pids);
            dao.update(Resource.class, Chain.make("project_ids", new Gson().toJson(pids)), Cnd.where("id", "=", it.get("id")));
        });
    }

    private String getOwner(QuotaInfo quotaInfo) {
        if (quotaInfo.getLabels() == null || StringUtils.isEmpty(quotaInfo.getLabels().get("owner"))) {
            return "";
        }
        return quotaInfo.getLabels().get("owner");
    }

    private Integer getLevel(QuotaInfo quotaInfo) {
        if (quotaInfo.getLabels() == null || StringUtils.isEmpty(quotaInfo.getLabels().get("level"))) {
            return RESOURCE_LEVEL_DEFAULT;
        }
        return Integer.valueOf(quotaInfo.getLabels().get("level"));
    }

    private boolean getKeyCenter(QuotaInfo quotaInfo) {
        if (quotaInfo.getLabels() == null || StringUtils.isEmpty(quotaInfo.getLabels().get("keycenter"))) {
            return false;
        }
        return quotaInfo.getLabels().get("keycenter").equals("true");
    }
}
