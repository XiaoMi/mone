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

package com.xiaomi.youpin.gwdash.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.AppDeployStatus;
import com.xiaomi.youpin.gwdash.common.DeployTypeEnum;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.hermes.bo.response.Account;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.trans.Trans;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class MachineManagementServiceImp implements MachineManagementService {

    @Autowired
    private Dao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private PipelineService pipelineService;

    public Result<Map<String, Object>> getList(int page, int pageSize, String queryKey, String queryValue, String labelKey, String labelValue) {
        Cnd cnd = Cnd.where("1", "=", "1");
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(queryKey)
                && StringUtils.isNotEmpty(queryValue)) {
            cnd.and(queryKey, "like", "%" + queryValue + "%");
        }
        if (!labelKey.equals("") && !labelValue.equals("")) {
            cnd.and("labels->'$." + labelKey + "'", "=", labelValue);
        }
        map.put("total", dao.count("machine_list", cnd));
        cnd.desc("id");
        map.put("list", dao.query(Machine.class, cnd, new Pager(page, pageSize)));
        return Result.success(map);
    }

    public Result<Boolean> add(MachineBo machineBo) {
        final String ip = machineBo.getIp();
        Machine machine0 = dao.fetch(Machine.class, Cnd.where("ip", "=", ip));
        if (null != machine0) {
            return new Result<>(1, "ip已经存在", false);
        }
        final long now = System.currentTimeMillis();
        // 机器相关
        Machine machine = new Machine();
        machine.setName(machineBo.getName());
        machine.setHostname(machineBo.getHostname());
        machine.setIp(ip);
        machine.setGroup(machineBo.getGroup());
        machine.setDesc(machineBo.getDesc());
        machine.setLabels(machineBo.getLabels());
        machine.setCtime(now);
        machine.setUtime(now);
        dao.insert(machine);
        return Result.success(true);
    }

    public Result<Boolean> insertOrUpdate(MachineBo machineBo) {
        Machine machine = dao.fetch(Machine.class, Cnd.where("ip", "=", machineBo.getIp()));
        if (machine == null) {
            addMachine(machineBo);
            return Result.success(true);
        }
        if (StringUtils.isNotBlank(machineBo.getHostname())) {
            machine.setHostname(machineBo.getHostname());
        }
        if (StringUtils.isNotBlank(machineBo.getName())) {
            machine.setName(machineBo.getName());
        }
        if (StringUtils.isNotBlank(machineBo.getGroup())) {
            machine.setGroup(machineBo.getGroup());
        }
        if (!MapUtils.isEmpty(machineBo.getLabels())) {
            MachineLabels oldLabels = machine.getLabels() == null ? new MachineLabels() : machine.getLabels();
            oldLabels.putAll(machineBo.getLabels());
            machine.setLabels(oldLabels);
        }
        machine.setUtime(System.currentTimeMillis());
        dao.update(machine);
        return Result.success(true);
    }

    @Override
    public void addMachine(MachineBo machineBo) {
        final String ip = machineBo.getIp();
        final long now = System.currentTimeMillis();
        Machine machine = new Machine();
        machine.setName(machineBo.getName());
        machine.setHostname(machineBo.getHostname());
        machine.setIp(ip);
        machine.setGroup(machineBo.getGroup());
        machine.setDesc(machineBo.getDesc());
        machine.setLabels(machineBo.getLabels());
        machine.setCtime(now);
        machine.setUtime(now);
        dao.insert(machine);
    }

    @Override
    public void updateMachine(MachineBo machineBo) {
        final String ip = machineBo.getIp();
        Machine machine = dao.fetch(Machine.class, Cnd.where("ip", "=", ip));
        if (null != machine) {
            machine.setHostname(machineBo.getHostname());
            dao.update(machine);
        }
    }

    public Result<Boolean> edit(final MachineBo machineBo) {
        Machine machine = dao.fetch(Machine.class, machineBo.getId());
        if (null == machine) {
            return new Result<>(1, "编辑机器不存在", false);
        }
        final String ip = machine.getIp();
        final long now = System.currentTimeMillis();
        machine.setName(machineBo.getName());
        machine.setHostname(machineBo.getHostname());
        machine.setGroup(machineBo.getGroup());
        machine.setDesc(machine.getDesc());
        machine.setLabels(machineBo.getLabels());
        machine.setUtime(System.currentTimeMillis());
        dao.update(machine);
        return Result.success(true);
    }

    public Result<Boolean> delete(List<Long> ids) {
        List<Machine> machines = dao.query(Machine.class, Cnd.where("id", "in", ids));
        if (machines.size() == 0) {
            return new Result<>(1, "机器不存在", false);
        }
        Trans.exec(() -> {
            dao.clear(ProjectEnvMachine.class, Cnd.where("machine_id", "in", ids));
            dao.clear(Machine.class, Cnd.where("id", "in", ids));
        });
        return Result.success(true);
    }

    /**
     * 更新(多条)
     *
     * @param id
     * @param labels
     * @return
     */
    public Result<Boolean> updateLabel(long id, MachineLabels labels) {
        Sql sql = Sqls.create("update machine_list set labels=@labels where id = @id");
        sql.params().set("labels", new Gson().toJson(labels)).set("id", id);
        dao.execute(sql);
        return Result.success(true);
    }


    /**
     * 单条插入label
     *
     * @param id
     * @param key
     * @param value
     * @return
     */
    public Result<Boolean> insertLabel(long id, String key, String value) {
        Sql sql = Sqls.create("update machine_list set labels=json_insert(labels,$key,@value) where id = @id");
        sql.vars().set("key", String.format("'$.%s'", key));
        sql.params().set("value", value).set("id", id);
        dao.execute(sql);
        return Result.success(true);
    }


    /**
     * 删除label
     *
     * @param id
     * @param key
     * @return
     */
    public Result<Boolean> removeLabel(long id, String key) {
        Sql sql = Sqls.create("update machine_list set labels=json_remove(labels,$key) where id = @id");
        sql.vars().set("key", String.format("'$.%s'", key));
        sql.params().set("id", id);
        dao.execute(sql);
        return Result.success(true);
    }


    /**
     * 根据label查询机器
     * <p>
     * example key=docker value=true
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public List<MachineBo> queryMachineListByLabel(String key, String value) {
        Sql sql = Sqls.create("select * from machine_list where labels->$key = @value");
        sql.vars().set("key", String.format("'$.%s'", key));
        sql.params().set("value", value);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Machine.class));
        List<Machine> list = dao.execute(sql).getList(Machine.class);
        return list.stream().map(it -> {
            MachineBo machineBo = new MachineBo();
            machineBo.setName(it.getName());
            machineBo.setIp(it.getIp());
            machineBo.setGroup(it.getGroup());
            machineBo.setHostname(it.getHostname());
            machineBo.setLabels(it.getLabels());
            machineBo.setDesc(it.getDesc());
            machineBo.setId(it.getId());
            return machineBo;
        }).collect(Collectors.toList());
    }


    @Override
    public MachineLabels queryMachineLabels(String ip) {
        Machine machine = dao.fetch(Machine.class, Cnd.where("ip", "=", ip));
        if (null != machine) {
            return machine.getLabels();
        }
        return null;
    }

    @Override
    public boolean updatePrepareLabels(long id, MachineLabels prepareLabels) {
        int i = 0;
        while (true) {
            Machine machine = dao.fetch(Machine.class, id);
            if (machine.getPrepareLabels() == null) {
                machine.setPrepareLabels(new MachineLabels());
            }
            MachineLabels old = machine.getPrepareLabels();
            prepareLabels.entrySet().stream().forEach(it -> {
                if (old.get(it.getKey()) != null) {
                    old.put(it.getKey(), String.valueOf(Long.valueOf(old.get(it.getKey())) + Long.valueOf(it.getValue())));
                }
                old.put(it.getKey(), it.getValue());
            });

            machine.setPrepareLabels(old);

            boolean res = dao.updateWithVersion(machine) > 0;
            if (res) {
                return true;
            }

            if (i++ > 20) {
                throw new RuntimeException("updatePrepareLabels error");
            }
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void removePrepareLabels(long machineId, long projectId) {
        int i = 0;
        while (true) {
            Machine machine = dao.fetch(Machine.class, machineId);

            MachineLabels old = machine.getPrepareLabels();

            Set<Map.Entry<String, String>> set = old.entrySet().stream().filter(it -> !it.getKey().contains(String.valueOf(projectId))).collect(Collectors.toSet());

            MachineLabels newMl = new MachineLabels();
            set.stream().forEach(it -> {
                newMl.put(it.getKey(), it.getValue());
            });

            machine.setPrepareLabels(newMl);

            boolean res = dao.updateWithVersion(machine) > 0;
            if (res) {
                return;
            }

            if (i++ > 20) {
                throw new RuntimeException("removePrepareLabels error");
            }
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public List<MachineBo> queryMachineListByLikeLabel(String group, String key, String value) {
        Sql sql = Sqls.create("select * from machine_list where my_group=@group and labels->'$.appName' like @value");
        sql.params().set("group", group);
        sql.params().set("value", "%" + value + "%");
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Machine.class));
        List<Machine> list = dao.execute(sql).getList(Machine.class);
        return list.stream().map(it -> {
            MachineBo machineBo = new MachineBo();
            machineBo.setId(it.getId());
            machineBo.setName(it.getName());
            machineBo.setIp(it.getIp());
            machineBo.setGroup(it.getGroup());
            machineBo.setHostname(it.getHostname());
            machineBo.setLabels(it.getLabels());
            machineBo.setDesc(it.getDesc());
            return machineBo;
        }).collect(Collectors.toList());
    }

    @Override
    public MachineBo queryMachineListByIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return null;
        }
        List<Machine> list = dao.query(Machine.class, Cnd.where("ip", "=", ip));
        if (list.size() == 0) {
            return null;
        }
        MachineBo machineBo = new MachineBo();
        BeanUtils.copyProperties(list.get(0), machineBo);
        return machineBo;
        //return list.get(0);
    }

    /**
     * 根据用户名依据项目关联的机器
     *
     * @param username
     * @return
     */
    @Override
    public List<MachineBo> queryMachinesByUsername(String username) {

        Account account = userService.queryUserByName(username);
        if (null == account) {
            return null;
        }

        //get all projects
        List<Project> projects = projectService.getAllProjects(account.getId());

        //get all project env
        List<ProjectEnv> list = new ArrayList<>();
        projects.stream().forEach(it -> {
            list.addAll(projectEnvService.getList(it.getId()).getData());
        });

        //get all machines
        List<Machine> machines = new ArrayList<>();
        for (ProjectEnv env : list) {
            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(env.getPipelineId()).getData();
            if (null != projectPipeline && projectPipeline.getDeployInfo() != null) {
                DeployInfo deployInfo = projectPipeline.getDeployInfo();
                if (DeployTypeEnum.isDocker(env.getDeployType())) {
                    //docker env check
                    if (CollectionUtils.isNotEmpty(deployInfo.getDockerMachineList())) {
                        machines.addAll(deployInfo.getDockerMachineList());
                    }
                } else {
                    //physicalMachine env check
                    if (CollectionUtils.isNotEmpty(deployInfo.getDeployBatches())) {
                        for (DeployBatch e : deployInfo.getDeployBatches()) {
                            if (CollectionUtils.isNotEmpty(e.getDeployMachineList())) {
                                machines.addAll(e.getDeployMachineList().stream().filter(it ->
                                        it.getAppDeployStatus() != AppDeployStatus.NUKE.getId()).collect(Collectors.toList()));
                            }
                        }
                    }
                }
            }
        }

//        /**
//         * 内部系统 直接join 4张表 超硬核
//         * 表关联
//         * Project.id = project_env_machine.app_id
//         * Project.id = project_role.projectId
//         * machine_list.id=project_env_machine.machine_id;
//         */
//        Long startTime = System.currentTimeMillis();
//        Sql sql = Sqls.create("SELECT  DISTINCT(M.id),M.name,M.ip,M.hostname,M.my_desc from project_role Role,project P,project_env_machine EnvM,machine_list M " +
//                "WHERE Role.accountId =@accountId and P.id = EnvM.app_id and P.id = Role.projectId and M.id=EnvM.machine_id;");
//        sql.params().set("accountId", account.getId());
//        sql.setCallback(Sqls.callback.entities());
//        sql.setEntity(dao.getEntity(MachineBo.class));
//        List<MachineBo> list = dao.execute(sql).getList(MachineBo.class);
//        log.info("queryMachinesByUsername sql use time:{}", System.currentTimeMillis() - startTime);
        if (CollectionUtils.isEmpty(machines)) {
            return new ArrayList<>();
        }

        return machines.stream().map(it -> {
            MachineBo machineBo = new MachineBo();
            BeanUtils.copyProperties(it, machineBo);
            return machineBo;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateLastUpdateTime(String ip, long time) {
        dao.update(Machine.class, Chain.make("utime", time), Cnd.where("ip", "=", ip));
    }

    @Override
    public int countWithIp(String ip) {
        return dao.count(Machine.class, Cnd.where("ip", "=", ip));
    }


    @Override
    public List<MachineBo> queryMachineListByLabel(String group, String key, String value) {
        Sql sql = Sqls.create("select * from machine_list where my_group = @group and labels->$key = @value");
        sql.vars().set("key", String.format("'$.%s'", key));
        sql.params().set("group", group);
        sql.params().set("value", value);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Machine.class));
        List<Machine> list = dao.execute(sql).getList(Machine.class);
        return list.stream().map(it -> {
            MachineBo machineBo = new MachineBo();
            machineBo.setId(it.getId());
            machineBo.setName(it.getName());
            machineBo.setIp(it.getIp());
            machineBo.setGroup(it.getGroup());
            machineBo.setHostname(it.getHostname());
            machineBo.setLabels(it.getLabels());
            machineBo.setDesc(it.getDesc());
            return machineBo;
        }).collect(Collectors.toList());
    }


    /**
     * 根据ip 向机器 更新或者插入 label
     * example cpu mem 信息
     *
     * @param ip
     * @param key
     * @param value
     * @return
     */
    @Override
    public Boolean setMachineLabel(String ip, String key, String value) {
        Sql sql = Sqls.create("update machine_list set labels=json_set(labels,$key,@value) where ip = @ip");
        sql.vars().set("key", String.format("'$.%s'", key));
        sql.params().set("value", value).set("ip", ip);
        dao.execute(sql);
        return true;
    }

    @Override
    public Sql setMachineLableSql(String ip, String key, String value) {
        Sql sql = Sqls.create("update machine_list set labels=json_set(labels,$key,@value) where ip = @ip");
        sql.vars().set("key", String.format("'$.%s'", key));
        sql.params().set("value", value).set("ip", ip);
        return sql;
    }


    @Override
    public Boolean setMachineLabels(Sql... sqls) {
        dao.execute(sqls);
        return true;
    }

    /**
     * 根据ip查询label信息
     *
     * @param ip
     * @return
     */
    @Override
    public MachineLabels getLabelsByIp(String ip) {
        Machine machine = dao.fetch(Machine.class, Cnd.where("ip", "=", ip));
        if (null == machine) {
            log.warn("machine is null ip:{}", ip);
            return null;
        }
        return machine.getLabels();
    }

    public Result<Boolean> bindApplication(ProjectEnvMachineBo projectEnvMachineBo) {
        long[] machineIds = projectEnvMachineBo.getMachineIds();
        if (null == machineIds) {
            return Result.success(true);
        }
        long appId = projectEnvMachineBo.getAppId();
        long envId = projectEnvMachineBo.getEnvId();
        for (long machineId : machineIds) {
            ProjectEnvMachine projectEnvMachine = dao.fetch(ProjectEnvMachine.class,
                    Cnd.where("machine_id", "=", machineId)
                            .and("env_id", "=", envId));
            if (null != projectEnvMachine) {
                projectEnvMachine.setAppId(appId);
                dao.update(projectEnvMachine);
                return Result.success(true);
            }
            long now = System.currentTimeMillis();
            projectEnvMachine = new ProjectEnvMachine();
            projectEnvMachine.setMachineId(machineId);
            projectEnvMachine.setAppId(appId);
            projectEnvMachine.setEnvId(envId);
            projectEnvMachine.setUtime(now);
            projectEnvMachine.setCtime(now);
            dao.insert(projectEnvMachine);
        }
        return Result.success(true);
    }

    public Result<Boolean> unbindApplication(long id) {
        ProjectEnvMachine projectEnvMachine = dao.fetch(ProjectEnvMachine.class, id);
        if (null != projectEnvMachine) {
            dao.delete(projectEnvMachine);
        }
        return Result.success(true);
    }

    public Result<List<ProjectEnvMachineBo>> listApplicationsOfMachine(long machineId) {
        List<ProjectEnvMachine> list = dao.query(ProjectEnvMachine.class,
                Cnd.where("machine_id", "=", machineId));
        if (null == list) {
            return Result.success(new ArrayList<>());
        }
        return Result.success(list.stream().map(it -> {
            /**
             * 兼容老数据 -- 设置appId
             */
            ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, it.getEnvId());
            if (null != projectEnv) {
                it.setAppId(projectEnv.getProjectId());
                dao.update(it);
            }
            dao.fetchLinks(it, null);
            ProjectEnvMachineBo projectEnvMachineBo = new ProjectEnvMachineBo();
            projectEnvMachineBo.setId(it.getId());
            projectEnvMachineBo.setAppId(it.getAppId());
            projectEnvMachineBo.setEnvId(it.getEnvId());
            projectEnvMachineBo.setMachineId(it.getMachineId());
            projectEnvMachineBo.setProject(it.getProject());
            projectEnvMachineBo.setProjectEnv(it.getProjectEnv());
            return projectEnvMachineBo;
        }).collect(Collectors.toList()));
    }

    public Result<List<EnvMachineBo>> listMachinesOfEnv(long envId) {
        List<ProjectEnvMachine> list = dao.queryByJoin(ProjectEnvMachine.class, "machine",
                Cnd.where("env_id", "=", envId));
        if (null == list) {
            return Result.success(new ArrayList<>());
        }
        return Result.success(list.stream()
                .filter(it -> {
                    /**
                     * 兼容代码，过滤掉库中不存在的机器
                     */
                    Machine machine = it.getMachine();
                    if (StringUtils.isEmpty(machine.getIp())) {
                        dao.delete(it);
                        return false;
                    }
                    return true;
                })
                .map(it -> {
                    Machine machine = it.getMachine();
                    EnvMachineBo envMachineBo = new EnvMachineBo();
                    envMachineBo.setId(it.getId());
                    envMachineBo.setUsed(it.isUsed());
                    envMachineBo.setMachineId(machine.getId());
                    envMachineBo.setName(machine.getName());
                    envMachineBo.setIp(machine.getIp());
                    envMachineBo.setHostname(machine.getHostname());
                    return envMachineBo;
                })
                .collect(Collectors.toList()));
    }
}
