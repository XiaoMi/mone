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
import com.xiaomi.youpin.quota.bo.*;
import com.xiaomi.youpin.quota.dao.RecordDao;
import com.xiaomi.youpin.quota.dao.ResourceDao;
import com.xiaomi.youpin.quota.exception.CommonError;
import com.xiaomi.youpin.quota.service.QuotaService;
import com.xiaomi.youpin.quota.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class QuotaCommonTest extends BaseTest {

    @Autowired
    private Dao dao;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RecordDao recordDao;

    private ResourceBo resource0, resource1, resource2;

    private static final String IP_0 = "xxxx";
    private static final String IP_1 = "xxxx";
    private static final String IP_2 = "xxxx";

    private static final long BIZ_ID_0 = 0l;
    private static final int PORT_0 = 0;
    private static final int PORT_1 = 1;


//    @Before
    public void init() {
        dao.create(Quota.class, true);
        dao.create(QuotaRequest.class, true);
        dao.create(Resource.class, true);
        dao.create(Record.class, true);

        resource0 = new ResourceBo();
        resource0.setIp(IP_0);
        resource0.setCpu(100);
        resource0.setMem(2000);

        resource1 = new ResourceBo();
        resource1.setIp(IP_1);
        resource1.setCpu(50);
        resource1.setMem(1000);

        resource2 = new ResourceBo();
        resource2.setIp(IP_2);
        resource2.setCpu(10);
        resource2.setMem(3000);


        resourceService.updateResource(resource0);
        resourceService.updateResource(resource1);
        resourceService.updateResource(resource2);
    }


    @Test
    public void test_drift_valid() {
        QuotaInfo info = new QuotaInfo();
        info.setCpu(50);
        info.setMem(30);
        Set<Integer> ports = new HashSet<>();
        ports.add(PORT_0);
        ports.add(PORT_1);
        info.setPorts(ports);
        info.setBizId(BIZ_ID_0);
        info.setNum(1);

        Result<List<ResourceBo>> result = quotaService.createQuota(info);
        List<ResourceBo> resources = result.getData();
        Assert.assertEquals(1, resources.size());

        String beforeDriftIp = resources.get(0).getIp();
        System.out.println(beforeDriftIp);
        info.setIp(beforeDriftIp);
        Assert.assertTrue(checkResourceHasBiz(beforeDriftIp, info.getBizId()));

        ResourceBo afterDrift = quotaService.drift(info).getData();
        String afterDriftIp = afterDrift.getIp();
        Assert.assertNotEquals(beforeDriftIp, afterDriftIp);

        Assert.assertFalse(checkResourceHasBiz(beforeDriftIp, info.getBizId()));
        Assert.assertTrue(checkResourceHasBiz(afterDriftIp, info.getBizId()));

        Record record = recordDao.getRecord(2);
        Assert.assertEquals(beforeDriftIp, record.getProjectBefore().get(0).getIp());
        Assert.assertEquals(afterDriftIp, record.getProjectAfter().get(0).getIp());
    }

    @Test
    public void test_drift_failedToFindAnotherResource() {
        QuotaInfo info = new QuotaInfo();
        info.setCpu(100);
        info.setMem(30);
        Set<Integer> ports = new HashSet<>();
        ports.add(PORT_0);
        ports.add(PORT_1);
        info.setPorts(ports);
        info.setBizId(BIZ_ID_0);
        info.setNum(1);

        Result<List<ResourceBo>> result = quotaService.createQuota(info);
        List<ResourceBo> resources = result.getData();
        Assert.assertEquals(1, resources.size());

        String beforeDriftIp = resources.get(0).getIp();
        info.setIp(beforeDriftIp);

        Result<ResourceBo> afterDrift = quotaService.drift(info);
        Assert.assertEquals(CommonError.FailedToAddNewQuota.code, afterDrift.getCode());
        System.out.println(afterDrift.getCode());
    }

    @Test
    public void test_offline() {
        QuotaInfo info = new QuotaInfo();
        info.setCpu(100);
        info.setMem(30);
        Set<Integer> ports = new HashSet<>();
        ports.add(PORT_0);
        ports.add(PORT_1);
        info.setPorts(ports);
        info.setBizId(BIZ_ID_0);
        info.setNum(1);

        Result<List<ResourceBo>> result = quotaService.createQuota(info);
        List<ResourceBo> resources = result.getData();
        Assert.assertEquals(1, resources.size());

        String ip = resources.get(0).getIp();
        info.setIp(ip);
        quotaService.offline(info);
        Assert.assertTrue(isResourceOffline(ip));
    }

    private boolean isResourceOffline(String ip) {
        Resource resource = getResource(ip);
        return resource.getStatus() == 1 && resource.getPorts().isEmpty() && resource.getBizIds().isEmpty() &&
            resource.getRemainCpu() == resource.getCpu() && resource.getRemainMem() == resource.getMem();
    }

    private boolean checkResourceHasBiz(String ip, long bizId) {
        Resource resource = getResource(ip);
        return resource.getBizIds().containsKey(bizId);
    }

    private Resource getResource(String ip) {
        return dao.fetch(Resource.class, Cnd.where("ip", "=", ip));
    }

    @Ignore
    @Test
    public void testSort() {
        List<Integer> quotas = Lists.newArrayList(11, 1, 55, 3, 4, 6, 99);
        System.out.println(quotas.stream().sorted(Comparator.reverseOrder()).limit(2).collect(Collectors.toList()));
    }
}
