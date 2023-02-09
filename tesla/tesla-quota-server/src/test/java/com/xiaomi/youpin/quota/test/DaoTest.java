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

package com.xiaomi.youpin.quota.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.xiaomi.youpin.quota.bo.Resource;
import com.xiaomi.youpin.quota.bo.ResourceListReq;
import com.xiaomi.youpin.quota.dao.ResourceDao;
import com.xiaomi.youpin.quota.dao.TransactionDao;
import com.xiaomi.youpin.quota.service.ConfigService;
import com.xiaomi.youpin.quota.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class DaoTest extends BaseTest {


    @Autowired
    private Dao dao;

    @Autowired
    private TransactionDao tDao;


    @Autowired
    private ResourceDao resourceDao;


    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ConfigService configService;

    private Resource resource0, resource1, resource2;

    private static final double DELTA = 0.0000001;

    private static final String IP_0 = "xxxx";
    private static final String IP_1 = "xxxx";
    private static final String IP_2 = "xxxx";
    private static final String IP_3 = "xxxx";

    private static final String CPU_METRICS = "CPU";
    private static final String CPU_MEM_METRICS = "CPU_MEM";
    private static final String MEM_METRICS = "MEM";

    //    @Before
    public void initResource() {
        dao.create(Resource.class, true);
        resource0 = new Resource();
        resource0.setIp(IP_0);
        resource0.setCpu(100);
        resource0.setRemainCpu(50);
        resource0.setMem(2000);
        resource0.setRemainMem(500);
        resource0.setPorts(new ArrayList<>());
        resource0.setSystemPorts(new ArrayList<>());

        resource1 = new Resource();
        resource1.setIp(IP_1);
        resource1.setCpu(100);
        resource1.setRemainCpu(10);
        resource1.setMem(1000);
        resource1.setRemainMem(551);
        resource1.setPorts(new ArrayList<>());
        resource1.setSystemPorts(new ArrayList<>());

        resource2 = new Resource();
        resource2.setIp(IP_2);
        resource2.setCpu(100);
        resource2.setRemainCpu(1);
        resource2.setMem(3000);
        resource2.setRemainMem(1130);
        resource2.setPorts(new ArrayList<>());
        resource2.setSystemPorts(new ArrayList<>());

        dao.insert(resource0);
        dao.insert(resource1);
        dao.insert(resource2);
    }

    @Test
    public void test_resource_list_CPU_ASC() throws InterruptedException {
        configService.setDesc(false);
        configService.setTopKMetrics(CPU_METRICS);
        Set<Integer> ports = new HashSet<>();
        ports.add(1);
        List<Resource> resources = resourceDao.list(1, 1, ports, 0, 0, "a", 100, false, 2, null);
        Assert.assertEquals(2, resources.size());
        Assert.assertEquals(IP_2, resources.get(0).getIp());
        Assert.assertEquals(IP_1, resources.get(1).getIp());
    }

    @Test
    public void test_resource_list_MEM_DESC() throws InterruptedException {
        configService.setDesc(true);
        configService.setTopKMetrics(MEM_METRICS);
        Set<Integer> ports = new HashSet<>();
        ports.add(1);
        List<Resource> resources = resourceDao.list(1, 1, ports, 0, 0, "", 100, false, 2, null);
        Assert.assertEquals(2, resources.size());
        Assert.assertEquals(IP_1, resources.get(0).getIp());
        Assert.assertEquals(IP_2, resources.get(1).getIp());
    }

    @Test
    public void test_resource_list_MEM_ASC() throws InterruptedException {
        configService.setDesc(false);
        configService.setTopKMetrics(MEM_METRICS);
        Set<Integer> ports = new HashSet<>();
        ports.add(1);
        List<Resource> resources = resourceDao.list(1, 1, ports, 0, 0, "", 100, false, 2, null);
        Assert.assertEquals(2, resources.size());
        Assert.assertEquals(IP_0, resources.get(0).getIp());
        Assert.assertEquals(IP_2, resources.get(1).getIp());
    }

    @Test
    public void test_resource_list_CPU_MEM_DESC() throws InterruptedException {
        configService.setDesc(true);
        configService.setTopKMetrics(CPU_MEM_METRICS);
        Set<Integer> ports = new HashSet<>();
        ports.add(1);
        List<Resource> resources = resourceDao.list(1, 1, ports, 0, 0, "", 100, false, 2, null);
        Assert.assertEquals(2, resources.size());
        Assert.assertEquals(IP_0, resources.get(0).getIp());
        Assert.assertEquals(IP_1, resources.get(1).getIp());
    }

    @Test
    public void test_resource_list_CPU_DESC() {
        Set<Integer> ports = new HashSet<>();
        ports.add(1);
        List<Resource> resources = resourceDao.list(1, 1, ports, 0, 0, "", 100, false, 2, null);
        Assert.assertEquals(2, resources.size());
        Assert.assertEquals(IP_0, resources.get(0).getIp());
        Assert.assertEquals(IP_1, resources.get(1).getIp());

        resources = resourceDao.list(1, 1, ports, 0, 0, "", 100, false, 1, null);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(IP_0, resources.get(0).getIp());


        Resource resource = new Resource();
        resource.setIp(IP_3);
        resource.setCpu(200);
        resource.setRemainCpu(100);
        resource.setMem(2000);
        resource.setRemainMem(500);
        resource.setPorts(new ArrayList<>());
        resource.setSystemPorts(new ArrayList<>());
        dao.insert(resource);

        resources = resourceDao.list(1, 1, ports, 0, 0, "", 100, false, 1, null);
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(IP_3, resources.get(0).getIp());
    }


    @Test
    public void test1() {
        long a = 100l;
        System.out.println(a);
    }


    @Test
    public void testUpgrade() {
        resourceDao.upgrade(1747L, 2, 100);
    }


    @Test
    public void testList2() {
        ResourceListReq req = new ResourceListReq();
        req.setTargetIp("xxxx");
        List<Resource> list = resourceDao.list(1, 2, Sets.newHashSet(), 778899, 1100, "a", 1000, false, 3, Maps.newHashMap(), req);
        System.out.println("\n\n");
        list.stream().forEach(it -> {
            System.out.println(it + "\n");
        });
    }

    @Test
    public void testGetResourceByEnvId() {
        List<Resource> list = resourceDao.getResourceByEnvId(14);
        System.out.println("\n\n");
        list.stream().forEach(it -> {
            System.out.println(it + "\n");
        });
    }

    @Test
    public void testGetResourceByProjectId() {
        List<Resource> list = resourceDao.getResourceByProjectId(66);
        System.out.println("\n\n");
        list.stream().forEach(it -> {
            System.out.println(it + "\n");
        });
    }


    @Test
    public void testCreate() {
        log.info("{}", dao);
//        dao.create(Quota.class, true);
//        dao.create(QuotaRequest.class, true);
//        dao.create(Resource.class, true);
    }


    @Test
    public void testList() {
        Sql sql = Sqls.create("select * from resource where remain_cpu > @remain_cpu and biz_ids->'$.\"$id\"' != \"1\"");
        sql.vars().set("id", 1);
        sql.params().set("remain_cpu", 0);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Resource.class));
        List<Resource> list = dao.execute(sql).getList(Resource.class);
        System.out.println(list);
    }


    @Test
    public void testTransaction() {
        tDao.testTransaction();
    }


    @Test
    public void testInit() {
        Map<String, Long> map = Maps.newHashMap();
        List<Record> list = dao.query("project_env", null);
        list.stream().forEach(it -> {
//            System.out.println(it.getLong("id")+":"+it.getLong("project_id"));
            map.put(it.getString("id"), it.getLong("project_id"));
        });


        List<Record> rlist = dao.query("resource", null);
        rlist.stream().forEach(it -> {
            Map br = new Gson().fromJson(it.getString("biz_ids"), Map.class);
            Set<String> s = (br.keySet());
//            System.out.println(s);

            Set<Long> pids = s.stream().map(it2 -> map.get(it2)).collect(Collectors.toSet());
            System.out.println(it.get("id") + "+" + pids);


        });

    }


    @Test
    public void testProjectIds() {
        List<Resource> list = dao.query(Resource.class, null);
        list.stream().forEach(it -> {
            System.out.println(it.getProjectIds());

            it.getProjectIds().remove(new Long(53L));
            System.out.println(it.getProjectIds());
//            dao.update(it);

        });
    }


    @Test
    public void testList4() {
        List<Long> l = Lists.newArrayList(1L, 2L);
        long lo = 1L;
        l.remove(lo);
        System.out.println(l);
    }

}
