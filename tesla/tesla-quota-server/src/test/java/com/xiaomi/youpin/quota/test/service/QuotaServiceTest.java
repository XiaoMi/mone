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

package com.xiaomi.youpin.quota.test.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xiaomi.youpin.quota.bo.*;
import com.xiaomi.youpin.quota.dao.ResourceDao;
import com.xiaomi.youpin.quota.service.QuotaService;
import com.xiaomi.youpin.quota.service.ResourceService;
import com.xiaomi.youpin.quota.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class QuotaServiceTest extends BaseTest {


    @Autowired
    private QuotaService quotaService;


    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private ResourceService resourceService;

    @Test
    public void testCreateQuota() {
        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setBizId(145511L);
        quotaInfo.setNum(6);
        quotaInfo.setCpu(2);
        quotaInfo.setMem(30);
        quotaInfo.setProjectId(23333);
        quotaInfo.setPorts(Sets.newHashSet(77788));
        Result<List<ResourceBo>> res = quotaService.createQuota(quotaInfo);
        System.out.println(res);
    }


    @Test
    public void testDestroyQuota() {
        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setBizId(177L);
        quotaService.destoryQuota(quotaInfo);
    }


    @Test
    public void testModifyQuota() {
        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setBizId(1488L);
        quotaInfo.setProjectId(666);
        //增加
        quotaInfo.setNum(4);
        quotaInfo.setCpu(2);
        quotaInfo.setMem(30);
        quotaInfo.setPorts(Sets.newHashSet(777));
        ModifyQuotaRes res = quotaService.modifyQuota(quotaInfo);
        System.out.println(res);
    }


    @Test
    public void testResourceDao() {
        resourceDao.addBizId(1, 14, 4, 100, Sets.newHashSet(809, 4432));
//        resourceDao.removeBizId(1, 13, 4, 100, Sets.newHashSet(80, 443),1);
    }

    @Test
    public void testResourceList() {
        Map<String, String> labels = Maps.newHashMap();
//        labels.put("owner", "1");
        List<Resource> list = resourceDao.list(5, 100, Sets.newHashSet(), 7114L,7114L, "a", 2000,false, 2, labels);
        System.out.println(list);
    }

    @Test
    public void testRemoveQuota() {
        quotaService.removeQuota("127.0.0.12", 1488L, 666);
    }

    @Test
    public void test() {
        ResourceBo resourceBo = new ResourceBo();
        resourceBo.setIp("127.0.0.12");
        resourceBo.setCpu(24);
        List<Integer> list = new ArrayList<>();
        list.add(0);

        resourceBo.setMem(12345678);

        Set<Integer> tmp = new HashSet<>();
        tmp.add(8080);

        resourceService.updateResource(resourceBo);
    }

    @Test
    public void testListC() {
        Result<Map<String, Object>> res = resourceService.list(1,10,0,null);
        System.out.println(res);
    }

}
